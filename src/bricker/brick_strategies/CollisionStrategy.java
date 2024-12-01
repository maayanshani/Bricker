package bricker.brick_strategies;

import danogl.GameObject;

// TODO: can be change to brick instead of object1?
public interface CollisionStrategy {
    public void onCollision(GameObject object1, GameObject object2);
}
