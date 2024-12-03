package bricker.brick_strategies;

import danogl.GameObject;
import danogl.gui.ImageReader;
import danogl.gui.Sound;
import danogl.gui.SoundReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import gameobjects.Brick;
import gameobjects.Pack;

import java.util.Random;

public class ExstraPackStrategy implements CollisionStrategy {
    /**
     * The game manager that manages the game logic, including removing bricks and adding packs.
     */
    private bricker.main.BrickerGameManager gameManager;

    /**
     * The pack object that is created upon collision with a brick.
     */
    private Pack pack;

    /**
     * The speed at which the pack will move.
     */
    private final float ballSpeed;

    /**
     * The radius of the pack, which is smaller than the ball's radius.
     */
    private final float ballRadius;

    /**
     * The image reader used to load images for the pack.
     */
    private ImageReader imageReader;

    /**
     * The sound reader used to load sound for the pack's collision.
     */
    private SoundReader soundReader;

    /**
     * Constructs an instance of ExstraPackStrategy to manage the creation of packs after a collision.
     *
     * @param gameManager   The game manager that handles game logic.
     * @param ballSpeed     The speed at which the pack will move.
     * @param windowDimensions The dimensions of the game window.
     * @param ballRadius    The radius of the ball, used to scale the pack radius.
     * @param imageReader   The image reader used to load pack images.
     * @param soundReader   The sound reader used to load sounds for the pack.
     */
    public ExstraPackStrategy(bricker.main.BrickerGameManager gameManager,
                              float ballSpeed, Vector2 windowDimensions,
                              float ballRadius, ImageReader imageReader, SoundReader soundReader) {

        this.gameManager = gameManager;
        this.ballSpeed = ballSpeed;
        this.ballRadius = ballRadius;
        this.imageReader = imageReader;
        this.soundReader = soundReader;
    }

    /**
     * Handles the collision between two GameObjects. In this case, it removes the brick
     * from the game upon collision and add two Packs.
     *
     * @param object1 The first GameObject involved in the collision.
     * @param object2 The second GameObject involved in the collision.
     */
    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        // create in the place of the brick two Packs:
        // remove brick:
        gameManager.removeBrick((Brick)object1);
        // create 2 Packs:
        for (int i = 0; i < 2; i++) {
            Renderable packImage = imageReader.readImage("assets/mockBall.png", true);
            Sound collisionSound = soundReader.readSound("assets/blop.wav");
            pack = new Pack(Vector2.ZERO,
                    new Vector2(ballRadius, ballRadius),
                    packImage,
                    collisionSound);
            pack.setTag("Pack");
            Vector2 currentPosition = object1.getCenter();
            resetPack(currentPosition);
        }
    }

    /**
     * Resets the pack's position and velocity when a brick is destroyed.
     *
     * @param currentPosition The position where the pack will be placed.
     */
    private void resetPack(Vector2 currentPosition) {
        Random rand = new Random();

        // Setting velocity:
        float angle = rand.nextFloat();
        float velX = (float)Math.cos(angle) * ballSpeed;
        float velY = (float)Math.sin(angle) * ballSpeed;

        pack.setVelocity(new Vector2(velX, velY));

        // Setting coordinates:
        pack.setCenter(currentPosition);

        gameManager.addPack(pack);
    }
}
