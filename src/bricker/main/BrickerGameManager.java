package bricker.main;

import bricker.brick_strategies.*;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.*;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import gameobjects.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * TODO:
 * 2. change Ex2 to Bricker - ROTEM
 * 3. fix all layers (addObject)- ball, bricks - MAAYAN
 * 4. bricks behind the lives dont disappears ? (not happens to me MAAYAN)
 * 5. if the user win, the game wont reset
 *
 */


public class BrickerGameManager extends GameManager {
    // CONSTANTS:
    private static final int PADDLE_HEIGHT = 15;
    private static final int PADDLE_WIDTH = 100;
    private static final int BALL_RADIUS = 20;
    private static final float BALL_SPEED = 250;
    private static final int NUM_BRICKS_PER_ROW = 8;
    private static final int NUM_ROWS = 7;
    private static final int BRICK_HEIGHT = 15;
    private static final int WALL_WIDTH = 10;
    private static final int NUM_OF_LIVES = 3;
    private static final int HEART_SIZE = 30;
    private static final int HEART_COR_Y = 20;
    private static final int MARGIN = 5;
    private static final int TEXT_SIZE_X = 80;
    private static final int TEXT_SIZE_Y = 20;
    private static final int TEXT_COR_Y = 50;

    // Bricks fields:
    private final int numBricksPerRow;
    private final int numRows;
    private danogl.util.Counter bricksCountDown;

    // Lives fields:
    private int livesLeft;
    // TODO for MAAYAN: we can use mutable array?
    // Don't think cause i delete and create a new one when numLives change
    // You used mutable array (this: <>)
    private List<Heart> heartsList;
    private LifeNumeric lifeNumeric;
    private GameObject lifeTextObject;
    private int heartCounter;
    private Heart[] heartList;

    // Initialization fields:
    private Ball ball;
    private Vector2 windowDimensions;
    private WindowController windowController;
    private ImageReader imageReader;
    private UserInputListener inputListener;

    // Pack fields:
    private int packCounter;
    private Pack[] packsList;
    private SoundReader soundReader;

    // Paddles fields:
    private boolean extraPaddleOn;
    private Paddle extraPaddle;

    /**
     * Constructor for the BrickerGameManager.
     * <p>
     * Initializes the game manager with specified settings like window title,
     * dimensions, number of bricks per row, and number of rows.
     *
     * @param windowTitle Title of the game window.
     * @param windowDimensions Dimensions of the game window.
     * @param numBricksPerRow Number of bricks per row.
     * @param numRows Number of rows of bricks.
     */
    public BrickerGameManager(String windowTitle, Vector2 windowDimensions,
                              int numBricksPerRow, int numRows) {
        super(windowTitle, windowDimensions);
        this.numBricksPerRow = numBricksPerRow;
        this.numRows = numRows;
        this.bricksCountDown = new danogl.util.Counter();
        this.packCounter = 0;
        // TODO: can be replaced with mutable array
        this.packsList = new Pack[numBricksPerRow*numRows*2];
        this.heartList = new Heart[numBricksPerRow*numRows*2];
        this.lifeNumeric = new LifeNumeric(NUM_OF_LIVES);
        this.extraPaddleOn = false;
        this.extraPaddle = null;
    }

    // TODO: needed?
    /**
     * Returns the number of bricks removed from the game.
     *
     * @return Number of bricks removed.
     */
    public int getBricksCountDown() {
        return bricksCountDown.value();
    }

    /**
     * Removes a specified game object from the game.
     * If the removed object is a brick, it updates the brick counter.
     *
     * @param object The game object to remove.
     */
    public void removeObject(GameObject object) {
        boolean removed = this.gameObjects().removeGameObject(object);
        if (removed && object instanceof Brick) {
            this.bricksCountDown.increment();
//            System.out.println("Removed Bricks: " + this.bricksCountDown.value());
        }
        if (removed && object instanceof Paddle) {
            extraPaddle = null;
            extraPaddleOn = false;
        }
    }

    /**
     * Adds a specified game object to the game.
     * If the added object is a pack, it tracks the pack.
     *
     * @param object The game object to add.
     */
    public void addObject(GameObject object) {
        // if it's a pack, add to the list and counter:
        if (object instanceof Pack) {
            this.gameObjects().addGameObject(object);
            this.packsList[packCounter] = (Pack) object;
            this.packCounter++;
        }
        // if it's a paddle, check there aren't one already:
        else if (object instanceof Paddle) {
            if (!extraPaddleOn) {
                this.gameObjects().addGameObject(object);
                extraPaddle = (Paddle) object;
                extraPaddleOn = true;
            }
        }
        else if (object instanceof Heart) {
            this.gameObjects().addGameObject(object);
            this.heartList[packCounter] = (Heart) object;
            this.heartCounter++;
        }
        else {
            this.gameObjects().addGameObject(object);
        }
    }

