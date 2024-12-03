package bricker.brick_strategies;

import danogl.GameObject;

public interface CollisionStrategy {
    /**
     * Handles the collision between two GameObjects.
     *
     * @param object1 The first GameObject involved in the collision.
     * @param object2 The second GameObject involved in the collision.
     */
    public void onCollision(GameObject object1, GameObject object2);
}
