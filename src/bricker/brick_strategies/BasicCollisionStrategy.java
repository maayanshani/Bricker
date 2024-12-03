package bricker.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import gameobjects.Brick;

public class BasicCollisionStrategy implements CollisionStrategy {
    private bricker.main.BrickerGameManager gameManager;

    public BasicCollisionStrategy(bricker.main.BrickerGameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        gameManager.removeBrick((Brick)object1);


    }

}
