package stickman.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import stickman.entity.moving.player.Controllable;
import stickman.entity.still.Over;
import stickman.entity.still.Win;
import stickman.level.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of GameEngine. Manages the running of the game.
 */
public class GameManager implements GameEngine {

    /**
     * The current level
     */
    private Level level;
    private long totalScore; //The combined score from previous levels
    /**
     * List of all level files
     */
    private List<String> levelFileNames;
    private int numLvls;
    private int currNumLvl;

    /**
     * Creates a GameManager object.
     * @param levels The config file containing the names of all the levels
     */
    public GameManager(String levels) {
        this.levelFileNames = this.readConfigFile(levels);
        this.numLvls = this.levelFileNames.size();
        this.currNumLvl = 0;
        this.totalScore = 0;

        this.level = LevelBuilderImpl.generateFromFile(levelFileNames.get(this.currNumLvl), this);
        this.currNumLvl++;
    }


    @Override
    public void nextLevel(){
        this.totalScore += this.level.getScore();
        long lives = this.level.getLives();
        if(this.currNumLvl < this.numLvls){//Only transit to levels that exist in levels folder
            this.level = LevelBuilderImpl.generateFromFile(levelFileNames.get(this.currNumLvl), this);
            this.level.setLives(lives); //lives are carried on
            this.currNumLvl++;
        }else{
            this.level.getEntities().add(new Win(this.level.getHeroX() - 200, this.level.getHeroY() - 200));
        }
    }

    @Override
    public long getTotalScore() {
        return this.totalScore;
    }

    @Override
    public GameMemento saveMemento() {
        return new GameMemento(this.level.copy(), this.totalScore, this.currNumLvl, this.level.getHeroY());
    }

    @Override
    public void loadMemento(GameMemento game) {

        if(game == null){
            return;
        }

        boolean up = false;
        if(this.level.getHero().upgraded()){
            up = true;
        }

        boolean left = this.level.getHero().isLeftFacing();

        //reloaded here
        this.level = game.getLevel();
        this.level.setModel(this);
        this.level.getHero().setY(game.getyPos());
        this.level.getHero().setLeft(!left);
        this.level.getHero().setUpgrade(up);

        this.totalScore = game.getTotalScore();
        this.currNumLvl = game.getCurrNumLvl();
    }

    @Override
    public Level getCurrentLevel() {
        return this.level;
    }

    @Override
    public boolean jump() {
        return this.level.jump();
    }

    @Override
    public boolean moveLeft() {
        return this.level.moveLeft();
    }

    @Override
    public boolean moveRight() {
        return this.level.moveRight();
    }

    @Override
    public boolean stopMoving() {
        return this.level.stopMoving();
    }

    @Override
    public void tick() {
        this.level.tick();
    }

    @Override
    public void shoot() {
        this.level.shoot();
    }

    @Override
    public void reset() {
        long score = this.level.getScore();
        long lives = this.level.getLives();
        this.level = LevelBuilderImpl.generateFromFile(this.level.getSource(), this);
        this.level.setScore(score); //Score does not change after reset
        this.level.setLives(lives); //Lives does not change after reset
    }

    @Override
    public void endGame() {
        this.totalScore += this.level.getScore();
        this.level.getEntities().add(new Over(this.level.getHeroX() - 200, this.level.getHeroY() - 200));
    }

    /**
     * Retrieves the list of level filenames from a config file
     * @param config The config file
     * @return The list of level names
     */
    @SuppressWarnings("unchecked")
    private List<String> readConfigFile(String config) {

        List<String> res = new ArrayList<String>();

        JSONParser parser = new JSONParser();

        try {

            Reader reader = new FileReader(config);

            JSONObject object = (JSONObject) parser.parse(reader);

            JSONArray levelFiles = (JSONArray) object.get("levelFiles");

            Iterator<String> iterator = (Iterator<String>) levelFiles.iterator();

            // Get level file names
            while (iterator.hasNext()) {
                String file = iterator.next();
                res.add("levels/" + file);
            }

        } catch (IOException e) {
            System.exit(10);
            return null;
        } catch (ParseException e) {
            return null;
        }

        return res;
    }

}