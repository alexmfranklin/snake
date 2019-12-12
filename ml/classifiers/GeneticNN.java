package ml.classifiers;

import java.util.Comparator;
import java.util.Random;

public class GeneticNN implements Comparable<GeneticNN> {
    public static int LEFT = 0;
    public static int RIGHT = 1;

    private Random r = new Random();

    private int numHidden;
    private int numLayers = 1;
    private int numFeatures;

    //weights for first and last layers in network
    private double inputTable[][];
    private double outputTable[][];

    //values of hidden and output nodes
    private double output[];
    private double hiddenNodes[][];

    //weights between hidden layers
    private double layerTable[][][];

    //variable used for genetic algorithm
    private int fitness = 1;
    private int genNum;

    /**
     * @param numHidden
     * @param layers
     * @param numFeatures
     * @param gen_num
     */
    public GeneticNN(int numHidden, int layers, int numFeatures, int gen_num) {
        this.numHidden = numHidden;
        this.numFeatures = numFeatures;
        genNum = gen_num;
        numLayers = layers;

        //initialize all hidden nodes and weights
        output = new double[3];
        inputTable = new double[numFeatures][numHidden];
        layerTable = new double[numLayers][numHidden + 1][numHidden + 1];
        outputTable = new double[numHidden + 1][output.length];
        hiddenNodes = new double[numLayers][numHidden + 1];

        //initialize hidden nodes as 0, but last one being 1 for bias
        for (int j = 0; j < numLayers; j++) {
            for (int i = 0; i < numHidden + 1; i++) {
                hiddenNodes[j][i] = 0; // for bias case
                if (i == numHidden)
                    hiddenNodes[j][i] = 1;
                else
                    hiddenNodes[j][i] = 0;
            }
        }
    }

    /**
     * Train this classifier with random weights
     */
    public void train() {
        // initialize and fill input table which stores the weights between the input
        // features and hidden weights
        for (int i = 0; i < numFeatures; i++) {
            for (int j = 0; j < numHidden; j++) {
                // sets range of nextDouble to -.1-.1 and assigns value to table
                inputTable[i][j] = r.nextGaussian();
            }
        }

        if (numLayers != 0) {
            for (int l = 0; l < numLayers; l++) {
                for (int i = 0; i < numHidden + 1; i++) {
                    for (int j = 0; j < numHidden + 1; j++) {
                        // sets range of nextDouble to -.1-.1 and assigns value to table
                        layerTable[l][i][j] = r.nextGaussian();
                    }
                }
            }
            for (int i = 0; i < numHidden + 1; i++) {
                for (int j = 0; j < output.length; j++) {
                    // sets range of nextDouble to -.1-.1 and assigns value to table
                    outputTable[i][j] += r.nextGaussian();
                }
            }
        }
    }

    /**
     * @return number of layers in the newtork
     */
    public int getNumLayers() {
        return numLayers;
    }

    /**
     * Update the hidden nodes based on the features passed in
     *
     * @param el example set of features
     */
    private void updateHidden(int[] el) {
        for (int l = 0; l < numLayers; l++) {
            for (int i = 0; i < numHidden; i++) {
                if (l == 0) {
                    for (int j = 0; j < numFeatures; j++) {
                        hiddenNodes[l][i] += inputTable[j][i] * el[j];
                    }
                    hiddenNodes[l][i] = Math.tanh(hiddenNodes[l][i]);
                } else {
                    for (int j = 0; j < numHidden; j++) {
                        hiddenNodes[l][i] += layerTable[l - 1][j][i] * hiddenNodes[l - 1][j];
                    }
                    hiddenNodes[l][i] = Math.tanh(hiddenNodes[l][i]);
                }
            }
        }
    }

    /**
     * Update the output based on the hidden nodes and weights
     */
    private void updateOutput() {
        //calculate output value based on hidden nodes and weights coming out of them
        //to the output node
        for (int i = 0; i < hiddenNodes[0].length; i++) {
            for (int j = 0; j < output.length; j++) {
                output[j] += outputTable[i][j] * hiddenNodes[numLayers - 1][i];
            }
        }
        //call the activation function on all output values
        for (int i = 0; i < output.length; i++) {
            output[i] = Math.tanh(output[i]);
        }
    }

    /**
     * @return the weights of the first layer of this network
     */
    public double[][] getInputTable() {
        return inputTable;
    }

    /**
     * @return the weights of the last layer of this network
     */
    public double[][] getOutputTable() {
        return outputTable;
    }

    /**
     * @return the weights for all layers between the first and last layers of this network
     */
    public double[][][] getLayerTable() {
        return layerTable;
    }

