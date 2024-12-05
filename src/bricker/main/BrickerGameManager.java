package bricker.main;

import bricker.brick_strategies.*;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.*;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import bricker.gameobjects.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


/**
 * TODO:
 * 3. return to the right probabilities in createBrickCollisionStrategy method
 */

/**
 * Manages the Bricker game, handling initialization, game updates, object creation, and collision logic.
 */
public class BrickerGameManager extends GameManager {

    // CONSTANTS:
    private static final int PADDLE_HEIGHT = 15;
    private static final int PADDLE_WIDTH = 100;
    private static final int MAX_EXTRA_PADDLE_HITS = 4;
    private static final int BALL_RADIUS = 20;
    private static final float BALL_SPEED = 250;
    private static final int NUM_BRICKS_PER_ROW = 8;
    private static final int NUM_ROWS = 7;
    private static final int BRICK_HEIGHT = 15;
    private static final int WALL_WIDTH = 10;
    private static final int NUM_OF_LIVES_START = 3;
    private static final int HEART_SIZE = 30;
    private static final int HEART_COR_Y = 20;
    private static final int MARGIN = 5;
    private static final int TEXT_SIZE_X = 80;
    private static final int TEXT_SIZE_Y = 20;
    private static final int TEXT_COR_Y = 50;
    private static final int BASIC = 0;
    private static final int EXTRA_PACK = 1;
    private static final int EXTRA_PADDLE = 2;
    private static final int TURBO = 3;
    private static final int RETURN_LIVE = 4;
    private static final int MULTIPLE_BEHAVIORS = 5;
    private static final float TURBO_MULTIPLIER = 1.4f;
    private static final int TURBO_COLLISION_THRESHOLD = 6;
    private static final int NEGATIVE_DIRECTION = -1;
    private static final float WALL_POSITION_ADJUSTMENT = 0.5f;
    private static final float PROBABILITIES_SUM = 1.0f;
    private static final float PROBABILITIES_TOLERANCE = 1e-6f;

    // probabilities for checking things
//     private static final float[] STRATEGY_PROBABILITIES = {0f, 0f, 0f, 0f, 0f, 1f};

    // right probabilities:
     private static final float[] STRATEGY_PROBABILITIES = {0.5f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f};


    // Bricks fields:
    /**
     * Number of bricks per row in the game grid.
     */
    private final int numBricksPerRow;

    /**
     * Number of rows of bricks in the game grid.
     */
    private final int numRows;

    /**
     * A counter used to track the number of bricks left in the game.
     */
    private danogl.util.Counter bricksCountDown;

    // Ball fields:
    /**
     * Flag indicating whether turbo mode is active.
     */
    private boolean isTurboOn;

    /**
     * The number of collisions the ball has made, used to manage turbo mode.
     */
    private int numCollisions;

    /**
     * List of hearts representing the player's lives.
     */
    private List<Heart> heartsLifeList;

    /**
     * Object used to display the number of lives numerically.
     */
    private LifeNumeric lifeNumeric;

    /**
     * The game object used to display the number of lives as text.
     */
    private GameObject lifeTextObject;


    /**
     * Array of moving hearts.
     */
//    private Heart[] movingHeartsList;
    private List<Heart> movingHeartsList;

    // Initialization fields:
    /**
     * The ball object in the game.
     */
    private Ball ball;

    /**
     * Dimensions of the game window.
     */
    private Vector2 windowDimensions;

    /**
     * Controller for managing window-related operations.
     */
    private WindowController windowController;

    /**
     * Reader for loading images.
     */
    private ImageReader imageReader;

    /**
     * Listener for user input (e.g., keyboard events).
     */
    private UserInputListener inputListener;

    // Pack fields:
    /**
     * Array holding all packs in the game.
     */
    private List<Pack> packList;

    /**
     * Reader for loading sound files.
     */
    private SoundReader soundReader;

    // Paddles fields:
    /**
     * Flag indicating whether an extra paddle is currently active.
     */
    private boolean extraPaddleOn;