    /**
     * Initializes the game by setting up the components and environment.
     * Sets up elements like the background, ball, paddle, bricks, and lives.
     *
     * @param imageReader Reader for loading images.
     * @param soundReader Reader for loading sounds.
     * @param inputListener Listener for user inputs.
     * @param windowController Controller for managing the game window.
     */
    @Override
    public void initializeGame(ImageReader imageReader,
                               SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        this.windowController = windowController;
        this.imageReader = imageReader;
        this.soundReader = soundReader;

        // initialization:
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        windowDimensions = windowController.getWindowDimensions();
        this.inputListener = inputListener;

        // Adding background
        addBackground(imageReader, windowDimensions);

        // Creating ball:
        createBall(imageReader, windowDimensions, soundReader);

        // Creating paddles:
        // user paddle:
        createPaddle(imageReader, inputListener, windowDimensions);


        // Creating walls:
        createWalls(windowDimensions);

        // Creating break:
        createBricks(windowDimensions, imageReader);

        // Creating lives:
        createLives(imageReader, windowDimensions, NUM_OF_LIVES);
        createLifeText(NUM_OF_LIVES);

    }

    /**
     * Updates the game state on each frame.
     * Checks for game-ending conditions and updates objects in the game.
     *
     * @param deltaTime Time passed since the last frame.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        checkForGameEnd();
        checkPacksStatus();
        checkExtraPaddleStatus();
        checkHeartsStatus();
        checkTurboStatus();
    }

    private void checkTurboStatus() {
        if (ball.getCollisionCounter()==6) {
            // TODO ROTEM: change back to un-Turbo
        }
    }

    private void checkPacksStatus() {
        for (int i = 0; i < packsList.length; i++) {
            Pack currentPack = packsList[i];
            if (currentPack != null) {
                float packHeight = currentPack.getCenter().y();
                if (packHeight > windowDimensions.y()) {
                    removeObject(currentPack);
                    packsList[i] = null;
                    packCounter--;
                }
            }
        }
    }

    private void checkHeartsStatus() {
        for (int i = 0; i < heartList.length; i++) {
            Heart currentHeart = heartList[i];
            if (currentHeart != null) {
                float heartHeight = currentHeart.getCenter().y();
                if (heartHeight > windowDimensions.y()) {
                    removeObject(currentHeart);
                    heartList[i] = null;
                    heartCounter--;
                }
            }
        }
    }

    private void checkExtraPaddleStatus(){
        if (extraPaddle!=null) {
            if (extraPaddle.getNumCollision() == 4) {
                removeObject(extraPaddle);
            }
        }
    }

    private void checkForGameEnd() {
        float ballHeight = ball.getCenter().y();
        String prompt = "";
        // if the user won:
        // if all the bricks disappeared:
        if (bricksCountDown.value() == numBricksPerRow * numRows ||
                inputListener.isKeyPressed(KeyEvent.VK_W)) {
            prompt = "You Win!";
        }

        // if the user lost:
        if (ballHeight > windowDimensions.y()) {
            resetBall();
            updateLives(false);
//            System.out.println("Lives: " + this.lifeNumeric.getNumLives());
        }

        if (this.lifeNumeric.getNumLives() <=0) {
            prompt = "You Lose!";
        }

        if (!prompt.isEmpty()) {
            prompt += " Play again?";
            if (windowController.openYesNoDialog(prompt)) {
                this.lifeNumeric = new LifeNumeric(NUM_OF_LIVES);
                windowController.resetGame();
            } else {
                windowController.closeWindow();
            }
        }
    }

    private void createWalls(Vector2 windowDimensions) {
        // TODO: dont show walls

        Color borderColor = Color.blue;
        // create side walls
        int[] wallsWidth = new int[]{(int) (windowDimensions.x()-MARGIN), MARGIN};
        int wallHeight = (int)windowDimensions.y();

        RectangleRenderable sideWallRectangle = new RectangleRenderable(borderColor);

        for (int i = 0; i < wallsWidth.length; i++) {
            GameObject sideWall = new GameObject(Vector2.ZERO, new Vector2(WALL_WIDTH, wallHeight), sideWallRectangle);
            sideWall.setCenter(
                    new Vector2(wallsWidth[i], windowDimensions.y()/2));
            this.gameObjects().addGameObject(sideWall);
        }

        // create upper wall:
        int upperWallWidth = (int)windowDimensions.x();

        RectangleRenderable upperWallRectangle = new RectangleRenderable(borderColor);
        GameObject upperWall = new GameObject(Vector2.ZERO,
                new Vector2(upperWallWidth, WALL_WIDTH),
                upperWallRectangle);
        upperWall.setCenter(
                new Vector2(windowDimensions.x()/2, WALL_WIDTH/2));
        this.gameObjects().addGameObject(upperWall);
    }

    private void addBackground(ImageReader imageReader, Vector2 windowDimensions) {
        Renderable backgroundImage = imageReader.readImage("assets/DARK_BG2_small.jpeg", false);
        GameObject background = new GameObject(Vector2.ZERO, windowDimensions, backgroundImage);
        background.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        this.gameObjects().addGameObject(background, Layer.BACKGROUND);

    }

    private void createLives(ImageReader imageReader, Vector2 windowDimensions, int numHearts) {
        this.heartsList = new ArrayList<>(); // Initialize the list
        Renderable heartImage = imageReader.readImage("assets/heart.png", true);

        for (int i = 0; i < numHearts; i++) {
            Vector2 heartSize = new Vector2(HEART_SIZE, HEART_SIZE);
            float corX = windowDimensions.x() - (i + 1) * (heartSize.x() + MARGIN); // Position hearts
            // horizontally
            Vector2 heartCoors = new Vector2(corX, HEART_COR_Y);

            Heart heart = new Heart(heartCoors, heartSize, heartImage);
            heartsList.add(heart); // Add heart to the list
            this.gameObjects().addGameObject(heart, Layer.UI);
        }
}


    // TODO for MAAYAN: switch to CONSTANT.     why?
    private void createLifeText(int numLives) {
        TextRenderable textRenderable = new TextRenderable("Lives: " + numLives);

        // Set the text color based on the number of lives
        if (numLives == 1) {
            textRenderable.setColor(Color.red);
        } else if (numLives == 2) {
            textRenderable.setColor(Color.yellow);
        } else {
            textRenderable.setColor(Color.green);
        }

        // Position for the text
        float corX = windowDimensions.x() - 100; // Position hearts horizontally
        Vector2 textPosition = new Vector2(corX, TEXT_COR_Y); // Top-left corner
        Vector2 textSize = new Vector2(TEXT_SIZE_X, TEXT_SIZE_Y); // Size for text

        // Remove the old text object if it exists
        if (lifeTextObject != null) {
            this.gameObjects().removeGameObject(lifeTextObject, Layer.UI);
        }
        // Create the new text object
        lifeTextObject = new GameObject(textPosition, textSize, textRenderable);
        this.gameObjects().addGameObject(lifeTextObject, Layer.UI);
    }

    private void deleteHearts() {
        for (Heart heart : heartsList) {
            this.gameObjects().removeGameObject(heart, Layer.UI);
        }
    }

    /**
     * Updates the player's lives both numerically and visually.
     * Adjusts the life counter, refreshes the hearts, and updates the life text.
     *
     * @param add True to add a life, false to remove one.
     */
    private void updateLives(boolean add) {
//        System.out.println("Updating lives...");

        // Update the numeric life counter
        if (add) {
            this.lifeNumeric.addLife();
        } else {
            this.lifeNumeric.loseLife();
        }

        // Get the updated number of lives
        int numLives = this.lifeNumeric.getNumLives();
//        System.out.println("Updated Lives: " + numLives);

        // Clear the old hearts and recreate based on new life count
        deleteHearts();
        if (numLives > 0) {
            createLives(imageReader, windowDimensions, numLives);
        }

        // Update the text displaying the number of lives
        createLifeText(numLives);
    }

