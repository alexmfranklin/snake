import java.awt.*;
import javax.swing.JFrame;

public class Snake extends JFrame {

    public static final int EMPTY = 0;
    public static final int SNAKE = 1;
    public static final int FOOD = 2;
    public static final int WALL = 3;

    private Board board;

    public Snake() throws AWTException {

        initUI();
    }

    public Board getBoard(){
        return board;
    }

    private void initUI() throws AWTException {
        Board b = new Board();
        this.board = b;
        add(b);

        setResizable(false);
        pack();

        setTitle("Snake");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


}