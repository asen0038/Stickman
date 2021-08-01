package stickman.level;

import stickman.entity.*;
import stickman.entity.moving.MovingEntity;
import stickman.entity.moving.enemy.Slime;
import stickman.entity.moving.other.Bullet;
import stickman.entity.moving.other.Projectile;
import stickman.entity.moving.player.Controllable;
import stickman.entity.moving.player.StickMan;
import stickman.entity.still.Flag;
import stickman.entity.still.Mushroom;
import stickman.entity.still.Platform;
import stickman.entity.still.Win;
import stickman.model.GameEngine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the Level interface. Manages the running of
 * the level and all the entities within it.
 */
public class LevelManager implements Level {

    /**
     * The player character.
     */
    private Controllable hero;

    /**
     * A list of all the entities in the level.
     */
    private List<Entity> entities;

    /**
     * A list of all the moving entities in the level.
     */
    private List<MovingEntity> movingEntities;

    /**
     * A list of all the entities that can interact with the player.
     */
    private List<Interactable> interactables;

    /**
     * A list of all the projectiles (bullets) in the level.
     */
    private List<Projectile> projectiles;

    /**
     * The height of the level.
     */
    private double height;

    /**
     * The width of the level.
     */
    private double width;

    /**
     * The height of the floor in the level.
     */
    private double floorHeight;

    /**
     * Whether the entities should update, or the player has reached the flag.
     */
    private boolean active;

    /**
     * The name of the file the level is from.
     */
    private String filename;

    /**
     * The GameEngine the level is running inside of.
     */
    private GameEngine model;
    private long time;
    private long score; //The score obtained in this level
    private long lives;
    private int tickCount = 0;
    private String hs;

    /**
     * Creates a new LevelManager object.
     * @param model The GameEngine the level is in
     * @param filename The file the level is based off of
     * @param height The height of the level
     * @param width The width of the level
     * @param floorHeight The height of the floor
     * @param heroX The starting x of the hero
     * @param heroSize The size of the hero
     * @param entities The list of entities in the level
     * @param movingEntities The list of moving entities in the level
     * @param interactables The list of entities that can interact with the hero in the level
     * @param time The time this level will count down from
     * @param lives The lives given to hero for the current level
     */
    public LevelManager(GameEngine model, String filename, double height, double width, double floorHeight, double heroX, String heroSize,
                        List<Entity> entities, List<MovingEntity> movingEntities, List<Interactable> interactables,
                        long time, long lives) {
        this.model = model;
        this.filename = filename;
        this.height = height;
        this.width = width;
        this.floorHeight = floorHeight;
        this.entities = entities;
        this.movingEntities = movingEntities;
        this.interactables = interactables;
        this.time = time;
        this.lives = lives;

        this.score = time;

        this.projectiles = new ArrayList<>();

        this.hs = heroSize;

        // Create new hero
        this.hero = new StickMan(heroX, floorHeight, heroSize, this);
        this.movingEntities.add(this.hero);

        // Ensure entities has all entities (including moving ones)
        this.entities.addAll(movingEntities);
        this.entities = new ArrayList<>(new HashSet<>(entities));

        this.active = true;
    }

    @Override
    public List<Entity> getEntities() {
        return this.entities;
    }

    @Override
    public double getHeight() {
        return this.height;
    }

    @Override
    public double getWidth() {
        return this.width;
    }

    @Override
    public void tick() {

        tickCount++;

        if (!active) {
            return;
        }

        for (MovingEntity entity : this.movingEntities) {
            entity.tick(this.entities, this.hero.getXPos(), this.floorHeight);
        }

        this.manageCollisions();

        // Remove inactive entities
        this.clearOutInactive();

        //check time and manage scores
        if(tickCount % 120 == 0){
            if(this.time <= 0){
                this.time = 0;
                if(this.score <= 0){
                    this.score = 0;
                }else{
                    this.score--;
                }
            }else{
                this.time--;
                this.score--;
            }
        }
    }

    /**
     * Removes inactive entities from all the lists.
     */
    private void clearOutInactive() {
        for(Entity en : this.entities){//updates score when enemy is killed
            if(en instanceof Slime && !en.isActive()){
                this.score += 100;
                break;
            }
        }
        this.entities.removeIf(x -> !x.isActive());
        this.movingEntities.removeIf(x -> !this.entities.contains(x));
        this.interactables.removeIf(x -> !this.entities.contains(x));
        this.projectiles.removeIf(x -> !this.entities.contains(x));
    }

