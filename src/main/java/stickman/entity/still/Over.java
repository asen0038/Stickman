package stickman.entity.still;

import stickman.entity.Entity;
import stickman.entity.GameObject;

public class Over extends GameObject {

    /**
     * Constructs a GameObject object.
     * @param x         The x-coordinate.
     * @param y         The y-coordinate.
     */
    public Over(double x, double y) {
        super("over.png", x, y, 200, 200, Layer.EFFECT);
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public Entity copy(){
        return new Over(this.xPos, this.yPos);
    }

}