    /**
     * The extra paddle object, if applicable.
     */
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
        this.packList = new ArrayList<>();
//        this.movingHeartsList = new Heart[numBricksPerRow*numRows*2];
        this.movingHeartsList = new ArrayList<>();
        this.heartsLifeList = new ArrayList<>();
        this.lifeNumeric = new LifeNumeric(NUM_OF_LIVES_START);
        this.extraPaddleOn = false;
        this.extraPaddle = null;
        this.isTurboOn = false;
    }

    /**
     * Removes a specified game object from the game.
     * If the removed object is a brick, it updates the brick counter.
     *
     * @param object The game object to remove.
     */
    public void removeGeneralObject(GameObject object) {
        this.gameObjects().removeGameObject(object);
    }

    /**
     * Removes the paddle from the game.
     * If the paddle is removed, it sets the `extraPaddle` flag to false.
     *
     * @param paddle The paddle object to remove.
     */
    public void removePaddle(Paddle paddle) {
        boolean removed = this.gameObjects().removeGameObject(paddle);
        if (removed) {
            extraPaddle = null;
            extraPaddleOn = false;
        }
    }

    /**
     * Removes a brick from the game.
     * If the brick is removed, it increments the brick counter.
     *
     * @param brick The brick object to remove.
     */
    public void removeBrick(Brick brick) {
        boolean removed = this.gameObjects().removeGameObject(brick);
        if (removed) {
            this.bricksCountDown.increment();
        }
    }

    /**
     * Adds a specified game object to the game.
     * If the added object is a pack, it tracks the pack.
     *
     * @param object The game object to add.
     */
    public void addGeneralObject(GameObject object) {
        this.gameObjects().addGameObject(object);
    }

    /**
     * Adds a heart object to the game and updates the moving hearts list.
     *
     * @param object The heart object to add.
     */
    public void addHeart(GameObject object) {
        this.gameObjects().addGameObject(object);
        this.movingHeartsList.add((Heart) object);
    }

    /**
     * Adds an extra paddle to the game.
     * Ensures only one extra paddle is added at a time.
     *
     * @param paddle The paddle object to add.
     */
    public void addPaddle(Paddle paddle) {
        // Check there aren't one already:
        if (!extraPaddleOn) {
            this.gameObjects().addGameObject(paddle);
            extraPaddle = paddle;
            extraPaddleOn = true;
        }
    }

    /**
     * Adds a pack to the game and updates the pack counter.
     *
     * @param pack The pack object to add.
     */
    public void addPack(Pack pack) {
        // Add to the list and counter:
        this.packList.add(pack);
        this.gameObjects().addGameObject(pack);
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

        // Creating paddle:
        createPaddle(imageReader, inputListener, windowDimensions);

        // Creating walls:
        createWalls(windowDimensions);

        // Creating break:
        createBricks(windowDimensions, imageReader);

        // Creating lives:
        createLives(imageReader, windowDimensions, NUM_OF_LIVES_START);
        createLifeText(NUM_OF_LIVES_START);
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

    /**
     * Retrieves the current status of turbo mode.
     *
     * @return True if turbo mode is active, false otherwise.
     */
    public boolean getTurboMode() {
        return isTurboOn;
    }

    /**
     * Activates turbo mode for the ball.
     * Increases ball speed and changes the ball's appearance.
     *
     * @param numCollisions The number of collisions the ball has made,
     *                       used to determine when to deactivate turbo mode.
     */
    public void setTurboMode(int numCollisions) {
        isTurboOn = true;
        this.numCollisions = numCollisions;

        // Set ball velocity and change its appearance:
        ball.setVelocity(ball.getVelocity().mult(TURBO_MULTIPLIER));
        Renderable redBallImage = imageReader.readImage("assets/redball.png", true);
        ball.renderer().setRenderable(redBallImage);
    }

    /**
     * Checks if turbo mode is active and deactivates it once the
     * required number of collisions have been reached.
     */
    private void checkTurboStatus() {
        if (isTurboOn && ball.getCollisionCounter() == numCollisions + TURBO_COLLISION_THRESHOLD) {
            // Change the ball back to normal speed and appearance
            resetBall();
            isTurboOn = false;
        }
    }

    /**
     * Removes packs that fall out of the game area.
     */
    private void checkPacksStatus() {
        // Use iterator to safely remove while iterating
        packList.removeIf(pack -> {
            if (pack.getCenter().y() > windowDimensions.y()) {
                removeGeneralObject(pack);
                return true; // Remove from list
            }
            return false;
        });
    }

    /**
     * Checks the status of all hearts in the game.
     * Removes hearts that fall out of the game area.
     */
    private void checkHeartsStatus() {
        // Use iterator to safely remove while iterating
        movingHeartsList.removeIf(heart -> {
            if (heart.getCenter().y() > windowDimensions.y()) {
                removeGeneralObject(heart); // Remove from game objects
                return true;               // Indicate removal from list
            }
            return false; // Retain in list
        });
    }


    /**
     * Checks if the extra paddle has exceeded the maximum number of hits.
     * If it has, the extra paddle is removed from the game.
     */
    private void checkExtraPaddleStatus() {
        if (extraPaddle != null) {
            if (extraPaddle.getNumCollision() >= MAX_EXTRA_PADDLE_HITS) {
                removePaddle(extraPaddle);
            }
        }
    }

    /**
     * Checks for game-ending conditions, such as winning, losing, or running out of lives.
     * Prompts the user for a decision when the game ends, either to restart or exit.
     */
    private void checkForGameEnd() {
        float ballHeight = ball.getCenter().y();
        String prompt = "";

        // Check if the user won (all bricks are gone or the W key is pressed)
        if (bricksCountDown.value() == numBricksPerRow * numRows ||
                inputListener.isKeyPressed(KeyEvent.VK_W)) {
            prompt = "You Win!";
        }

        // Check if the user lost (ball fell out of bounds)
        if (ballHeight > windowDimensions.y()) {
            isTurboOn = false;
            resetBall();
            updateLives(false);
        }

        // Check if the user has no more lives
        if (this.lifeNumeric.getNumLives() <= 0) {
            prompt = "You Lose!";
        }

        // Display end game prompt and handle user response
        if (!prompt.isEmpty()) {
            prompt += " Play again?";
            if (windowController.openYesNoDialog(prompt)) {
                this.lifeNumeric = new LifeNumeric(NUM_OF_LIVES_START);
                bricksCountDown.reset(); // Reset brick counter (or similar logic)
                this.isTurboOn = false;
                windowController.resetGame();
            } else {
                windowController.closeWindow();
            }
        }
    }

    /**
     * Creates the walls of the game window, including side and upper walls.
     * The side walls are positioned at the left and right edges, and the upper wall is at the top.
     *
     * @param windowDimensions The dimensions of the game window.
     */
    private void createWalls(Vector2 windowDimensions) {
        int[] wallsWidth = new int[]{(int) (windowDimensions.x() - MARGIN), MARGIN};
        int wallHeight = (int) windowDimensions.y();

        for (int i = 0; i < wallsWidth.length; i++) {
            GameObject sideWall = new GameObject(Vector2.ZERO, new Vector2(WALL_WIDTH, wallHeight), null);
            sideWall.setCenter(new Vector2(wallsWidth[i], windowDimensions.y() * WALL_POSITION_ADJUSTMENT));
            this.gameObjects().addGameObject(sideWall);
        }

        int upperWallWidth = (int) windowDimensions.x();
        GameObject upperWall = new GameObject(Vector2.ZERO, new Vector2(upperWallWidth, WALL_WIDTH), null);
        upperWall.setCenter(new Vector2(windowDimensions.x() * WALL_POSITION_ADJUSTMENT, WALL_WIDTH * WALL_POSITION_ADJUSTMENT));
        this.gameObjects().addGameObject(upperWall);
    }

    /**
     * Adds the background to the game window.
     *
     * @param imageReader    The reader used to load the background image.
     * @param windowDimensions The dimensions of the game window.
     */
    private void addBackground(ImageReader imageReader, Vector2 windowDimensions) {
        Renderable backgroundImage = imageReader.readImage("assets/DARK_BG2_small.jpeg", false);
        GameObject background = new GameObject(Vector2.ZERO, windowDimensions, backgroundImage);
        background.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        this.gameObjects().addGameObject(background, Layer.BACKGROUND);
    }

    /**
     * Creates and adds hearts representing the player's lives in the game.
     *
     * @param imageReader     The reader used to load the heart image.
     * @param windowDimensions The dimensions of the game window.
     * @param numHearts       The number of hearts (lives) to display.
     */
    private void createLives(ImageReader imageReader, Vector2 windowDimensions, int numHearts) {
        Renderable heartImage = imageReader.readImage("assets/heart.png", true);

        for (int i = 0; i < numHearts; i++) {
            Vector2 heartSize = new Vector2(HEART_SIZE, HEART_SIZE);
            float corX = windowDimensions.x() - (i + 1) * (heartSize.x() + MARGIN);
            Vector2 heartCoors = new Vector2(corX, HEART_COR_Y);

            Heart heart = new Heart(heartCoors, heartSize, heartImage);
            heart.setTag("Heart");
            heartsLifeList.add(heart); // Add heart to the list
            this.gameObjects().addGameObject(heart, Layer.UI);
        }
    }

    /**
     * Creates and updates the text that displays the number of lives remaining.
     *
     * @param numLives The number of lives remaining.
     */
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

        float corX = windowDimensions.x() - 100;
        Vector2 textPosition = new Vector2(corX, TEXT_COR_Y);
        Vector2 textSize = new Vector2(TEXT_SIZE_X, TEXT_SIZE_Y);

        // Remove the old text object if it exists
        if (lifeTextObject != null) {
            this.gameObjects().removeGameObject(lifeTextObject, Layer.UI);
        }
        // Create the new text object
        lifeTextObject = new GameObject(textPosition, textSize, textRenderable);
        this.gameObjects().addGameObject(lifeTextObject, Layer.UI);
    }

    /**
     * Deletes all the heart objects representing the player's lives.
     */
    private void deleteHearts() {
        for (Heart heart : heartsLifeList) {
            this.gameObjects().removeGameObject(heart, Layer.UI);
        }
    }

    /**
     * Updates the player's lives, both numerically and visually.
     * Adds or removes a life and updates the hearts and life text.
     *
     * @param add True to add a life, false to remove one.
     */
    public void updateLives(boolean add) {
        // Update the numeric life counter
        if (add) {
            this.lifeNumeric.addLife();
        } else {
            this.lifeNumeric.loseLife();
        }

        int numLives = this.lifeNumeric.getNumLives();

        // Clear old hearts and recreate based on new life count
        deleteHearts();
        if (numLives > 0) {
            createLives(imageReader, windowDimensions, numLives);
        }

        // Update the life text
        createLifeText(numLives);
    }

    /**
     * Creates the ball object that the player controls in the game.
     *
     * @param imageReader    The reader used to load the ball image.
     * @param windowDimensions The dimensions of the game window.
     * @param soundReader    The reader used to load the ball's collision sound.
     */
    private void createBall(ImageReader imageReader, Vector2 windowDimensions, SoundReader soundReader) {
        Renderable ballImage = imageReader.readImage("assets/ball.png", true);
        Sound collisionSound = soundReader.readSound("assets/blop.wav");
        ball = new Ball(Vector2.ZERO, new Vector2(BALL_RADIUS, BALL_RADIUS), ballImage, collisionSound);
        ball.setTag("Ball");

        resetBall();
    }

    /**
     * Resets the ball to its default position and speed.
     * If turbo mode is active, adjusts the ball's velocity and appearance.
     */
    private void resetBall() {
        // if Turbo is On:
        if (isTurboOn) {
            ball.setVelocity(ball.getVelocity().mult(1 / TURBO_MULTIPLIER));
            Renderable redBallImage = imageReader.readImage("assets/ball.png", true);
            ball.renderer().setRenderable(redBallImage);
        }
        // else, fully-reset:
        else {
            float ballVelX = BALL_SPEED;
            float ballVelY = BALL_SPEED;
            Random rand = new Random();
            if (rand.nextBoolean()) {
                ballVelX *= NEGATIVE_DIRECTION;
            }
            if (rand.nextBoolean()) {
                ballVelY *= NEGATIVE_DIRECTION;
            }
            ball.setVelocity(new Vector2(ballVelX, ballVelY));

            ball.setCenter(windowDimensions.mult(0.5f));

            Renderable redBallImage = imageReader.readImage("assets/ball.png", true);
            ball.renderer().setRenderable(redBallImage);

            this.gameObjects().addGameObject(ball);
        }
    }

    /**
     * Creates the paddle that the player controls in the game.
     *
     * @param imageReader     The reader used to load the paddle image.
     * @param inputListener   The input listener for player control.
     * @param windowDimensions The dimensions of the game window.
     */
    private void createPaddle(ImageReader imageReader,
                              UserInputListener inputListener,
                              Vector2 windowDimensions) {
        Renderable paddleImage = imageReader.readImage("assets/paddle.png", true);
        Paddle paddle = new Paddle(Vector2.ZERO, new Vector2(PADDLE_WIDTH, PADDLE_HEIGHT),
                paddleImage, inputListener, windowDimensions, false);
        paddle.setTag("Paddle");
        paddle.setCenter(new Vector2(windowDimensions.x() / 2, windowDimensions.y() - WALL_WIDTH));
        this.gameObjects().addGameObject(paddle);
    }

    /**
     * Creates the brick objects arranged in rows.
     *
     * @param windowDimensions The dimensions of the game window.
     * @param imageReader      The reader used to load the brick image.
     */
    private void createBricks(Vector2 windowDimensions, ImageReader imageReader) {
        for (int i = 0; i < this.numRows; i++) {
            createBricksRow(windowDimensions, imageReader, this.numBricksPerRow, i);
        }
    }

    /**
     * Creates a row of bricks at a specific row index.
     *
     * @param windowDimensions The dimensions of the game window.
     * @param imageReader      The reader used to load the brick image.
     * @param numBricks        The number of bricks in the row.
     * @param rowIdx           The index of the row.
     */
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

    /**
     * Creates a brick game object and adds it to the game objects collection.
     *
     * @param imageReader The ImageReader used to read the brick's image.
     * @param brickDims The dimensions (width and height) of the brick.
     * @param brickCoors The coordinates (x, y) of the brick in the game world.
     */
    private void createBrick(ImageReader imageReader,
                             Vector2 brickDims, Vector2 brickCoors) {
        Renderable brickImage = imageReader.readImage("assets/brick.png", false);

        // create strategy:
        CollisionStrategy collisionStrategy = createBrickCollisionStrategy();

        Brick brick = new Brick(Vector2.ZERO,
                new Vector2(brickDims),
                brickImage,
                collisionStrategy);
        brick.setTag("Brick");
        brick.setCenter(brickCoors);

        this.gameObjects().addGameObject(brick);
    }

    /**
     * Validates that the probabilities array sums to 1.0.
     *
     * @param probabilities The array of probabilities to validate.
     * @throws IllegalArgumentException if the probabilities do not sum to 1.0.
     */
    private void validateProbabilities(float[] probabilities) {
        float sum = 0;
        for (float prob : probabilities) {
            sum += prob;
        }
        if (Math.abs(sum - PROBABILITIES_SUM) > PROBABILITIES_TOLERANCE) {
            throw new IllegalArgumentException("Probabilities must sum to 1.");
        }
    }


    /**
     * Creates a collision strategy for a brick based on weighted random selection.
     * This method assigns a collision strategy to a brick using random probability
     * weighted by predefined values.
     *
     * @return A `CollisionStrategy` object for the brick, selected based on random weights.
     */
    private CollisionStrategy createBrickCollisionStrategy() {
        Random random = new Random();

        // Validate probabilities sum to 1
        validateProbabilities(STRATEGY_PROBABILITIES);

        // Compute cumulative probabilities
        float[] cumulativeProbabilities = new float[STRATEGY_PROBABILITIES.length];
        cumulativeProbabilities[0] = STRATEGY_PROBABILITIES[0];
        for (int i = 1; i < STRATEGY_PROBABILITIES.length; i++) {
            cumulativeProbabilities[i] = cumulativeProbabilities[i - 1] + STRATEGY_PROBABILITIES[i];
        }

        // Generate a random number between 0 and 1
        float randomValue = random.nextFloat();

        // Determine the strategy type based on random value
        int selectedStrategy = 0;
        for (int i = 0; i < cumulativeProbabilities.length; i++) {
            if (randomValue <= cumulativeProbabilities[i]) {
                selectedStrategy = i;
                break;
            }
        }

        // Create the appropriate strategy based on the selected enum value
        return createStrategy(selectedStrategy);
    }

    /**
     * Creates a collision strategy for a brick based on the specified behavior type.
     *
     * @param behaviour An integer representing the behavior type (e.g., BASIC, EXTRA_PACK).
     * @return A CollisionStrategy object corresponding to the specified behavior.
     * @throws IllegalArgumentException if the behavior type is unknown.
     */
    private CollisionStrategy createStrategy(int behaviour) {
        switch (behaviour) {
            case BASIC:
                return new BasicCollisionStrategy(this);
            case EXTRA_PACK:
                return new ExstraPackStrategy(this, imageReader, soundReader, BALL_SPEED, BALL_RADIUS);
            case EXTRA_PADDLE:
                return new ExstraPaddleStrategy(this, windowDimensions, inputListener,
                        imageReader, PADDLE_WIDTH, PADDLE_HEIGHT);
            case TURBO:
                return new TurboStrategy(this, ball);
            case RETURN_LIVE:
                return new ReturnLiveStrategy(this, windowDimensions, imageReader, HEART_SIZE);
            case MULTIPLE_BEHAVIORS:
                return new MultipleBehaviorsStrategy(this, getMultipleStrategies());

            default:
                throw new IllegalArgumentException("Unknown strategy behaviour: " + behaviour);
        }
    }

    /**
     * Generates an array of multiple collision strategies.
     * Randomly selects two or three strategies based on predefined logic.
     *
     * @return An array of CollisionStrategy objects.
     */
    private CollisionStrategy[] getMultipleStrategies() {
        boolean threeBehaviors = false;
        int behave1 = getRandomNum(EXTRA_PACK, MULTIPLE_BEHAVIORS);
        int behave2;
        int behave3 = -1;
        do {
            behave2 = getRandomNum(EXTRA_PACK, MULTIPLE_BEHAVIORS);
        } while (behave1 == behave2);
        if (behave1 == MULTIPLE_BEHAVIORS || behave2 == MULTIPLE_BEHAVIORS) {
            threeBehaviors = true;
            behave1 = (behave1 == MULTIPLE_BEHAVIORS) ? behave2 : behave1;

            behave2 = getRandomNum(EXTRA_PACK, RETURN_LIVE);
            do {
                behave3 = getRandomNum(EXTRA_PACK, RETURN_LIVE);
            } while (behave2 == behave3);
        }
        // Create the strategies array
        if (threeBehaviors) {
            return new CollisionStrategy[] {
                    createStrategy(behave1),
                    createStrategy(behave2),
                    createStrategy(behave3)
            };
        } else {
            return new CollisionStrategy[] {
                    createStrategy(behave1),
                    createStrategy(behave2)
            };
        }
    }

    /**
     * Generates a random integer within a specified range.
     *
     * @param min The minimum value (inclusive).
     * @param max The maximum value (inclusive).
     * @return A random integer between min and max.
     */
    private int getRandomNum(int min, int max){
        return ThreadLocalRandom.current().nextInt(min, max + 1); // Inclusive of `max`
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
