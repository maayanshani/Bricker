package gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents a ball in the game.
 * The ball interacts with other game objects, and its behavior is defined upon collisions.
 * It keeps track of the number of collisions and plays a sound when a collision occurs.
 */
public class Ball extends GameObject {
    /**
     * The sound to be played when a collision occurs.
     */
    private final Sound collisionSound;

    /**
     * The number of collisions the ball has experienced
     */
    private int collisionCounter;

    /**
     * Constructs a new Ball instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     * @param collisionSound The sound to be played when a collision occurs.
     */
    public Ball(Vector2 topLeftCorner,
                Vector2 dimensions,
                Renderable renderable,
                Sound collisionSound) {
        super(topLeftCorner, dimensions, renderable);
        this.collisionSound = collisionSound;;
    }

    /**
     * Called when the ball collides with another GameObject.
     * Reverses the velocity of the ball based on the collision normal and plays a collision sound.
     *
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     */
    @Override
    public void onCollisionEnter(GameObject other,
                                 Collision collision) {
        super.onCollisionEnter(other, collision);
        Vector2 newVel = getVelocity().flipped(collision.getNormal());
        setVelocity(newVel);
        collisionSound.play();
        collisionCounter++;
    }

    /**
     * Gets the number of times the ball has collided.
     *
     * @return The number of collisions the ball has experienced.
     */
    public int getCollisionCounter() {
        return collisionCounter;
    }
}
