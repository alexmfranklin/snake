import ml.classifiers.GeneticNN;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Game {
    private static int numFeatures = 7;
    private static int numNetworks = 12;
    private static int numGenerations = 10;
    private static int numChildren = 12;

    private int total = numNetworks;
    private int numLayers = 10;
    private int numHidden = 25;
    private int numPartners = 6;

    private Random r = new Random();

    public Game() {

    }

    public static void main(String[] args) throws AWTException {
        Game game = new Game();

        ArrayList<GeneticNN> networkList = new ArrayList<GeneticNN>();
        
        for (int i = 0; i < numChildren; i++) {
            GeneticNN network = new GeneticNN(25, 25);
            network.train(numFeatures);
            networkList.add(network);
        }

        game.runGen(networkList);
        for (int g = 1; g < numGenerations; g++) {
            ArrayList<GeneticNN> newNetList = game.nextGen(networkList);
            networkList= newNetList;
            game.runGen(networkList);
            
            
        }


    }

    public void runGen(ArrayList<GeneticNN> networkList) throws AWTException {
        for (int i = 0; i < numNetworks; i++) {
            Snake snake = new Snake();
            Board board = snake.getBoard();

            board.setNumFeatures(numFeatures);

            board.setNetwork(networkList.get(i));

            EventQueue.invokeLater(() -> {
                JFrame ex = snake;
                board.setJFrame(ex);
                ex.setVisible(true);
            });
        }

        while (Board.numFinished < total) {
            System.out.print("");
        }
        
        total += numNetworks;

    }

    public ArrayList<GeneticNN> nextGen(ArrayList<GeneticNN> networkList) {
        ArrayList<GeneticNN> allTheChildren = new ArrayList<GeneticNN>();

        Collections.sort(networkList, GeneticNN.byFitness());

       
            for (int i = numNetworks - 2; i >= numNetworks - 2 - numPartners; i--) {
                GeneticNN net1 = networkList.get(numNetworks - 1);
                GeneticNN net2 = networkList.get(i);
                ArrayList<GeneticNN> someChildren = crossover(net1, net2, numChildren / numPartners);
                for (int j = 0; j < someChildren.size(); j++) {
                    allTheChildren.add(someChildren.get(j));
                }
            }
            return allTheChildren;

    }

    private ArrayList<GeneticNN> crossover(GeneticNN net1, GeneticNN net2, int numChildren) {

        ArrayList<GeneticNN> networks = new ArrayList<GeneticNN>();

        double[][] input1 = net1.getInputTable();
        double[][] output1 = net1.getOutputTable();
        double[][][] layers1 = net1.getLayerTable();

        double[][] input2 = net2.getInputTable();
        double[][] output2 = net2.getOutputTable();
        double[][][] layers2 = net2.getLayerTable();

        double[][] input = new double[input1.length][input1[0].length];
        double[][] output = new double[output1.length][output1[0].length];
        double[][][] layers = new double[layers1.length][layers1[0].length][layers1[0][0].length];
        
        for (int num = 0; num < numChildren; num++) {

            int inputRandX = r.nextInt(input.length);
            int inputRandY = r.nextInt(input[0].length);
            for (int i = 0; i < input.length; i++) {
                for (int j = 0; j < input[0].length; j++) {
                    
                    if (i < inputRandX || (i == inputRandX && j <= inputRandY))
                        input[i][j] = input1[i][j];
                    else
                        input[i][j] = input2[i][j];
                }
            }
            
            int outputRandX = r.nextInt(output.length);
            int outputRandY = r.nextInt(output[0].length);
            for (int i = 0; i < output.length; i++) {
                for (int j = 0; j < output[0].length; j++) {
                    if (i < outputRandX || (i == outputRandX && j <= outputRandY))
                        output[i][j] = output1[i][j];
                    else
                        output[i][j] = output2[i][j];
                }
            }

            int layersRandX = r.nextInt(layers[0].length);
            int layersRandY = r.nextInt(layers[0][0].length);
            for (int i = 0; i < layers.length; i++) {
                for (int j = 0; j < layers[0].length; j++) {
                    for (int k = 0; k < layers[0][0].length; k++) {
                        if (j < layersRandX || (j == layersRandX && k <= layersRandY))
                            layers[i][j][k] = layers1[i][j][k];
                        else
                            layers[i][j][k] = layers2[i][j][k];
                    }
                }
            }
            GeneticNN newNetwork = new GeneticNN(input, layers, output, numHidden, numLayers);
            newNetwork.mutate(.99);
            networks.add(newNetwork);
        }

        return networks;
    }

}
