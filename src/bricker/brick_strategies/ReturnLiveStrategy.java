package bricker.brick_strategies;

import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import gameobjects.Heart;

public class ReturnLiveStrategy implements CollisionStrategy{
    private static final int HEART_VEL = 100;

    private final BrickerGameManager gameManager;
    private final Vector2 windowDimensions;
    private final UserInputListener inputListener;
    private final ImageReader imageReader;
    private final int heartSize;
    private Heart heart;

    public ReturnLiveStrategy (bricker.main.BrickerGameManager gameManager,
                               Vector2 windowDimensions,
                               UserInputListener inputListener,
                               ImageReader imageReader,
                               int heartSize
                               ){
        this.gameManager = gameManager;
        this.windowDimensions = windowDimensions;
        this.inputListener = inputListener;
        this.imageReader = imageReader;
        this.heartSize = heartSize;

    }
    //

    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        System.out.println("life Brick");
        // remove the brick and create a heart in the middle that is going down
        gameManager.removeObject(object1);
        Renderable heartImage = imageReader.readImage("assets/heart.png", false);
        Vector2 size = new Vector2(heartSize, heartSize);
        CollisionStrategy HeartColideStrategy = new HeartColideStrategy(gameManager);
        this.heart = new Heart(Vector2.ZERO, size, heartImage, HeartColideStrategy);
        Vector2 currentPosition = object1.getCenter();
        moveHeart(currentPosition);
    }

    private void moveHeart(Vector2 currentPosition) {
        System.out.println("life createsd");

        float velX = (float)0;
        float velY = (float) HEART_VEL;

        heart.setVelocity(new Vector2(velX, velY));

        // Setting coordinates:
        heart.setCenter(windowDimensions.mult(0.5f));
        heart.setCenter(currentPosition);

        gameManager.addObject(heart);
    }
}