    private void createBall(ImageReader imageReader, Vector2 windowDimensions,
                            SoundReader soundReader) {
        Renderable ballImage = imageReader.readImage("assets/ball.png", true);
        Sound collisionSound = soundReader.readSound("assets/blop.wav");
        ball = new Ball(Vector2.ZERO, new Vector2(BALL_RADIUS, BALL_RADIUS), ballImage, collisionSound);

        resetBall();
    }

    private void resetBall() {
        // Setting velocity:
        float ballVelX = BALL_SPEED;
        float ballVelY = BALL_SPEED;
        Random rand = new Random();
        if (rand.nextBoolean()) {
            ballVelX *= -1;
        }
        if (rand.nextBoolean()) {
            ballVelY *= -1;
        }
        ball.setVelocity(new Vector2(ballVelX, ballVelY));

        // Setting coordinates:
        ball.setCenter(windowDimensions.mult(0.5f));

        this.gameObjects().addGameObject(ball);
    }

    private void createPaddle(ImageReader imageReader, UserInputListener inputListener,
                              Vector2 windowDimensions) {
        Renderable paddleImage = imageReader.readImage("assets/paddle.png", true);
        Paddle paddle = new Paddle(
                Vector2.ZERO,
                new Vector2(PADDLE_WIDTH, PADDLE_HEIGHT),
                paddleImage,
                inputListener,
                windowDimensions,
                false);
        paddle.setCenter(
                new Vector2(windowDimensions.x()/2, (int) (windowDimensions.y()-WALL_WIDTH)));
        this.gameObjects().addGameObject(paddle);
    }

