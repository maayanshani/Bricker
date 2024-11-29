package brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;

public class BasicCollisionStrategy implements CollisionStrategy {
    private GameObjectCollection gameObjectCollection;

    public BasicCollisionStrategy(GameObjectCollection gameObjectCollection) {
        this.gameObjectCollection = gameObjectCollection;
    }

    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        // TODO MAAYAN: check why the brick never appears
//        gameObjectCollection.removeGameObject(object1);
        System.out.println("collision with break detected");


    }

}
