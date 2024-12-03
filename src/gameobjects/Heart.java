package gameobjects;

import bricker.brick_strategies.CollisionStrategy;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

public class Heart extends GameObject {

    private final CollisionStrategy collisionStrategy;

    /**
     * Construct a new Heart object.
     *
     * @param topLeftCorner Position of the heart, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the heart icon.
     */
    public Heart(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
        this.collisionStrategy = null;
    }

    public Heart(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, CollisionStrategy collisionStrategy) {
        super(topLeftCorner, dimensions, renderable);
        this.collisionStrategy = collisionStrategy;
    }

    // todo: should colide only with paddle
    // todo: how to add extra life?

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        this.collisionStrategy.onCollision(this, other);

    }

    @Override
    public boolean shouldCollideWith(GameObject other) {
        return other instanceof Paddle && !((Paddle) other).isExtraPaddle();
    }
}
