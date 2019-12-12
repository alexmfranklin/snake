import ml.classifiers.GeneticNN;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.*;

public class Board extends JPanel implements ActionListener {

    //constants representing items that can occupy the game grid
    private static final int EMPTY = 0;
    private static final int SNAKE = 1;
    private static final int FOOD = 2;
    private static final int WALL = 3;

    //constants for the UI and game play
    private static final long serialVersionUID = -3199194738524258272L;
    private final int B_WIDTH = 300;
    private final int B_HEIGHT = 300;
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = 900;
    private final int RAND_POS = 29;
    private final int DELAY = 120;
    private int runtime = 10;

    Random r = new Random();

    //keep track of the number of snakes that have died in each generation
    public static int numFinished = 0;

    //arrays to keep track of all the points in the snake
    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

    //the grid which contains all items in the game
    private int[][] grid = new int[B_WIDTH / DOT_SIZE + 2][B_HEIGHT / DOT_SIZE + 2];

    //variables to keep track of the apple and how many apples have been eaten
    public int appleCount = 0;
    private int dots;
    private int apple_x;
    private int apple_y;
    private int gridAppleX;
    private int gridAppleY;
    private int xDistApple;
    private int yDistApple;
    private int xDistAppleOld;
    private int yDistAppleOld;

    //variables to keep track of the condition of the snake
    private Boolean hasDied = false;
    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;
    private boolean newTurn = true;

    //images and timer for the UI
    private Timer timer;
    private Image ball;
    private Image apple;
    private Image head;
    private JFrame frame;

    //variables to keep track of the network for the snake
    private GeneticNN network;
    private int numFeatures;
    int[] features;

    //variables that keep track of game status
    long gameStart;
    long gameEnd;
    long start;
    long elapsedTime;

    /**
     * @throws AWTException
     */
    public Board() throws AWTException {
        initBoard();
    }

    /**
     * load the images and set up the UI
     */
    private void initBoard() {
        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();
        initGame();
    }

    /**
     * load the images for the snake and apple
     */
    private void loadImages() {
        ImageIcon iid = new ImageIcon("dot.png");
        ball = iid.getImage();

        ImageIcon iia = new ImageIcon("apple.png");
        apple = iia.getImage();

        ImageIcon iih = new ImageIcon("head.png");
        head = iih.getImage();
    }

