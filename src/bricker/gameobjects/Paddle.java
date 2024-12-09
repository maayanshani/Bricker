package bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

/**
 * Represents a paddle in the game.
 * The paddle is controlled by user input and can move left or right within the game window.
 * It interacts with other game objects, such as balls and packs, and keeps track of collisions.
 */
public class Paddle extends GameObject {
    private static final float MOVEMENT_SPEED = 300;

    /**
     * The input listener that detects and processes user key presses (e.g., for moving the paddle).
     */
    private UserInputListener inputListener;

    /**
     * The dimensions of the game window, used to constrain the paddle's movement within the window.
     */
    private Vector2 windowDimensions;

    /**
     * The number of collisions this paddle has had with balls or packs.
     */
    private int numBallCollisions;

    /**
     * A boolean flag indicating whether this paddle is an extra paddle (e.g., a power-up).
     */
    private boolean isExtraPaddle;

    /**
     * Non-default Constructor
     *
     * @param topLeftCorner    Position of the object, in window coordinates (pixels).
     *                         Note that (0,0) is the top-left corner of the window.
     * @param dimensions       Width and height in window coordinates.
     * @param renderable       The renderable representing the object. Can be null, in which case
     *                         the GameObject will not be rendered.
     * @param inputListener    The listener that detects user inputs.
     * @param windowDimensions The dimensions of the window.
     * @param isExtraPaddle    A boolean indicating whether this paddle is an extra paddle.
     */
    public Paddle(Vector2 topLeftCorner,
                  Vector2 dimensions, Renderable renderable,
                  UserInputListener inputListener, Vector2 windowDimensions, boolean isExtraPaddle) {
        super(topLeftCorner, dimensions, renderable);
        this.inputListener = inputListener;
        this.windowDimensions = windowDimensions;
        this.numBallCollisions = 0;
        this.isExtraPaddle = isExtraPaddle;
    }

    /**
     * Returns the number of collisions this paddle has had with balls.
     *
     * @return The number of ball collisions.
     */
    public int getNumCollision() {
        return numBallCollisions;
    }

    /**
     * Checks if this paddle is an extra paddle.
     *
     * @return True if this paddle is an extra paddle, false otherwise.
     */
    public boolean isExtraPaddle() {
        return isExtraPaddle;
    }

    /**
     * Updates the paddle's position based on user input.
     * Moves the paddle left or right while ensuring it stays within the window bounds.
     *
     * @param deltaTime The time passed since the last update.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Vector2 movementDirection = Vector2.ZERO;

        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT) &&
                getTopLeftCorner().x() >= 0) {
            movementDirection = movementDirection.add(Vector2.LEFT);
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT) &&
                getTopLeftCorner().x() <= windowDimensions.x() - this.getDimensions().x()) {
            movementDirection = movementDirection.add(Vector2.RIGHT);
        }

        setVelocity(movementDirection.mult(MOVEMENT_SPEED));
    }

    /**
     * Called when a collision occurs with another GameObject.
     * Increments the BallCollisions count if the other object is a ball or pack.
     *
     * @param other     The GameObject with which a collision occurred.
     * @param collision Information regarding the collision.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if ("Ball".equals(other.getTag()) || "Pack".equals(other.getTag())) {
            numBallCollisions++;
        }
    }
}