    private void createBricks(Vector2 windowDimensions, ImageReader imageReader) {
        for (int i = 0; i < this.numRows; i++) {
            createBricksRow(windowDimensions, imageReader, this.numBricksPerRow, i);
        }
    }

    private void createBricksRow(Vector2 windowDimensions, ImageReader imageReader,
                                 int numBricks, int rowIdx) {
        int brickWidth = ((int)windowDimensions.x() - (2* WALL_WIDTH) - 2*(numBricks-1)) / numBricks;
        Vector2 brickDims = new Vector2(brickWidth, BRICK_HEIGHT);
        for (int i = 0; i < numBricks; i++) {
            float corX = i*brickWidth + 0.5f*brickWidth + WALL_WIDTH + 2*i;
            float corY = rowIdx*BRICK_HEIGHT + 0.5f*BRICK_HEIGHT + WALL_WIDTH + 2*rowIdx;
            Vector2 brickCoors = new Vector2(corX, corY);
            createBrick(imageReader, brickDims, brickCoors);
        }
    }

    private void createBrick(ImageReader imageReader,
                             Vector2 brickDims, Vector2 brickCoors) {
        // TODO MAAYAN: fix the bricks layer according to 1.7 note 4
        Renderable brickImage = imageReader.readImage("assets/brick.png", false);

        // create strategy:
        CollisionStrategy collisionStrategy = createBrickCollisionStrategy();

        Brick brick = new Brick(Vector2.ZERO,
                new Vector2(brickDims),
                brickImage,
                collisionStrategy);
        brick.setCenter(brickCoors);

        this.gameObjects().addGameObject(brick);
    }

    private CollisionStrategy createBrickCollisionStrategy() {
        Random random = new Random();

        // Define weights for each strategy according to Instructions
        float[] weights = {0.5f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f};
//        // TODO: delete wrong probabilities, its only for checking the strategies:
//        float[] weights = {0f, 0f, 1f, 0f, 0f, 0f};
        float[] cumulativeProbabilities = new float[weights.length];

        // Compute cumulative probabilities
        cumulativeProbabilities[0] = weights[0];
        for (int i = 1; i < weights.length; i++) {
            cumulativeProbabilities[i] = cumulativeProbabilities[i - 1] + weights[i];
        }

        // Generate a random number between 0 and 1
        float randomValue = random.nextFloat();

        // Add the relevant strategy:
        // TODO FOR MAAYAN: add the relevant parameters for your strategies
        for (int i = 0; i < cumulativeProbabilities.length; i++) {
            if (randomValue <= cumulativeProbabilities[i]) {
                switch (i) {
                    case 0:
                        return new BasicCollisionStrategy(this.gameObjects(), this);
                    case 1:
                        return new ExstraPackStrategy(this,
                                BALL_SPEED,
                                windowDimensions,
                                BALL_RADIUS,
                                imageReader,
                                soundReader);
                    case 2:
                        return new ExstraPaddleStrategy(this,
                                PADDLE_WIDTH,
                                PADDLE_HEIGHT,
                                windowDimensions,
                                inputListener,
                                imageReader);
                    case 3:
                        return new TurboStrategy();
                    case 4:
                        return new ReturnLiveStrategy(this,
                                windowDimensions,
                                inputListener,
                                imageReader,
                                HEART_SIZE
                                );
                    case 5:
                        return new MultipleBehaviorsStrategy();
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        int numBricks = NUM_BRICKS_PER_ROW;
        int numRows = NUM_ROWS;
        if (args.length == 2) {
            numBricks = Integer.parseInt(args[0]);
            numRows = Integer.parseInt(args[1]);
        }
        BrickerGameManager trial = new BrickerGameManager("bouncing ball",
                new Vector2(700, 500), numBricks, numRows);
        trial.run();
    }
}
