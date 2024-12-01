package bricker.brick_strategies;

import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import gameobjects.Paddle;

public class ExstraPaddleStrategy implements CollisionStrategy{
    private final BrickerGameManager gameManager;
    private final int paddleWidth;
    private final int paddleHeight;
    private final Vector2 windowDimensions;
    private final UserInputListener inputListener;
    private final ImageReader imageReader;

    public ExstraPaddleStrategy(bricker.main.BrickerGameManager gameManager,
                                int paddleWidth, int paddleHeight,
                                Vector2 windowDimensions,
                                UserInputListener inputListener,
                                ImageReader imageReader){
        this.gameManager = gameManager;
        this.paddleWidth = paddleWidth;
        this.paddleHeight = paddleHeight;
        this.windowDimensions = windowDimensions;
        this.inputListener = inputListener;
        this.imageReader = imageReader;
    }

    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        // remove the brick:
        gameManager.removeObject(object1);

        // create another paddle:
        Renderable paddleImage = imageReader.readImage("assets/paddle.png", true);
        Paddle paddle = new Paddle(
                Vector2.ZERO,
                new Vector2(paddleWidth, paddleHeight),
                paddleImage,
                inputListener,
                windowDimensions,
                true);
        paddle.setCenter(
                new Vector2(windowDimensions.x()/2, (int) (windowDimensions.y()/2)));
        gameManager.addObject(paddle);

    }
}
