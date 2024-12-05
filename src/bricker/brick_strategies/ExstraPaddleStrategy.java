package bricker.brick_strategies;

import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import bricker.gameobjects.Brick;
import bricker.gameobjects.Paddle;

/**
 * A collision strategy that removes a brick from the game upon collision and creates
 * an additional paddle at the center of the game window.
 * This paddle is controlled by the user and is flagged as an extra paddle.
 */
public class ExstraPaddleStrategy implements CollisionStrategy {

    /**
     * The game manager that manages the game logic, including removing bricks and adding paddles.
     */
    private final BrickerGameManager gameManager;

    /**
     * The width of the paddle that is created after a collision.
     */
    private final int paddleWidth;

    /**
     * The height of the paddle that is created after a collision.
     */
    private final int paddleHeight;

    /**
     * The dimensions of the game window.
     */
    private final Vector2 windowDimensions;

    /**
     * The user input listener used to control the newly created paddle.
     */
    private final UserInputListener inputListener;

    /**
     * The image reader used to load images for the paddle.
     */
    private final ImageReader imageReader;

    /**
     * Non-default Constructor
     *
     * @param gameManager     The game manager that handles game logic.
     * @param paddleWidth     The width of the new paddle.
     * @param paddleHeight    The height of the new paddle.
     * @param windowDimensions The dimensions of the game window.
     * @param inputListener   The input listener used to control the paddle.
     * @param imageReader     The image reader used to load paddle images.
     */
    public ExstraPaddleStrategy(bricker.main.BrickerGameManager gameManager,
                                Vector2 windowDimensions,
                                UserInputListener inputListener,
                                ImageReader imageReader,
                                int paddleWidth,
                                int paddleHeight) {
        this.gameManager = gameManager;
        this.paddleWidth = paddleWidth;
        this.paddleHeight = paddleHeight;
        this.windowDimensions = windowDimensions;
        this.inputListener = inputListener;
        this.imageReader = imageReader;
    }

    /**
     * Handles the collision between two GameObjects. In this case, it removes the brick
     * from the game upon collision and create another paddle.
     *
     * @param object1 The first GameObject involved in the collision.
     * @param object2 The second GameObject involved in the collision.
     */
    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        // remove the brick:
        gameManager.removeBrick((Brick)object1);

        // create another paddle:
        Renderable paddleImage = imageReader.readImage("assets/paddle.png", true);
        Paddle paddle = new Paddle(
                Vector2.ZERO,
                new Vector2(paddleWidth, paddleHeight),
                paddleImage,
                inputListener,
                windowDimensions,
                true);
        paddle.setTag("Paddle");
        paddle.setCenter(
                new Vector2(windowDimensions.x()/2, (int) (windowDimensions.y()/2)));
        gameManager.addPaddle(paddle);
    }
}
