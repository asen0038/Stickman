package stickman.model;

import stickman.level.Level;

public class GameMemento {

    private Level level;
    private long totalScore;
    private int currNumLvl;
    private double yPos;

    public GameMemento(Level level, long totalScore, int currNumLvl, double yPos) {
        this.level = level;
        this.totalScore = totalScore;
        this.currNumLvl = currNumLvl;
        this.yPos = yPos;
    }

    public Level getLevel() {
        return level;
    }

    public long getTotalScore() {
        return totalScore;
    }

    public int getCurrNumLvl() {
        return currNumLvl;
    }

    public double getyPos() {
        return yPos;
    }
}
