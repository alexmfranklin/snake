import java.awt.EventQueue;
import javax.swing.JFrame;

public class Snake extends JFrame {

    private static final int EMPTY = 0;
    private static final int SNAKE = 1;
    private static final int FOOD = 2;
    private static final int WALL = 3;

    private Board board;

    public Snake() {

        initUI();
    }

    public int getFront() {
        return board.getFront();
    }

    public int getLeft() {
        return board.getLeft();
    }

    public int getRight() {
        return board.getRight();
    }

    public boolean appleLeft() {
        return board.appleLeft();
    }

    public boolean appleRight() {
        return board.appleRight();
    }

    public boolean appleUp() {
        return board.appleUp();
    }

    public boolean appleDown() {
        return board.appleDown();
    }

    private void initUI() {
        Board b = new Board();
        this.board = b;
        add(b);

        setResizable(false);
        pack();

        setTitle("Snake");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            JFrame ex = new Snake();
            ex.setVisible(true);
        });
    }
}