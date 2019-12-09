import ml.classifiers.GeneticNN;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Game {
    private static int numFeatures = 4;
    private static int numNetworks = 10;
    private static int numGenerations = 10;
    private static int numChildren = 10;

    private int total = numNetworks;
    private int numLayers = 8;
    private int numHidden = 25;
    private int numPartners = 4;

    private Random r = new Random();

    public Game() {

    }

    public static void main(String[] args) throws AWTException {
        Game game = new Game();

        ArrayList<GeneticNN> networkList = new ArrayList<GeneticNN>();
        ArrayList<GeneticNN> networkList2 = new ArrayList<GeneticNN>();
        for(int i = 0; i < numChildren; i ++) {
            GeneticNN network = new GeneticNN(25, 25);
            network.train(numFeatures);
            networkList.add(network);
        }

        game.runGen(networkList);
        for(int g = 1; g < numGenerations; g ++) {
            networkList2 = game.nextGen(networkList);
            game.runGen(networkList2);
            networkList = networkList2;
            //mutate all networks using mutation function (mutates all weights a little)
            for(GeneticNN net : networkList) {
                net.mutate(0.1);
            }
        }
    }

    public void runGen(ArrayList<GeneticNN> networkList) throws AWTException {
        for (int i = 0; i < numNetworks; i++) {
            Snake snake = new Snake();
            Board board  = snake.getBoard();

            board.setNumFeatures(numFeatures);

            board.setNetwork(networkList.get(i));

            EventQueue.invokeLater(() -> {
                JFrame ex = snake;
                board.setJFrame(ex);
                ex.setVisible(true);
            });
        }

        while(Board.numFinished < total){
            System.out.print("");
        }
        total+= numNetworks;

    }

    public ArrayList<GeneticNN> nextGen(ArrayList<GeneticNN> networkList) {
        ArrayList<GeneticNN> allTheChildren = new ArrayList<GeneticNN>();

        Collections.sort(networkList, GeneticNN.byFitness());
        
        System.out.println(networkList.get(numNetworks-2).fitness() + "'" + networkList.get(numNetworks-1).fitness());
        if(networkList.get(0).fitness() == networkList.get(numNetworks-1).fitness()) {
            for(int i = 0; i < numChildren/2; i ++) {
                int index1 = r.nextInt(numNetworks);
                int index2 = r.nextInt(numNetworks);
                while (index2 == index1) {
                    index2 = r.nextInt(numNetworks);
                }
                GeneticNN net1 = networkList.get(index1);
                GeneticNN net2 = networkList.get(index2);
                ArrayList<GeneticNN> someChildren = crossover(net1, net2, 2);
                for(int j = 0; j < someChildren.size(); j ++) {
                    allTheChildren.add(someChildren.get(j));
                }
            }
            return allTheChildren;
        } else {
           for(int i = numNetworks-2; i >= numNetworks-2 - numPartners; i-- ){
            GeneticNN net1 = networkList.get(numNetworks-1);
            GeneticNN net2 = networkList.get(i);
            ArrayList<GeneticNN> someChildren = crossover(net1, net2, numChildren/numPartners);
            for(int j = 0; j < someChildren.size(); j ++) {
                allTheChildren.add(someChildren.get(j));
            }
        }
            return allTheChildren;
        }
    }

    private ArrayList<GeneticNN> crossover (GeneticNN net1, GeneticNN net2, int numChildren) {

        ArrayList<GeneticNN> networks = new ArrayList<GeneticNN>();

        for(int i = 0; i < numChildren; i ++) {
            int rand = r.nextInt(net1.getNumLayers()+2);
            if (i < numChildren/2) {
                double[][] input = net1.getInputTable();
                double[][][] left = net1.getLayerTable(rand, GeneticNN.LEFT);
                double[][][] right = net2.getLayerTable(rand, GeneticNN.RIGHT);
                double[][] output = net1.getOutputTable2();
                GeneticNN newNetwork = new GeneticNN(input, left, right, rand, output, numLayers, numHidden);
                networks.add(newNetwork);
           } else { 
                double[][] input = net2.getInputTable();
                double[][][] left = net2.getLayerTable(rand, GeneticNN.LEFT);
                double[][][] right = net1.getLayerTable(rand, GeneticNN.RIGHT);
                double[][] output = net2.getOutputTable2();
                GeneticNN newNetwork = new GeneticNN(input, left, right, rand, output, numLayers, numHidden);

                networks.add(newNetwork);
            }
        }
        return networks;
    }

}