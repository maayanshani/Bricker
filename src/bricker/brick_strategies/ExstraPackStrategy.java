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

/**
 * A collision strategy that removes a brick from the game upon collision and creates
 * a specified number of "Pack" objects (e.g., power-ups or bonuses) at the brick's position.
 */
public class ExstraPackStrategy implements CollisionStrategy {

    /**
     * The number of packs to create for each brick collision.
     */
    private static final int NUM_PACKS_PER_BRICK = 2;

    /**
     * The game manager that manages the game logic, including removing bricks and adding packs.
     */
    private bricker.main.BrickerGameManager gameManager;

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
     * @param imageReader   The image reader used to load pack images.
     * @param soundReader   The sound reader used to load sounds for the pack.
     * @param ballSpeed     The speed at which the pack will move.
     * @param ballRadius    The radius of the ball, used to scale the pack radius.
     */
    public ExstraPackStrategy(bricker.main.BrickerGameManager gameManager,
                              ImageReader imageReader,
                              SoundReader soundReader,
                              float ballSpeed,
                              float ballRadius) {

        this.gameManager = gameManager;
        this.imageReader = imageReader;
        this.soundReader = soundReader;
        this.ballSpeed = ballSpeed;
        this.ballRadius = ballRadius;
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
        // remove brick:
        gameManager.removeBrick((Brick)object1);

        // create in the place of the brick two Packs:
        for (int i = 0; i < NUM_PACKS_PER_BRICK; i++) {
            Renderable packImage = imageReader.readImage("assets/mockBall.png", true);
            Sound collisionSound = soundReader.readSound("assets/blop.wav");
            Pack pack = new Pack(Vector2.ZERO,
                    new Vector2(ballRadius, ballRadius),
                    packImage,
                    collisionSound);
            pack.setTag("Pack");
            Vector2 currentPosition = object1.getCenter();
            resetPack(pack, currentPosition);
        }
    }

    /**
     * Resets the pack's position and velocity when a brick is destroyed.
     *
     * @param currentPosition The position where the pack will be placed.
     */
    private void resetPack(Pack pack, Vector2 currentPosition) {
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
