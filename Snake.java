import java.awt.*;
import javax.swing.JFrame;

public class Snake extends JFrame {

    private Board board;

    public Snake() throws AWTException {

        initUI();
    }

    public Board getBoard(){
        return board;
    }

    private void initUI() throws AWTException {
        Board b = new Board(true);
        this.board = b;
        add(b);

        setResizable(false);
        pack();

        setTitle("Snake");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


}