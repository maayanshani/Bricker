import brick_strategies.BasicCollisionStrategy;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.*;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import gameobjects.Ball;
import gameobjects.Brick;
import gameobjects.Paddle;

import java.awt.*;
import java.util.Random;

/* TODO: MISSING:
    - num of lives in text
    - remove brick after collision
    - count down bricks who disappear

 */


public class BrickerGameManager extends GameManager {
    private static final int PADDLE_HEIGHT = 15;
    private static final int PADDLE_WIDTH = 100;
    private static final int BALL_RADIUS = 20;

    private static final float BALL_SPEED = 250;
    private static final int NUM_OF_BRICKS = 8;
    private static final int NUM_OF_BRICKS_ROWS = 7;
    private static final int BRICK_HEIGHT = 15;
    private static final int WALL_WIDTH = 10;

    private static final int NUM_OF_LIVES = 3;
    private static final int HEART_SIZE = 30;

    private int numBricks;
    private int numBricksRows;
    private int livesLeft;
    // TODO MAAYAN: find out how to use Counter
    //    private danogl.util.Counter bricksCountDown;
    private int bricksCountDown;

    private Ball ball;
    private Vector2 windowDimensions;
    private WindowController windowController;

    public BrickerGameManager(String windowTitle, Vector2 windowDimensions,
                              int numBricks, int numBricksRows) {
        super(windowTitle, windowDimensions);
        this.numBricks = numBricks;
        this.numBricksRows = numBricksRows;
        this.livesLeft = NUM_OF_LIVES;
        // TODO MAAYAN: find out how to use Counter
        this.bricksCountDown = 0;

    }

    public int getBricksCountDown() {
        return bricksCountDown;
    }

    public void setBricksCountDown(int bricksCountDown) {
        this.bricksCountDown = bricksCountDown;
    }

    @Override
    public void initializeGame(ImageReader imageReader,
                               SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        this.windowController = windowController;

        // initialization:
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        windowDimensions = windowController.getWindowDimensions();

        // Adding background
        addBackground(imageReader, windowDimensions);

        // Creating ball:
        createBall(imageReader, windowDimensions, soundReader);

        // Creating paddles:
        Renderable paddleImage = imageReader.readImage("assets/paddle.png", true);
        // user paddle:
        createPaddle(imageReader, inputListener, windowDimensions, paddleImage);
        // AI paddle:
        GameObject aiPaddle = new GameObject(Vector2.ZERO, new Vector2(100, 15), paddleImage);
        aiPaddle.setCenter(
                new Vector2(windowDimensions.x()/2, 30));
        this.gameObjects().addGameObject(aiPaddle);

        // Creating walls:
        createWalls(windowDimensions);

        // Creating break:
        createBricks(windowDimensions, imageReader, numBricks);

        // Creating lives:
        createLives(imageReader, windowDimensions, livesLeft);

    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        checkForGameEnd();
    }

    private void checkForGameEnd() {
        float ballHeight = ball.getCenter().y();
        String prompt = "";
        // if we lost:
        if (ballHeight < 0 || bricksCountDown == numBricks*numBricksRows) {
            // TODO MAAYAN: how to decrease bricks num from collision?
            prompt = "You Win!";
        }

        // if we won:
        if (ballHeight > windowDimensions.y()) {
            livesLeft--;
            System.out.println("Lives: " + livesLeft);
            windowController.resetGame();
        }
        if (livesLeft <=0) {
            prompt = "You Lose!";
        }

        if (!prompt.isEmpty()) {
            prompt += " Play again?";
            if (windowController.openYesNoDialog(prompt)) {
                livesLeft = NUM_OF_LIVES;
                windowController.resetGame();
            } else {
                windowController.closeWindow();
            }
        }
    }

