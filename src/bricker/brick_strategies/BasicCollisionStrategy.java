package bricker.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import gameobjects.Brick;

public class BasicCollisionStrategy implements CollisionStrategy {
    /**
     * The game manager that manages the game state.
     */
    private bricker.main.BrickerGameManager gameManager;

    /**
     * Non-default Constructor
     *
     * @param gameManager The game manager that handles the game logic.
     */
    public BasicCollisionStrategy(bricker.main.BrickerGameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * Handles the collision between two GameObjects. In this case, it removes the brick
     * from the game upon collision.
     *
     * @param object1 The first GameObject involved in the collision.
     * @param object2 The second GameObject involved in the collision.
     */
    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        gameManager.removeBrick((Brick)object1);
    }
}
