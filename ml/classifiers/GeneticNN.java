package ml.classifiers;

import ml.data.DataSet;
import ml.data.DataSetSplit;
import ml.data.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class GeneticNN implements Comparable<GeneticNN>{
    private double eta = .1;
    private int iterations = 200;
    private int numHidden;
    private int numLayers = 1;
    private double inputTable[][];
    private double outputTable[];
    private double layerTable[][][];
    private double hiddenNodes[][];
    private double theOutput = 0;
    private int numFeatures;
    private DataSet test;
    public static int LEFT = 0;
    public static int RIGHT = 1;
    public int fitness=0;

    /**
     * @param numHidden
     */
    public GeneticNN(int numHidden,int layers) {
        this.numHidden = numHidden;
        numLayers = layers;
    }

    /**
     * @param eta
     */
    public void setEta(double eta) {
        this.eta = eta;
    }

    /**
     * @param iter
     */
    public void setIterations(int iter) {
        iterations = iter;
    }

    /**
     * Train this classifier based on the data set
     *
     * @param size
     */
    public void train(int size) {
        // init random
        Random random = new Random();

        // get the number of possible features
        numFeatures = size;

        // initialize and fill input table which stores the weights between the input features and hidden weights
        inputTable = new double[numFeatures][numHidden + 1];
        for (int i = 0; i < numFeatures; i++) {
            for (int j = 0; j < numHidden; j++) {
                // sets range of nextDouble to -.1-.1 and assigns value to table
                inputTable[i][j] = random.nextDouble() / 5 - .1;
            }

        }

        if (numLayers != 0) {
            layerTable = new double[numLayers][numHidden + 1][numHidden + 1];
            for (int l = 0; l < numLayers; l++) {
                for (int i = 0; i < numHidden + 1; i++) {
                    for (int j = 0; j < numHidden + 1; j++) {
                        // sets range of nextDouble to -.1-.1 and assigns value to table
                        layerTable[l][i][j] = random.nextDouble() / 5 - .1;
                    }
                }


            }
            // initialize and fill output table which stores the weights between the hidden nodes and the output node
            outputTable = new double[numHidden + 1];
            for (int k = 0; k < outputTable.length; k++) {
                outputTable[k] = random.nextDouble() / 5 - .1;

            }

            // initialize and fill hidden nodes array which contains the values of the nodes
            hiddenNodes = new double[numLayers][numHidden + 1];
            for (int j = 0; j < numLayers; j++) {
                for (int i = 0; i < hiddenNodes.length; i++) {
                    // for bias case
                    if (i == numHidden) hiddenNodes[j][i] = 1;

                    else hiddenNodes[j][i] = 0;
                }
            }


        }
    }

    /**
     * Update the hidden nodes based on the features of the passed in examples
     *
     * @param el example passed in from the data set
     */
    private void updateHidden(int[] el) {
        for (int l = 0; l < numLayers; l++) {
            for (int i = 0; i < numHidden; i++) {
                if( l == 0) {
                    for (int j = 0; j < numFeatures; j++) {
                        hiddenNodes[l][i] += inputTable[j][i] * el[j];
                    }
                    hiddenNodes[l][i] = Math.tanh(hiddenNodes[l][i]);
                }
                else{
                    for (int j = 0; j < numHidden; j++) {
                        hiddenNodes[l][i] += layerTable[l-1][j][i] * hiddenNodes[l-1][j];
                    }
                    hiddenNodes[l][i] = Math.tanh(hiddenNodes[l][i]);
                }
            }
        }
    }

    /**
     * calculate the value of the output node based on the hidden node values and weights
     *
     * @return the current output node value
     */
    private void updateOutput() {
        // calculate output value based on hidden nodes and weights coming out of them to the output node
        double output = 0;
        for (int i = 0; i < hiddenNodes.length; i++) {
            output += outputTable[i] * hiddenNodes[numLayers-1][i];
        }
        theOutput = Math.tanh(output);
    }

    public double[][] getInputTable(){
        return inputTable;
    }
    public double[] getOutputTable(){
        return outputTable;
    }
    public double[][][] getLayerTable(int cross_point, int direction ){
        if(direction == LEFT){
            double[][][] geneTable = new double[cross_point][numHidden+1][numHidden+1];

            for(int i = 0; i <cross_point; i++) {

                geneTable[i] = layerTable[i];
            }
            return geneTable;

        }

        else if(direction == RIGHT){
            double[][][] geneTable = new double[numLayers-cross_point][numHidden+1][numHidden+1];
            for(int i = cross_point; i < numLayers; i++){

                geneTable[i-cross_point] = layerTable[i];

            }
            return geneTable;
        }
        throw new IllegalArgumentException("Please use GeneticNN.LEFT or GeneticNN.RIGHT");
    }

    /**
     * Classify the example.  Should only be called *after* train has been called.
     *
     * @param example
     * @return the class label predicted by the classifier for this example
     */
    public double classify(int[] example) {
        updateHidden(example);
        updateOutput();
        return theOutput;
    }

    public Integer fitness(){
        return fitness;
    }

    public void increaseFitness(){
        fitness +=1;
    }
    public static Comparator<GeneticNN> byFitness(){

        // lambda expression takes two terms compares them by weight and returns
        // the result for the reverse weight order
        Comparator<GeneticNN> fitComp =  (GeneticNN one, GeneticNN two) -> {

            // creates Long objects so that the weights may be compared
            Integer fitOne = one.fitness();
            Integer fitTwo = two.fitness();

            // returns comparator
            return fitOne.compareTo(fitTwo);
        };

        return fitComp;
    }

    @Override
    public int compareTo(GeneticNN network) {
        return -this.fitness().compareTo(network.fitness());
    }


//    @Override
//    public double confidence(Example example) {
//        updateHidden(example);
//        updateOutput();
//        return Math.abs(theOutput);
//    }
}