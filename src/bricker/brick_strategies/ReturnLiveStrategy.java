package bricker.brick_strategies;

import bricker.heartStrategy.HeartCollideStrategy;
import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import gameobjects.Brick;
import gameobjects.Heart;

/**
 * A collision strategy that removes a brick and creates a heart object in the game.
 * The heart represents an extra life and moves downwards after being created.
 */
public class ReturnLiveStrategy implements CollisionStrategy {

    /**
     * The velocity of the heart object when it is created.
     */
    private static final int HEART_VEL = 100;

    /**
     * The game manager that manages the game logic and state.
     */
    private final BrickerGameManager gameManager;

    /**
     * The dimensions of the game window, used for positioning the heart.
     */
    private final Vector2 windowDimensions;

    /**
     * The image reader used to load images for the heart object.
     */
    private final ImageReader imageReader;

    /**
     * The size of the heart object.
     */
    private final int heartSize;

    /**
     * The heart object created during a collision.
     */
    private Heart heart;

    /**
     * Constructs a new ReturnLiveStrategy.
     *
     * @param gameManager      The game manager that handles game logic and state.
     * @param windowDimensions The dimensions of the game window.
     * @param imageReader      The image reader used to load heart images.
     * @param heartSize        The size of the heart object.
     */
    public ReturnLiveStrategy(bricker.main.BrickerGameManager gameManager,
                              Vector2 windowDimensions,
                              ImageReader imageReader,
                              int heartSize) {
        this.gameManager = gameManager;
        this.windowDimensions = windowDimensions;
        this.imageReader = imageReader;
        this.heartSize = heartSize;
    }

    /**
     * Handles the collision between two GameObjects.
     * Removes the brick from the game and creates a heart object that moves downwards.
     *
     * @param object1 The first GameObject involved in the collision, expected to be a brick.
     * @param object2 The second GameObject involved in the collision.
     */
    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        // Remove the brick and create a heart in the middle that moves downwards
        gameManager.removeBrick((Brick) object1);
        Renderable heartImage = imageReader.readImage("assets/heart.png", false);
        Vector2 size = new Vector2(heartSize, heartSize);
        CollisionStrategy HeartColideStrategy = new HeartCollideStrategy(gameManager);
        this.heart = new Heart(Vector2.ZERO, size, heartImage, HeartColideStrategy);
        this.heart.setTag("Heart");
        Vector2 currentPosition = object1.getCenter();
        moveHeart(currentPosition);
    }

    /**
     * Moves the heart object by setting its velocity and position.
     * The heart moves downwards after being created.
     *
     * @param currentPosition The position where the heart will be placed.
     */
    private void moveHeart(Vector2 currentPosition) {
        float velX = 0;
        float velY = HEART_VEL;

        heart.setVelocity(new Vector2(velX, velY));

        // Set coordinates
        heart.setCenter(windowDimensions.mult(0.5f));
        heart.setCenter(currentPosition);

        gameManager.addHeart(heart);
    }
}
