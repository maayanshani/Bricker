package bricker.gameobjects;

import bricker.brick_strategies.CollisionStrategy;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents a heart object in the game, typically used for tracking lives or providing bonuses.
 * The heart interacts with other game objects based on defined collision logic.
 */
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

    /**
     * Constructs a new Heart object with a collision strategy.
     *
     * @param topLeftCorner     Position of the heart in window coordinates (pixels).
     *                          Note that (0,0) is the top-left corner of the window.
     * @param dimensions        Width and height of the heart in window coordinates.
     * @param renderable        The renderable representing the heart icon.
     * @param collisionStrategy The collision strategy to execute when the heart collides with another object.
     */
    public Heart(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, CollisionStrategy collisionStrategy) {
        super(topLeftCorner, dimensions, renderable);
        this.collisionStrategy = collisionStrategy;
    }

    /**
     * Handles the logic when the heart collides with another object.
     *
     * @param other     The other game object involved in the collision.
     * @param collision The collision details between the heart and the other object.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        this.collisionStrategy.onCollision(this, other);

    }

    /**
     * Determines whether the heart should collide with another game object.
     *
     * @param other The other game object.
     * @return True if the other object is an instance of Paddle and is not an extra paddle, false otherwise.
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        return other instanceof Paddle && !((Paddle) other).isExtraPaddle();
    }
}
