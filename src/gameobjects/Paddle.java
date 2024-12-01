package gameobjects;

import danogl.GameObject;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

public class Paddle extends GameObject {
    private static final float MOVEMENT_SPEED = 300;
    private UserInputListener inputListener;
    private Vector2 windowDimensions;
    private int numCollision;
    private boolean isExtraPaddle;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner    Position of the object, in window coordinates (pixels).
     *                         Note that (0,0) is the top-left corner of the window.
     * @param dimensions       Width and height in window coordinates.
     * @param renderable       The renderable representing the object. Can be null, in which case
     *                         the GameObject will not be rendered.
     * @param inputListener
     * @param windowDimensions
     */
    public Paddle(Vector2 topLeftCorner,
                      Vector2 dimensions, Renderable renderable,
                      UserInputListener inputListener, Vector2 windowDimensions, boolean isExtraPaddle) {
        super(topLeftCorner, dimensions, renderable);
        this.inputListener = inputListener;
        this.windowDimensions = windowDimensions;
        this.numCollision = 0;
        this.isExtraPaddle = isExtraPaddle;
    }

    // TODO: not in the OG API, needed to be explaind in the README:
    public int getNumCollision() {
        return numCollision;
    }

    // TODO: not in the OG API, needed to be explaind in the README:
    public void addCollision() {
        this.numCollision++;
    }

    // TODO: not in the OG API, needed to be explaind in the README:
    public boolean isExtraPaddle() {
        return isExtraPaddle;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Vector2 movementDirection = Vector2.ZERO;

        // TODO: didnt use setTopLeft, just didnt allow the update
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
}
