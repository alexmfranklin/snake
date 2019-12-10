import ml.classifiers.GeneticNN;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;


public class Game {
    private  int numFeatures = 10;
    private  int numNetworks = 48;
    private  int numGenerations = 10;

    ArrayList<GeneticNN> networkList = new ArrayList<GeneticNN>();
    private int total = numNetworks;
    public int numLayers = 1;
    public int numHidden = 25;
    private int numPartners = 1;


    public static void main(String[] args) throws AWTException {
        Game game = new Game();
        game.start();

    }

    /**
     *  Ininitializes first gen of neural nets
     */
    public Game() {
        for (int i = 0; i < numNetworks; i++) {
            GeneticNN network = new GeneticNN(numHidden, numLayers, numFeatures);
            network.train();
            networkList.add(network);
        }
    }

  

    public void start() {
        try {
            this.runGen(networkList);
            for (int g = 1; g < numGenerations; g++) {
                 ArrayList<GeneticNN> newNetList = nextGen(networkList);
                 networkList= newNetList;
                 runGen(networkList);
            }
        } catch (AWTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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

             for (GeneticNN geneticNN : networkList) {
                 int i = 0;
                 System.out.println("index:" +i+ " "+ geneticNN.fitness());
                 i++;
                 
             }

           // for (int i = numNetworks - 2; i >= numNetworks - 2 - numPartners; i--) {
                GeneticNN net1 = networkList.get(numNetworks - 1);
                GeneticNN net2 = networkList.get(numNetworks-2);
                ArrayList<GeneticNN> someChildren = crossover(net1, net2, numNetworks / numPartners);
                
                for (int j = 0; j < someChildren.size(); j++) {
                    allTheChildren.add(someChildren.get(j));
                }
            //}
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

        
        
        for (int num = 0; num < numNetworks; num++) {
            GeneticNN tmpNetwork = new GeneticNN(numHidden, numLayers, numFeatures);
            tmpNetwork.crossOver(input1, layers1, output1, input2, layers2, output2);
            tmpNetwork.mutate(.01);
            networks.add(tmpNetwork);
            
            
        }

        return networks;
    }

}
