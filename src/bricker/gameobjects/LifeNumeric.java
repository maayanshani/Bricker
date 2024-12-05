package bricker.gameobjects;

/**
 * A class representing the numeric life counter for the game.
 *
 * This class manages the number of lives in the game. It provides methods to add or remove lives,
 * ensuring the life count remains within the defined bounds. The maximum number of lives is
 * limited by the `MAX_LIFES` constant.
 */
public class LifeNumeric {

    /**
     * The maximum number of lives allowed in the game.
     */
    private static final int MAX_LIFES = 4;

    /**
     * The current number of lives the player has.
     */
    private int numLives;

    /**
     * Constructs a new LifeNumeric object with the specified initial number of lives.
     *
     * @param numLives The initial number of lives. This should be a non-negative integer.
     */
    public LifeNumeric(int numLives) {
        this.numLives = numLives;
    }

    /**
     * Retrieves the current number of lives.
     *
     * @return The current number of lives.
     */
    public int getNumLives() {
        return this.numLives;
    }

    /**
     * Adds a life to the life counter, up to the maximum number of lives (`MAX_LIFES`).
     */
    public void addLife() {
        if (numLives < MAX_LIFES) {
            this.numLives++;
        }
    }

    /**
     * Removes a life from the life counter if there are any lives remaining.
     */
    public void loseLife() {
        if (this.numLives > 0) {
            this.numLives--;
        }
    }
}
