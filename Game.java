import ml.classifiers.GeneticNN;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Game {
    private  static int numFeatures = 10;
    private static int numNetworks = 10;
    public Game() {

    }

    public static void main(String[] args) throws AWTException {
        int generationNUm =0;
        if(generationNUm == 0) {
            ArrayList<GeneticNN> networkList = new ArrayList<>();
            for (int i = 0; i < numNetworks; i++) {
                GeneticNN network = new GeneticNN(8, 6);
                networkList.add(network);
                network.train(numFeatures);
                Snake snake = new Snake();
                Board board  = snake.getBoard();
                board.setNumFeatures(numFeatures);

                board.setNetwork(network);

                EventQueue.invokeLater(() -> {
                    boolean bool = true;
                    JFrame ex = snake;
                    ex.setVisible(true);

                });
            }
        }

    }
}