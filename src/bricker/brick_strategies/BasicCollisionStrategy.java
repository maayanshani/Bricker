package bricker.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;

public class BasicCollisionStrategy implements CollisionStrategy {
    private GameObjectCollection gameObjectCollection;
    private bricker.main.BrickerGameManager gameManager;

    public BasicCollisionStrategy(GameObjectCollection gameObjectCollection,
                                  bricker.main.BrickerGameManager gameManager) {
        this.gameObjectCollection = gameObjectCollection;
        this.gameManager = gameManager;
    }

    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        // TODO MAAYAN: check why the brick never appears
        gameManager.removeObject(object1);
        System.out.println("collision with break detected");


    }

}