    private void createWalls(Vector2 windowDimensions) {
        // TODO: CODING STYLE
        // TODO: dont show walls

        Color borderColor = Color.blue;
        // create side walls
        int[] wallsWidth = new int[]{(int) (windowDimensions.x()-5), 5};
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

    private void createLives(ImageReader imageReader, Vector2 windowDimensions,
                            int numHearts) {
        // TODO: i didnt use object in object, like the tip in the instructions
        // Create the hearts:
        Renderable heartImage = imageReader.readImage("assets/heart.png", true);
        for (int i = 0; i < numHearts; i++) {
            Vector2 heartSize = new Vector2(30, 30);
            float corX = windowDimensions.x() - 30;
            float corY = (i+1)*HEART_SIZE + 0.5f*HEART_SIZE + WALL_WIDTH + 2*(i+1);
            Vector2 heartCoors = new Vector2(corX, corY);
            GameObject heart = new GameObject(Vector2.ZERO, heartSize, heartImage);

            heart.setCenter(heartCoors);
            this.gameObjects().addGameObject(heart);
        }

        // show the number of lives:
        TextRenderable textLives = new TextRenderable("" + livesLeft);
        if (livesLeft == 1) {
            textLives.setColor(Color.red);
        }
        else if (livesLeft == 2) {
            textLives.setColor(Color.yellow);
        }
        else {
            textLives.setColor(Color.green);
        }

        Vector2 heartCoors = new Vector2(windowDimensions.x() - HEART_SIZE, 0.5f*HEART_SIZE + WALL_WIDTH);
        // TODO MAAYAN: needs to show in renderer


    }
    private void createBall(ImageReader imageReader, Vector2 windowDimensions,
                            SoundReader soundReader) {
        Renderable ballImage = imageReader.readImage("assets/ball.png", true);
        Sound collisionSound = soundReader.readSound("assets/blop.wav");
        ball = new Ball(Vector2.ZERO, new Vector2(BALL_RADIUS, BALL_RADIUS), ballImage, collisionSound);

        float ballVelX = BALL_SPEED;
        float ballVelY = BALL_SPEED;
        Random rand = new Random();
        if (rand.nextBoolean()) {
            ballVelX *=1;
        }
        if (rand.nextBoolean()) {
            ballVelY *=1;
        }
        ball.setVelocity(new Vector2(ballVelX, ballVelY));

        ball.setCenter(windowDimensions.mult(0.5f));
        this.gameObjects().addGameObject(ball);

    }

    private void createPaddle(ImageReader imageReader, UserInputListener inputListener,
                              Vector2 windowDimensions, Renderable paddleImage) {
        GameObject paddle = new Paddle(
                Vector2.ZERO,
                new Vector2(PADDLE_WIDTH, PADDLE_HEIGHT),
                paddleImage,
                inputListener,
                windowDimensions);
        paddle.setCenter(
                new Vector2(windowDimensions.x()/2, (int) (windowDimensions.y()-WALL_WIDTH)));
        this.gameObjects().addGameObject(paddle);

    }

    private void createBricks(Vector2 windowDimensions, ImageReader imageReader, int numBricks) {
        for (int i = 0; i < this.numBricksRows; i++) {
            createBricksRow(windowDimensions, imageReader, this.numBricks, i);
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
        Renderable breakImage = imageReader.readImage("assets/brick.png", false);
        BasicCollisionStrategy basicCollisionStrategy = new BasicCollisionStrategy(this.gameObjects());

        Brick brick = new Brick(Vector2.ZERO,
                new Vector2(brickDims),
                breakImage,
                basicCollisionStrategy);
        brick.setCenter(brickCoors);

        this.gameObjects().addGameObject(brick);

    }

    public static void main(String[] args) {
        int numBricks = NUM_OF_BRICKS;
        int numBricksRows = NUM_OF_BRICKS_ROWS;
        if (args.length == 2) {
            numBricks = Integer.parseInt(args[0]);
            numBricksRows = Integer.parseInt(args[1]);
        }
        BrickerGameManager trial = new BrickerGameManager("bouncing ball",
                new Vector2(700, 500), numBricks, numBricksRows);
        trial.run();


    }
}
