package bricker.brick_strategies;

import bricker.main.BrickerGameManager;
import danogl.GameObject;
import gameobjects.Ball;
import gameobjects.Brick;

public class TurboStrategy implements CollisionStrategy{
    /**
     * The game manager responsible for managing the game logic and controlling game state.
     */
    private BrickerGameManager gameManager;

    /**
     * The ball object that may enter turbo mode depending on game events.
     */
    private Ball ball;

    /**
     * Non-default Constructor
     *
     * @param gameManager   The game manager responsible for managing the game logic.
     * @param ball          The ball that will potentially enter turbo mode.
     */
    public TurboStrategy(BrickerGameManager gameManager,
                         Ball ball) {
        this.gameManager = gameManager;
        this.ball = ball;
    }

    /**
     * Handles the collision between two GameObjects. In this case, it removes the brick
     * from the game upon collision, and if the ball is in the right terms, turn Turbo-mode on.
     *
     * @param object1 The first GameObject involved in the collision.
     * @param object2 The second GameObject involved in the collision.
     */
    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        // remove the brick:
        gameManager.removeBrick((Brick)object1);

        // if its Ball and not Pack and turbo isn't on, make the ball "Turbo":
        if (!("Pack".equals(object2.getTag())) && !gameManager.getTurboMode()){

            // change the ball mode:
            int numCollisions = ball.getCollisionCounter();
            gameManager.setTurboMode(numCollisions);

        }

    }
}
