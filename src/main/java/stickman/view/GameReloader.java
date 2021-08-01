package stickman.view;

import stickman.model.GameMemento;

public class GameReloader {

    GameMemento game;

    public void setMemento(GameMemento game) {
        this.game = game;
    }

    public GameMemento getMemento() {
        if(this.game == null){
            return null;
        }
        return this.game;
    }
}
