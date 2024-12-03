package gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

public class Pack extends Ball{
    /**
     * Non-default Constructor
     *
     * @param topLeftCorner  Position of the object, in window coordinates (pixels).
     *                       Note that (0,0) is the top-left corner of the window.
     * @param dimensions     Width and height in window coordinates.
     * @param renderable     The renderable representing the object. Can be null, in which case
     *                       the GameObject will not be rendered.
     * @param collisionSound The sound to be played when a collision occurs.
     */
    public Pack(Vector2 topLeftCorner,
                Vector2 dimensions,
                Renderable renderable,
                Sound collisionSound) {
        super(topLeftCorner, dimensions, renderable, collisionSound);
        this.setDimensions(dimensions.mult(0.75f));
    }

}
