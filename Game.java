import ml.classifiers.GeneticNN;

import javax.swing.*;
import java.awt.*;

public class Game {

    public Game() {

    }

    public static void main(String[] args) {
        Snake snake = new Snake();
        GeneticNN network = new GeneticNN(4, 3);

        EventQueue.invokeLater(() -> {
            JFrame ex = snake;
            System.out.println(snake.appleLeft());
            ex.setVisible(true);
        });
    }

}