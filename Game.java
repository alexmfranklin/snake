import ml.classifiers.GeneticNN;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class Game {
    
    public static int iterations = 10;

    //parameters for the genetic algorithm
    private int numFeatures = 7;
    private int numNetworks = 20;
    private int numGenerations = 10;
    public int genNum = 1;
   

    //parameters for each neural network in the genetic algorithm
    ArrayList<GeneticNN> networkList = new ArrayList<GeneticNN>();
    private int total;
    public int numLayers = 1;
    public int numHidden = 25;

    /**
     * run the game
     * @param args
     * @throws AWTException
     */
    public static void main(String[] args) throws AWTException {
        Game game = new Game();
        game.start();


    }


    /**
     * Initializes first gen of neural nets
     */
    public Game() {
        total = numNetworks;
        for (int i = 0; i < numNetworks; i++) {
            GeneticNN network = new GeneticNN(numHidden, numLayers, numFeatures, genNum);
            network.train();
            networkList.add(network);
        }
    }

   
    /**
     * run the generation of snakes and then create children based on the results
     * @throws AWTException
     */
    public void start() throws AWTException {
        this.runGen(networkList);
        for (int g = 1; g <= numGenerations; g++) {
            ArrayList<GeneticNN> newNetList = nextGen(networkList);
            networkList = newNetList;
            runGen(networkList);
        }
    }

    /**
     * let this generation of snakes play the game and get fitness scores
     * @param networkList
     * @throws AWTException
     */
    public void runGen(ArrayList<GeneticNN> networkList) throws AWTException {
        //run each network on the game
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

        //wait until all the networks have finished before moving on
        while (Board.numFinished < total) {
            System.out.print("");
        }

        total += numNetworks;
        genNum++;
    }

    /**
     * create the next generation of children based on the fittest snakes that just ran
     * @param networkList the networks in this generation
     * @return the children networks created from this generation
     */
    public ArrayList<GeneticNN> nextGen(ArrayList<GeneticNN> networkList) {
        ArrayList<GeneticNN> allTheChildren = new ArrayList<GeneticNN>(); //the new generation

        Collections.sort(networkList, GeneticNN.byFitness());
        
        // prints average performance per generation
        double count = 0;
        for (GeneticNN net : networkList) {
            count+= net.appleCount();
        }
        System.out.println(count/numNetworks);


        //Get the top 2 fittest networks and call the crossover function on them
        GeneticNN net1 = networkList.get(numNetworks - 1);
        GeneticNN net2 = networkList.get(numNetworks - 2);
        ArrayList<GeneticNN> someChildren = crossover(net1, net2);

        //add all the children to the output and return the next generation
        for (int j = 0; j < someChildren.size(); j++) {
            allTheChildren.add(someChildren.get(j));

        }
        return allTheChildren;
    }

    /**
     * create children from two given networks
     * @param net1 first parent
     * @param net2 second parent
     * @return the children created by first and second networks
     */
    private ArrayList<GeneticNN> crossover(GeneticNN net1, GeneticNN net2) {
        ArrayList<GeneticNN> networks = new ArrayList<GeneticNN>();

        //weights of the first parent
        double[][] input1 = net1.getInputTable();
        double[][] output1 = net1.getOutputTable();
        double[][][] layers1 = net1.getLayerTable();

        //weights of the second parent
        double[][] input2 = net2.getInputTable();
        double[][] output2 = net2.getOutputTable();
        double[][][] layers2 = net2.getLayerTable();

        //create numNetworks number of new children
        for (int num = 0; num < numNetworks; num++) {
            GeneticNN tmpNetwork = new GeneticNN(numHidden, numLayers, numFeatures, genNum);
            tmpNetwork.crossOver(input1, layers1, output1, input2, layers2, output2);
            tmpNetwork.mutate(.01);
            networks.add(tmpNetwork);
        }
        return networks;
    }
}