    /**
     * initialize the game by setting the initial snake and apple positions
     */
    private void initGame() {
        //fill the grid with wall on the outside and empty inside
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (i == 0 || j == 0 || i == grid.length - 1 || j == grid[i].length - 1) {
                    grid[i][j] = WALL;
                } else {
                    grid[i][j] = EMPTY;
                }
            }
        }

        //initial snake size
        dots = 3;

        //put the snake in the game
        for (int z = 0; z < dots; z++) {
            x[z] = (r.nextInt(25) + 3) * 10 - z * 10;
            y[z] = (r.nextInt(28) + 1) * 10;
            grid[4 - z][4] = SNAKE;
        }

        //create the first apple
        locateApple();

        //start the game timer
        timer = new Timer(DELAY, this);
        timer.start();

        start = System.currentTimeMillis() / 1000;
        gameStart = System.currentTimeMillis() / 1000;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        if (inGame) {

            g.drawImage(apple, apple_x, apple_y, this);

            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                    g.drawImage(head, x[z], y[z], this);
                } else {
                    g.drawImage(ball, x[z], y[z], this);
                }
            }
            yDistApple = Math.abs(y[0] / 10 + 1 - gridAppleY);
            xDistApple = Math.abs(x[0] / 10 + 1 - gridAppleX);
            Toolkit.getDefaultToolkit().sync();
        } else {
            gameOver(g);
        }
    }

    /**
     * set the network of this instance of the game
     * @param theNetwork
     */
    public void setNetwork(GeneticNN theNetwork) {
        network = theNetwork;
    }

    /**
     * end the game once the snake has died or the game times out
     */
    public void endGame() {
        inGame = false;
    }

    /**
     * set the JFrame of this instance of the game
     * @param frame
     */
    public void setJFrame(JFrame frame) {
        this.frame = frame;
    }

    /**
     * calculate the final fitness of the snake and stop the game from running
     * @param g
     */
    private void gameOver(Graphics g) {
        String msg = "Game Over";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
        gameEnd = System.currentTimeMillis() / 1000 - gameStart;

        //alter the fitness depending on if the snake died or not
        if (hasDied == true)
            network.deathFitness();
        network.increaseFitness(appleCount * 200);

        //indicate that this snake has stopped running
        numFinished++;

        timer.stop();
        frame.setVisible(false);
        frame.dispose();
    }

    /**
     * check if the snake is eating the apple
     */
    private void checkApple() {
        if ((x[0] == apple_x) && (y[0] == apple_y)) {
            start = System.currentTimeMillis() / 1000;
            appleCount++;
            dots++;
            locateApple();
        }
    }

    /**
     * take one move for the snake
     */
    private void move() {
        //shift all the snake dots along by one
        for (int z = dots; z > 0; z--) {
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }
        //update the grid to show this change
        grid[(x[dots] / 10 + 1)][(y[dots] / 10) + 1] = EMPTY;

        //calculate the new position of the head given a direction
        if (leftDirection) {
            x[0] -= DOT_SIZE;
            grid[(x[0] / 10) + 1][(y[0] / 10) + 1] = SNAKE;
        }
        if (rightDirection) {
            x[0] += DOT_SIZE;
            grid[(x[0] / 10) + 1][(y[0] / 10) + 1] = SNAKE;
        }
        if (upDirection) {
            y[0] -= DOT_SIZE;
            grid[(x[0] / 10) + 1][(y[0] / 10) + 1] = SNAKE;
        }
        if (downDirection) {
            y[0] += DOT_SIZE;
            grid[(x[0] / 10) + 1][(y[0] / 10) + 1] = SNAKE;
        }
    }

    /**
     * @return the item in the position to the relative left of the snake
     */
    public int getLeft() {
        if (leftDirection) {
            return grid[x[0] / 10 + 1][y[0] / 10 + 1 + 1];
        } else if (rightDirection) {
            return grid[x[0] / 10 + 1][y[0] / 10 + 1 - 1];
        } else if (upDirection) {
            return grid[x[0] / 10 + 1 - 1][y[0] / 10 + 1];
        } else {
            return grid[x[0] / 10 + 1 + 1][y[0] / 10 + 1];
        }
    }

    /**
     * @return the item in the position to the relative right of the snake
     */
    public int getRight() {
        if (leftDirection) {
            return grid[x[0] / 10 + 1][y[0] / 10 + 1 - 1];
        } else if (rightDirection) {
            return grid[x[0] / 10 + 1][y[0] / 10 + 1 + 1];
        } else if (upDirection) {
            return grid[x[0] / 10 + 1 + 1][y[0] / 10 + 1];
        } else {
            return grid[x[0] / 10 + 1 - 1][y[0] / 10 + 1];
        }
    }

    /**
     * @return the item in the position to the relative front of the snake
     */
    public int getFront() {
        if (leftDirection) {
            return grid[x[0] / 10 + 1 - 1][y[0] / 10 + 1];
        } else if (rightDirection) {
            return grid[x[0] / 10 + 1 + 1][y[0] / 10 + 1];
        } else if (upDirection) {
            return grid[x[0] / 10 + 1][y[0] / 10 + 1 - 1];
        } else {
            return grid[x[0] / 10 + 1][y[0] / 10 + 1 + 1];
        }
    }

    /**
     * @return whether the apple is to the relative left of the snake
     */
    public boolean appleLeft() {
        if (leftDirection) {
            return y[0] / 10 + 1 < gridAppleY;
        } else if (rightDirection) {
            return y[0] / 10 + 1 > gridAppleY;
        } else if (downDirection) {
            return x[0] / 10 + 1 < gridAppleX;
        } else {
            return x[0] / 10 + 1 > gridAppleX;
        }
    }

    /**
     * @return whether the apple is to the relative right of the snake
     */
    public boolean appleRight() {
        if (leftDirection) {
            return y[0] / 10 + 1 > gridAppleY;
        } else if (rightDirection) {
            return y[0] / 10 + 1 < gridAppleY;
        } else if (downDirection) {
            return x[0] / 10 + 1 > gridAppleX;
        } else {
            return x[0] / 10 + 1 < gridAppleX;
        }
    }

    /**
     * @return whether the apple is to the relative front of the snake
     */
    public boolean appleUp() {
        if (leftDirection) {
            return x[0] / 10 + 1 > gridAppleX;
        } else if (rightDirection) {
            return x[0] / 10 + 1 < gridAppleX;
        } else if (downDirection) {
            return y[0] / 10 + 1 < gridAppleY;
        } else {
            return y[0] / 10 + 1 > gridAppleY;
        }
    }

    /**
     * @return whether the apple is to the relative back of the snake
     */
    public boolean appleDown() {
        if (leftDirection) {
            return x[0] / 10 + 1 < gridAppleX;
        } else if (rightDirection) {
            return x[0] / 10 + 1 > gridAppleX;
        } else if (downDirection) {
            return y[0] / 10 + 1 > gridAppleY;
        } else {
            return y[0] / 10 + 1 < gridAppleY;
        }
    }

    /**
     * @return is the snake going left
     */
    public boolean isLeftDirection() {
        return leftDirection;
    }

    /**
     * @return is the snake going right
     */
    public boolean isRightDirection() {
        return rightDirection;
    }

    /**
     * @return is the snake going up
     */
    public boolean isUpDirection() {
        return upDirection;
    }

    /**
     * check to see if the snake is hitting itself of the wall
     */
    private void checkCollision() {
        //check if the snake's head is hitting its body
        for (int z = dots; z > 0; z--) {
            if ((z > 0) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;
                hasDied = true;
            }
        }

        //check if the snake's head has gone out of bounds
        if (y[0] >= B_HEIGHT || y[0] < 0 || x[0] >= B_WIDTH || x[0] < 0) {
            inGame = false;
            hasDied = true;
        }

        //stop the game if the snake has hit anything
        if (!inGame) {
            timer.stop();
        }
    }

    /**
     * reposition the apple
     */
    private void locateApple() {
        int rx = (int) (Math.random() * RAND_POS);
        int ry = (int) (Math.random() * RAND_POS);

        //make sure the new position is not hitting the snake
        while (grid[rx + 1][ry + 1] == SNAKE) {
            rx = (int) (Math.random() * RAND_POS);
            ry = (int) (Math.random() * RAND_POS);
        }

        //update the apple position
        apple_x = ((rx * DOT_SIZE));
        apple_y = ((ry * DOT_SIZE));
        gridAppleX = rx + 1;
        gridAppleY = ry + 1;
        grid[rx + 1][ry + 1] = FOOD;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            checkApple();
            checkCollision();

            if (inGame) {
                determineMove(); //use the network to determine the snake's next move

                //calculate old distance from snake's head to apple
                yDistAppleOld = Math.abs(y[0] / 10 + 1 - gridAppleY);
                xDistAppleOld = Math.abs(x[0] / 10 + 1 - gridAppleX);
                move(); //take the determined move
                //calculate new distance from snake's head to apple after the move
                yDistApple = Math.abs(y[0] / 10 + 1 - gridAppleY);
                xDistApple = Math.abs(x[0] / 10 + 1 - gridAppleX);

                //calculate the old and current distance from the apple
                double currentDist = Math.sqrt(Math.pow((double) xDistApple, 2) + Math.pow((double) yDistApple, 2));
                double oldDist = Math.sqrt(Math.pow((double) xDistAppleOld, 2) + Math.pow((double) yDistAppleOld, 2));

                //increase the fit if the snake has moved toward the apple, decrease otherwise
                if (currentDist < oldDist)
                    network.increaseFitness(2);
                else
                    network.decreaseFitness(3);

                //end the game if time is up and snake has not died yet
                elapsedTime = System.currentTimeMillis() / 1000 - start;
                if (elapsedTime > runtime) {
                    endGame();
                }
            }
        }
        repaint();
    }

    /**
     * @param i number of features this network should have
     */
    public void setNumFeatures(int i) {
        numFeatures = i;
    }

    /**
     * determine the next move the snake should make given the input features and the network
     */
    public void determineMove() {
        features = new int[numFeatures];
        setExample(features); //set the input features

        //get the output and confidences of the network given the features
        double move[] = network.classify(features);
        double confidence[] = network.confidence(features);

        int theMove; //the move the network will ultimately decide

        //get the most confident output node and set theMove to be the move corresponding to this move
        if (move[0] * confidence[0] > move[1] * confidence[1] && move[0] * confidence[0] > move[2] * confidence[2]) {
            theMove = 1;
        } else if (move[1] * confidence[1] > move[0] * confidence[0]
                && move[1] * confidence[1] > move[2] * confidence[2]) {
            theMove = 2;
        } else {
            theMove = 3;
        }

        //chance the direction of the snake given the move (left or right) if appropriate
        if (theMove == 1) {
            if (this.isLeftDirection()) {
                this.pressKey(KeyEvent.VK_DOWN);
            } else if (this.isRightDirection()) {
                this.pressKey(KeyEvent.VK_UP);
            } else if (this.isUpDirection()) {
                this.pressKey(KeyEvent.VK_LEFT);
            } else {
                this.pressKey(KeyEvent.VK_RIGHT);
            }
        } else if (theMove == 2) {
            if (this.isLeftDirection()) {
                this.pressKey(KeyEvent.VK_UP);
            } else if (this.isRightDirection()) {
                this.pressKey(KeyEvent.VK_DOWN);
            } else if (this.isUpDirection()) {
                this.pressKey(KeyEvent.VK_RIGHT);
            } else {
                this.pressKey(KeyEvent.VK_LEFT);
            }
        }
    }

    /**
     * set the features given the current game state
     * @param features the array to be filled with features
     */
    private void setExample(int[] features) {
        //variables holding the items surrounding the snake
        int left,right,front;
        left = this.getLeft();
        right = this.getRight();
        front = this.getFront();

        //set features based on snake, wall, and apple positions
        features[0] = ((front == SNAKE || front == WALL) ? 1 : 0);
        features[1] = ((left == SNAKE || left == WALL) ? 1 : 0);
        features[2] = ((right == SNAKE || (right == WALL)) ? 1 : 0);
        features[3] = ((this.appleLeft()) ? 1 : 0);
        features[4] = ((this.appleRight()) ? 1 : 0);
        features[5] = ((this.appleUp()) ? 1 : 0);
        features[6] = ((this.appleDown()) ? 1 : 0);
    }

    /**
     * change the direction of the snake given a key
     * @param key the key that is pressed
     */
    public void pressKey(int key) {
        if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
            leftDirection = true;
            upDirection = false;
            downDirection = false;
        }
        if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
            rightDirection = true;
            upDirection = false;
            downDirection = false;
        }
        if ((key == KeyEvent.VK_UP) && (!downDirection)) {
            upDirection = true;
            rightDirection = false;
            leftDirection = false;
        }
        if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
            downDirection = true;
            rightDirection = false;
            leftDirection = false;
        }
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }
            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }
            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }
}
