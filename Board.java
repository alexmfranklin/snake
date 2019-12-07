import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

    private final int B_WIDTH = 300;
    private final int B_HEIGHT = 300;
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = 900;
    private final int RAND_POS = 29;
    private final int DELAY = 140;

    private static final int EMPTY = 0;
    private static final int SNAKE = 1;
    private static final int FOOD = 2;
    private static final int WALL = 3;


    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

    private int[][] grid = new int[B_WIDTH/DOT_SIZE+2][B_HEIGHT/DOT_SIZE+2];

    private int dots;
    private int apple_x;
    private int apple_y;
    private int gridAppleX;
    private int gridAppleY;

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;

    private Timer timer;
    private Image ball;
    private Image apple;
    private Image head;

    public Board() {

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
            x[z] = 20 - z * 10;
            y[z] = 20;
            grid[3-z][3] = SNAKE;
        }

        locateApple();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
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

            Toolkit.getDefaultToolkit().sync();

        } else {

            gameOver(g);
        }
    }

    private void gameOver(Graphics g) {

        String msg = "Game Over";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
    }

    private void checkApple() {

        if ((x[0] == apple_x) && (y[0] == apple_y)) {

            dots++;
            locateApple();
        }
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

    private void checkCollision() {

        for (int z = dots; z > 0; z--) {

            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;
            }
        }

        if (y[0] >= B_HEIGHT) {
            inGame = false;
        }

        if (y[0] < 0) {
            inGame = false;
        }

        if (x[0] >= B_WIDTH) {
            inGame = false;
        }

        if (x[0] < 0) {
            inGame = false;
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {

            checkApple();
            checkCollision();
            if(inGame) {
                move();
            }
        }

        repaint();
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