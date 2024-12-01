package gameobjects;

public class LifeNumeric {
    private static final int MAX_LIFES = 4;
    private int numLives;

    /**
     * A class representing the numeric life counter for the game.
     *
     * This class manages the number of lives left in the game. It provides
     * methods to add and remove lives, ensuring the life count stays within
     * appropriate bounds.
     *
     * @param numLives Initial number of lives at the start of the game.
     */
    public LifeNumeric(int numLives) {
        this.numLives = numLives;

    }

    public int getNumLives() {
        return this.numLives;
    }

    public void addLife() {
        if (numLives < MAX_LIFES) {
            this.numLives++;
        }
    }

    public void loseLife() {
        if (this.numLives > 0) {
            this.numLives--;
        }
    }

}
