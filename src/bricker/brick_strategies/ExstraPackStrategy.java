package bricker.brick_strategies;

import danogl.GameObject;
import danogl.gui.ImageReader;
import danogl.gui.Sound;
import danogl.gui.SoundReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import gameobjects.Ball;
import gameobjects.Pack;

import java.util.Random;

public class ExstraPackStrategy implements CollisionStrategy{
    private bricker.main.BrickerGameManager gameManager;
    private Pack pack;
    private final float packSpeed;
    private final Vector2 windowDimensions;
    private final float packRadius;
    private ImageReader imageReader;
    private SoundReader soundReader;

    public ExstraPackStrategy(bricker.main.BrickerGameManager gameManager,
                              float packSpeed, Vector2 windowDimensions,
                              float packRadius, ImageReader imageReader, SoundReader soundReader) {

        this.gameManager = gameManager;
        this.packSpeed = packSpeed;
        this.windowDimensions = windowDimensions;
        this.packRadius = packRadius;
        this.imageReader = imageReader;
        this.soundReader = soundReader;
    }

    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        // create in the place of the brick to Packs:
        // remove brick:
        gameManager.removeObject(object1);
        // create 2 Packs:
        for (int i = 0; i < 2; i++) {
            Renderable packImage = imageReader.readImage("assets/mockBall.png", true);
            Sound collisionSound = soundReader.readSound("assets/blop.wav");
            pack = new Pack(Vector2.ZERO,
                    new Vector2(packRadius, packRadius),
                    packImage,
                    collisionSound);
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
        pack.setCenter(windowDimensions.mult(0.5f));
        pack.setCenter(currentPosition);

        gameManager.addObject(pack);

    }

}
