import ml.classifiers.GeneticNN;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class Game {
    private static int numFeatures = 10;

    public Game() {

    }

    public static void main(String[] args) throws AWTException {
        Snake snake = new Snake();
        Board board  = snake.getBoard();
        board.setNumFeatures(numFeatures);
        GeneticNN network = new GeneticNN(8, 6);
        network.train(numFeatures);
        board.setNetwork(network);
        EventQueue.invokeLater(() -> {
            JFrame ex = snake;
            ex.setVisible(true);


        });
    }
}