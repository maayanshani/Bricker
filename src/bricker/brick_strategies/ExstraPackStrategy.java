package bricker.brick_strategies;

import danogl.GameObject;
import danogl.gui.ImageReader;
import danogl.gui.Sound;
import danogl.gui.SoundReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import gameobjects.Ball;
import gameobjects.Brick;
import gameobjects.Pack;

import java.util.Random;

public class ExstraPackStrategy implements CollisionStrategy{
    private bricker.main.BrickerGameManager gameManager;
    private Pack pack;
    private final float packSpeed;
    private final float packRadius;
    private ImageReader imageReader;
    private SoundReader soundReader;

    public ExstraPackStrategy(bricker.main.BrickerGameManager gameManager,
                              float ballSpeed, Vector2 windowDimensions,
                              float ballRadius, ImageReader imageReader, SoundReader soundReader) {

        this.gameManager = gameManager;
        this.packSpeed = ballSpeed;
        this.packRadius = ballRadius * 0.75f;
        this.imageReader = imageReader;
        this.soundReader = soundReader;
    }

    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        // create in the place of the brick to Packs:
        // remove brick:
        gameManager.removeBrick((Brick)object1);
        // create 2 Packs:
        for (int i = 0; i < 2; i++) {
            Renderable packImage = imageReader.readImage("assets/mockBall.png", true);
            Sound collisionSound = soundReader.readSound("assets/blop.wav");
            pack = new Pack(Vector2.ZERO,
                    new Vector2(packRadius, packRadius),
                    packImage,
                    collisionSound);
            pack.setTag("Pack");
            Vector2 currentPosition = object1.getCenter();
            resetPack(currentPosition);
        }
    }


    private void resetPack(Vector2 currentPosition) {
        Random rand = new Random();

        // Setting velocity:
        float angle = rand.nextFloat();
        float velX = (float)Math.cos(angle) * packSpeed;
        float velY = (float)Math.sin(angle) * packSpeed;

        pack.setVelocity(new Vector2(velX, velY));

        // Setting coordinates:
        pack.setCenter(currentPosition);

        gameManager.addPack(pack);
    }

}
