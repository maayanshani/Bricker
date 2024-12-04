package bricker.heartStrategy;

import bricker.brick_strategies.CollisionStrategy;
import danogl.GameObject;

/**
 * A collision strategy that handles interactions involving heart objects.
 * When a collision occurs, the heart is removed from the game, and the player's
 * life count is updated.
 */
public class HeartColideStrategy implements CollisionStrategy {

    /**
     * The game manager responsible for managing the game logic and state.
     */
    private final bricker.main.BrickerGameManager gameManager;

    /**
     * Constructs a new HeartColideStrategy.
     *
     * @param gameManager The game manager responsible for managing the game logic.
     */
    public HeartColideStrategy(bricker.main.BrickerGameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * Handles the collision between two GameObjects.
     * Removes the heart object from the game and updates the player's life count.
     *
     * @param object1 The first GameObject involved in the collision, expected to be the heart.
     * @param object2 The second GameObject involved in the collision.
     */
    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        this.gameManager.removeGeneralObject(object1); // Remove the heart from the game
        this.gameManager.updateLives(true); // Increase the player's life count
    }
}
