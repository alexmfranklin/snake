import ml.classifiers.GeneticNN;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class Board extends JPanel implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = -3199194738524258272L;
    private final int B_WIDTH = 300;
    private final int B_HEIGHT = 300;
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = 900;
    private final int RAND_POS = 29;
    private final int DELAY = 100;

    private static final int EMPTY = 0;
    private static final int SNAKE = 1;
    private static final int FOOD = 2;
    private static final int WALL = 3;

    public static int numFinished = 0;

    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

    private int[][] grid = new int[B_WIDTH/DOT_SIZE+2][B_HEIGHT/DOT_SIZE+2];

    public int appleCount =0;
    private int runtime = 15;
    private int dots;
    private int apple_x;
    private int apple_y;
    private int gridAppleX;
    private int gridAppleY;
    private int xDistApple;
    private int yDistApple;

    private Boolean hasDied = false;
    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;
    private boolean newTurn = true;
    private Timer timer;
    private Image ball;
    private Image apple;
    private Image head;
    private JFrame frame;
    private int moveType = 0;
    private GeneticNN network;
    private int numFeatures;
    int[] features;
    Robot r = new Robot();


    long gameStart;
    long gameEnd;
    long start;
    long elapsedTime;


    public Board() throws AWTException {
 
        initBoard();
    }

    private void initBoard() {

        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();
        initGame();
    }

    private void loadImages() {

        ImageIcon iid = new ImageIcon("dot.png");
        ball = iid.getImage();

        ImageIcon iia = new ImageIcon("apple.png");
        apple = iia.getImage();

        ImageIcon iih = new ImageIcon("head.png");
        head = iih.getImage();
    }

    private void initGame() {

        for(int i = 0; i < grid.length; i ++) {
            for(int j = 0; j < grid[i].length; j ++) {
                if(i == 0 || j == 0 || i == grid.length-1 || j == grid[i].length-1) {
                    grid[i][j] = WALL;
                } else {
                    grid[i][j] = EMPTY;
                }
            }
        }

        dots = 3;

        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * 10;
            y[z] = 50;
            grid[4-z][4] = SNAKE;
        }

        locateApple();

        timer = new Timer(DELAY, this);
        timer.start();

        start = System.currentTimeMillis()/1000;
        gameStart = System.currentTimeMillis()/1000;
    }

    public void setNetwork(GeneticNN theNetwork){
        network = theNetwork;
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    public void endGame(){
        inGame = false;
    }

    public void setJFrame(JFrame frame) {
        this.frame = frame;
    }

    private void doDrawing(Graphics g) {

        if (inGame) {

            g.drawImage(apple, apple_x, apple_y, this);

//            for(int i = 1; i < grid.length-1; i ++){
//                for(int j = 1; j < grid[i].length-1; j ++) {
//
//                    if(grid[i][j] == SNAKE) {
//                        g.drawImage(head, i*10, j*10, this);
//                    } else if(grid[i][j] == FOOD) {
//                       g.drawImage(ball, i*10, j*10, this);
//                    }
//                }
//            }


            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                    g.drawImage(head, x[z], y[z], this);
                } else {
                    g.drawImage(ball, x[z], y[z], this);
                }
            }
            yDistApple = Math.abs(y[0]/10+1 - gridAppleY);
            xDistApple = Math.abs(x[0]/10+1 - gridAppleX);
            Toolkit.getDefaultToolkit().sync();

        } else {

            gameOver(g);
        }
    }
    public void setMoveType(int i){
        if(i == 1) moveType = 1;
        else moveType =0;
    }
    private void gameOver(Graphics g) {

        String msg = "Game Over";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
        gameEnd = System.currentTimeMillis()/1000 - gameStart;
        if(hasDied == true) appleCount = 0;
        else appleCount++;
        network.increaseFitness(appleCount);
        timer.stop();

        numFinished ++;
        frame.setVisible(false);
        frame.dispose();
    }

    private void checkApple() {

        if ((x[0] == apple_x) && (y[0] == apple_y)) {
            start = System.currentTimeMillis()/1000;
            appleCount++;
            dots++;
            locateApple();
        }
    }
    public boolean isGameOver(){
        return !inGame;
    }
    private void move() {

        for (int z = dots; z > 0; z--) {
            //grid[(x[z]/10)][(y[z]/10)] = grid[(x[z-1]/10)][(y[z-1]/10)];
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }
        grid[(x[dots]/10+1)][(y[dots]/10)+1] = EMPTY;


        if (leftDirection) {
            x[0] -= DOT_SIZE;
            grid[(x[0]/10)+1][(y[0]/10)+1] = SNAKE;
        }

        if (rightDirection) {
            x[0] += DOT_SIZE;
            grid[(x[0]/10)+1][(y[0]/10)+1] = SNAKE;
        }

        if (upDirection) {
            y[0] -= DOT_SIZE;
            grid[(x[0]/10)+1][(y[0]/10)+1] = SNAKE;
        }

        if (downDirection) {
            y[0] += DOT_SIZE;
            grid[(x[0]/10)+1][(y[0]/10)+1] = SNAKE;
        }
    }

    public int getLeft() {
        if(leftDirection) {
            return grid[x[0]/10+1][y[0]/10+1+1];
        } else if (rightDirection) {
            return grid[x[0]/10+1][y[0]/10+1-1];
        } else if (upDirection) {
            return grid[x[0]/10+1-1][y[0]/10+1];
        } else {
            return grid[x[0]/10+1+1][y[0]/10+1];
        }
    }

    public int getRight() {
        if(leftDirection) {
            return grid[x[0]/10+1][y[0]/10+1-1];
        } else if (rightDirection) {
            return grid[x[0]/10+1][y[0]/10+1+1];
        } else if (upDirection) {
            return grid[x[0]/10+1+1][y[0]/10+1];
        } else {
            return grid[x[0]/10+1-1][y[0]/10+1];
        }
    }

    public int getFront() {
        if(leftDirection) {
            return grid[x[0]/10+1-1][y[0]/10+1];
        } else if (rightDirection) {
            return grid[x[0]/10+1+1][y[0]/10+1];
        } else if (upDirection) {
            return grid[x[0]/10+1][y[0]/10+1-1];
        } else {
            return grid[x[0]/10+1][y[0]/10+1+1];
        }
    }

    public boolean appleLeft() {
        return x[0]/10+1 < gridAppleX;
    }

    public boolean appleRight() {
        return x[0]/10+1 > gridAppleX;
    }

    public boolean appleUp() {
        return y[0]/10+1 > gridAppleY;
    }

    public boolean appleDown() {
        return y[0]/10+1 < gridAppleY;
    }




    public boolean isLeftDirection() {
        return leftDirection;
    }

    public boolean isRightDirection() {
        return rightDirection;
    }

    public boolean isUpDirection() {
        return upDirection;
    }

    private void checkCollision() {
        for (int z = dots; z > 0; z--) {

            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;
                hasDied = true;
            }
        }

        if (y[0] >= B_HEIGHT) {
            inGame = false;
            hasDied = true;
            
        }

        if (y[0] < 0) {
            inGame = false;
            hasDied = true;
        }

        if (x[0] >= B_WIDTH) {
            inGame = false;
            hasDied = true;
        }

        if (x[0] < 0) {
            inGame = false;
            hasDied = true;
        }

        if (!inGame) {
            timer.stop();
        }
    }

    private void locateApple() {
        int rx = (int) (Math.random() * RAND_POS);
        int ry = (int) (Math.random() * RAND_POS);
        while(grid[rx+1][ry+1] == SNAKE) {
            rx = (int) (Math.random() * RAND_POS);
            ry = (int) (Math.random() * RAND_POS);
        }

        apple_x = ((rx * DOT_SIZE));
        apple_y = ((ry * DOT_SIZE));

        gridAppleX = rx+1;
        gridAppleY = ry+1;
        grid[rx+1][ry+1] = FOOD;
    }
    public boolean hasMoved(){
        return newTurn;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {

            checkApple();
            checkCollision();

            if(inGame) {
               
                determineMove();
                move();
                yDistApple = Math.abs(y[0]/10+1 - gridAppleY);
                xDistApple = Math.abs(x[0]/10+1 - gridAppleX);
                elapsedTime = System.currentTimeMillis()/1000 - start;
                if(elapsedTime > runtime){
                    endGame();
                }

            }
        }

        repaint();



    }

    public void pressKey(int key) {
        if ((key == KeyEvent.VK_LEFT ) && (!rightDirection)) {
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

            if ((key == KeyEvent.VK_LEFT ) && (!rightDirection)) {
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
    public void setNumFeatures(int i) {
        numFeatures = i;
    }
    public void determineMove(){

        features = new int[numFeatures];

        setExample(features);
        if(moveType == 1){
        double move[] = network.classify2(features);
        
        if( move[0] == 1 && move[1] == 1){
            if(this.isLeftDirection()){
                this.pressKey(KeyEvent.VK_DOWN);
            }
            else if(this.isRightDirection()){
                this.pressKey(KeyEvent.VK_UP);
            }
            else if(this.isUpDirection()){
                this.pressKey(KeyEvent.VK_LEFT);
            }
            else{
                this.pressKey(KeyEvent.VK_RIGHT);
            }

        }

        else if(move[0] == -1 && move[1] == -1){
            if(this.isLeftDirection()){
                this.pressKey(KeyEvent.VK_UP);
            }
            else if(this.isRightDirection()){
                this.pressKey(KeyEvent.VK_DOWN);
            }
            else if(this.isUpDirection()){
                this.pressKey(KeyEvent.VK_RIGHT);
            }
            else{
                this.pressKey(KeyEvent.VK_LEFT);
            }
        }
    }

    else{
        double move = network.classify(features);
        if( move == -1){
          
            if(this.isLeftDirection()){
                this.pressKey(KeyEvent.VK_DOWN);
            }
            else if(this.isRightDirection()){
                this.pressKey(KeyEvent.VK_UP);
            }
            else if(this.isUpDirection()){
                this.pressKey(KeyEvent.VK_LEFT);
            }
            else{
                this.pressKey(KeyEvent.VK_RIGHT);
            }

        }

        else if(move == 1){
            if(this.isLeftDirection()){
                this.pressKey(KeyEvent.VK_UP);
            }
            else if(this.isRightDirection()){
                this.pressKey(KeyEvent.VK_DOWN);
            }
            else if(this.isUpDirection()){
                this.pressKey(KeyEvent.VK_RIGHT);
            }
            else{
                this.pressKey(KeyEvent.VK_LEFT);
            }
    
    }
}
    }
    private void setExample(int[] features){
        features[0] = ((this.getFront() == SNAKE) ? 1 : 0);
        features[1] = ((this.getLeft() == SNAKE) ? 1 : 0);
        features[2] = ((this.getRight() == SNAKE) ? 1 : 0);
        features[3] = ((this.getFront() == WALL) ? 1 : 0);
        features[4] = ((this.getLeft() == WALL) ? 1 : 0);
        features[5] = ((this.getRight() == WALL) ? 1 : 0);
        double currentDist= Math.sqrt(Math.pow(y[0]/10+1 - gridAppleY,2) + Math.pow(x[0]/10+1 - gridAppleX,2));
        double oldDist = Math.sqrt(Math.pow((double)xDistApple,2) + Math.pow( (double) yDistApple,2));
        if(currentDist < oldDist) features[6] = 1;
        else features[6] = 0; 
       
    }

}

