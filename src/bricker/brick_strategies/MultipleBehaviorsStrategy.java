package bricker.brick_strategies;

import bricker.main.BrickerGameManager;
import danogl.GameObject;
import bricker.gameobjects.Brick;

/**
 * A collision strategy that applies multiple behaviors in sequence.
 * When a collision occurs, it executes all the specified collision strategies
 * one after the other, allowing for complex behaviors.
 */
public class MultipleBehaviorsStrategy implements CollisionStrategy {

    /**
     * The game manager that handles the game logic and state.
     */
    private final BrickerGameManager gameManager;

    /**
     * An array of collision strategies to be executed sequentially.
     */
    private final CollisionStrategy[] coliisionStrategies;

    /**
     * Constructs a new MultipleBehaviorsStrategy.
     *
     * @param brickerGameManager The game manager that handles game logic and state.
     * @param collisionStrategies An array of collision strategies to be executed in sequence.
     */
    public MultipleBehaviorsStrategy(BrickerGameManager brickerGameManager,
                                     CollisionStrategy[] collisionStrategies) {
        this.gameManager = brickerGameManager;
        this.coliisionStrategies = collisionStrategies;
    }

    /**
     * Handles the collision between two GameObjects.
     * Removes the brick from the game and applies all the specified collision strategies in sequence.
     *
     * @param object1 The first GameObject involved in the collision, expected to be a brick.
     * @param object2 The second GameObject involved in the collision.
     */
    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        gameManager.removeBrick((Brick)object1);
        for (CollisionStrategy strategy : coliisionStrategies) {
            strategy.onCollision(object1, object2);
        }
    }
}
