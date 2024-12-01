package bricker.brick_strategies;

import danogl.GameObject;

public class HeartColideStrategy implements CollisionStrategy {
    private bricker.main.BrickerGameManager gameManager;

    public HeartColideStrategy(bricker.main.BrickerGameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        this.gameManager.removeGeneralObject(object1);
        this.gameManager.updateLives(true);
    }
}
