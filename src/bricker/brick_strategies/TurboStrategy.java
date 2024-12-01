package bricker.brick_strategies;

import bricker.main.BrickerGameManager;
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

public class TurboStrategy implements CollisionStrategy{
    private BrickerGameManager gameManager;
    private ImageReader imageReader;
    private SoundReader soundReader;
    private Ball ball;

    public TurboStrategy(BrickerGameManager gameManager,
                         ImageReader imageReader, Ball ball) {
        this.gameManager = gameManager;
        this.imageReader = imageReader;
        this.ball = ball;
    }

    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        // remove the brick:
        gameManager.removeBrick((Brick)object1);

        // if its Ball and not Pack and turbo isn't on, make the ball "Turbo":
        // TODO: needs another solution:
        if (!(object2 instanceof Pack) && !gameManager.getTurboMode()){

            // change the ball mode:
            int numCollisions = ball.getCollisionCounter();
            gameManager.setTurboMode(numCollisions);

        }

    }

}