    /**
     * Classify the example. Should only be called *after* train has been called.
     * @param example the set of features
     * @return the multiple output values
     */
    public double[] classify(int[] example) {
        double[] tempOutput = new double[output.length];

        updateHidden(example);
        updateOutput();
        for (int i = 0; i < output.length; i++) {
            if (output[i] < 0)
                tempOutput[i] = -1;
            else
                tempOutput[i] = 1;
        }
        return tempOutput;
    }

    /**
     * Calculate the confidence which the network predicts each output
     * @param example the set of features
     * @return the multiple confidences for each output value
     */
    public double[] confidence(int[] example) {
        double[] tempOutput = new double[output.length];

        updateHidden(example);
        updateOutput();

        for (int i = 0; i < output.length; i++) {
            tempOutput[i] = Math.abs(output[i]);
        }
        return tempOutput;
    }

    /**
     * @return the fitness of this network
     */
    public Integer fitness() {
        return fitness;
    }

    /**
     * randomly change some of the weights in the network to simulate mutation
     * @param mutationRate the probability that any weight will be changed
     */
    public void mutate(double mutationRate) {
        //mutate the weights in layer table
        for (int m = 0; m < layerTable.length; m++) {
            for (int i = 0; i < layerTable[m].length; i++) {
                for (int j = 0; j < layerTable[m][i].length; j++) {
                    double rand = r.nextDouble();
                    if (rand < mutationRate) {
                        layerTable[m][i][j] = r.nextGaussian() ;

                    }
                }
            }
        }
        //mutate the weights in input table
        for (int i = 0; i < inputTable.length; i++) {
            for (int j = 0; j < inputTable[i].length; j++) {
                double rand = r.nextDouble();
                if (rand < mutationRate) {
                    inputTable[i][j] = r.nextGaussian();
                }

            }
        }
        //mutate the weights in output table
        for (int j = 0; j < outputTable.length; j++) {
            for (int k = 0; k < outputTable[0].length; k++) {
                double rand = r.nextDouble();
                if (rand < mutationRate) {
                    outputTable[j][k] = r.nextGaussian();
                }

            }
        }
    }

    /**
     * decrease the fitness of this model
     * @param count the amount to decrease fitness by
     */
    public void decreaseFitness(int count) {
        fitness = fitness - count;
    }

    /**
     * increase the fitness of this model
     * @param count the amount to increase fitness by
     */
    public void increaseFitness(int count) {
        fitness = fitness + count;
    }

    /**
     * alter the fitness by the appropriate amount if the network dies
     */
    public void deathFitness() {
        if (genNum > 10)
            fitness = fitness - 25*genNum;
        else
            fitness -= 100 ;
    }

    /**
     * create a set of weights given a set of two weights from two parents
     * @param input1 input weights for parent 1
     * @param layers1 hidden weights for parent 1
     * @param output1 output weights for parent 1
     * @param input2 input weights for parent 2
     * @param layers2 hidden weights for parent 2
     * @param output2 output weights for parent 2
     */
    public void crossOver(double[][] input1, double[][][] layers1, double[][] output1, double[][] input2,
            double[][][] layers2, double[][] output2) {

        // sets input weights
        int inputRandX = r.nextInt(inputTable.length);
        int inputRandY = r.nextInt(inputTable[0].length);
        for (int i = 0; i < inputTable.length; i++) {
            for (int j = 0; j < inputTable[0].length; j++) {

                if (i < inputRandX || (i == inputRandX && j <= inputRandY))
                    inputTable[i][j] = input1[i][j];
                else
                    inputTable[i][j] = input2[i][j];
            }
        }
        // sets output weights
        int outputRandX = r.nextInt(outputTable.length);
        int outputRandY = r.nextInt(outputTable[0].length);
        for (int i = 0; i < output.length; i++) {
            for (int j = 0; j < outputTable[0].length; j++) {
                if (i < outputRandX || (i == outputRandX && j <= outputRandY))
                    outputTable[i][j] = output1[i][j];
                else
                    outputTable[i][j] = output2[i][j];
            }
        }

        // sets the weights for the layers
        int layersRandX = r.nextInt(layerTable[0].length);
        int layersRandY = r.nextInt(layerTable[0][0].length);
        for (int i = 0; i < layerTable.length; i++) {
            for (int j = 0; j < layerTable[0].length; j++) {
                for (int k = 0; k < layerTable[0][0].length; k++) {
                    if (j < layersRandX || (j == layersRandX && k <= layersRandY))
                        layerTable[i][j][k] = layers1[i][j][k];
                    else
                        layerTable[i][j][k] = layers2[i][j][k];
                }
            }
        }
    }

    /**
     * @return comparator for the networks to sort by fitness
     */
    public static Comparator<GeneticNN> byFitness() {
        // lambda expression takes two terms compares them by weight and returns
        // the result for the reverse weight order
        Comparator<GeneticNN> fitComp = (GeneticNN one, GeneticNN two) -> {
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
}