    /**
     * Calls interact methods on interactables and projectiles.
     */
    private void manageCollisions() {

        if (!entities.contains(this.hero)) {
            return;
        }

        // Collision between hero and other entity
        for (Interactable interactable : this.interactables) {
            if (interactable.checkCollide(hero)) {
                interactable.interact(hero);
            }
        }

        // Collision between bullet and moving entity (not hero)
        for (Projectile projectile : this.projectiles) {
            projectile.movingCollision(this.movingEntities.stream().filter(x -> x != hero).collect(Collectors.toList()));
        }

        // Collision between bullet and other entity
        for (Projectile projectile : this.projectiles) {
            projectile.staticCollision(this.entities.stream().filter(x -> x != hero).collect(Collectors.toList()));
        }
    }

    @Override
    public double getFloorHeight() {
        return this.floorHeight;
    }

    @Override
    public double getHeroX() {
        return this.hero.getXPos();
    }

    @Override
    public double getHeroY() {
        return this.hero.getYPos();
    }

    @Override
    public boolean jump() {
        if (!active) {
            return false;
        }
        return this.hero.jump();
    }

    @Override
    public boolean moveLeft() {
        if (!active) {
            return false;
        }
        return this.hero.moveLeft();
    }

    @Override
    public boolean moveRight() {
        if (!active) {
            return false;
        }
        return this.hero.moveRight();
    }

    @Override
    public boolean stopMoving() {
        if (!active) {
            return false;
        }
        return this.hero.stop();
    }

    @Override
    public void reset() {
        if (this.model != null) {
            this.lives--;
            if(this.lives == 0){
                this.active = false;
                this.model.endGame();
            }else{
                this.model.reset();
            }
        }
    }

    @Override
    public void shoot() {
        if (!this.hero.upgraded() || !active) {
            return;
        }

        double x = this.hero.getXPos() + this.hero.getWidth();

        if (this.hero.isLeftFacing()) {
            x = this.hero.getXPos();
        }

        Projectile bullet = new Bullet(x, this.hero.getYPos() + (2 * this.hero.getWidth() / 3), this.hero.isLeftFacing());

        this.entities.add(bullet);
        this.movingEntities.add(bullet);
        this.projectiles.add(bullet);
    }

    @Override
    public String getSource() {
        return this.filename;
    }

    @Override
    public void win() {
        this.active = false;
        this.model.nextLevel();
    }

    @Override
    public long getTime() {
        return this.time;
    }

    @Override
    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public long getScore() {
        return this.score;
    }

    @Override
    public void setScore(long s) {
        this.score = s;
    }

    @Override
    public void addScore(long s) {
        this.score += s;
    }

    @Override
    public long getLives() {
        return this.lives;
    }

    @Override
    public void setLives(long l) {
        this.lives = l;
    }

    @Override
    public  List<Projectile> getProj(){
        return this.projectiles;
    }

    @Override
    public List<MovingEntity> getMoveEnt(){
        return this.movingEntities;
    }

    @Override
    public Controllable getHero() {
        return this.hero;
    }

    @Override
    public void setModel(GameEngine model) {
        this.model = model;
    }

    @Override
    public void setHero(Controllable hero){
        this.hero = hero;
    }

    @Override
    public Level copy(){

        List<Entity> e = new ArrayList<>();
        List<MovingEntity> me = new ArrayList<>();
        List<Interactable> ie = new ArrayList<>();
        for(Entity entity : this.entities){
            if(!(entity instanceof StickMan)){ //Stickman not added
                e.add(entity.copy());
            }
        }

        //slime copy
        for(Entity a : e){
            if(a instanceof Slime){
               me.add((MovingEntity) a);
               ie.add((Interactable) a);
            }
        }

        //flag copy
        for(Entity a : e){
            if(a instanceof Flag){
                ie.add((Interactable) a);
            }
        }

        //mushroom copy
        for(Entity a : e){
            if(a instanceof Mushroom){
                ie.add((Interactable) a);
            }
        }

        //level copy
        Level l = new LevelManager(this.model, this.filename, this.height, this.width, this.floorHeight, this.getHeroX(),
                this.hs, e, me, ie, this.time, this.lives);

        //bullet copy
        for(Entity a : e){
            if(a instanceof Bullet){
                l.getProj().add((Projectile) a);
                l.getMoveEnt().add((MovingEntity) a);
            }
        }

        //set score
        l.setScore(this.score);

        return l;
    }

}
