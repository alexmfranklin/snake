package ml.classifiers;

import ml.data.DataSet;
import ml.data.DataSetSplit;
import ml.data.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GeneticNN implements Classifier {
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
     * @param data
     */
    @Override
    public void train(DataSet data) {
        // init random
        Random random = new Random();

        // get the number of possible features
        numFeatures = data.getAllFeatureIndices().size();

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
    private void updateHidden(Example el) {
        for (int l = 0; l < numLayers; l++) {
            for (int i = 0; i < numHidden; i++) {
                if( l == 0) {
                    for (int j = 0; j < numFeatures; j++) {
                        hiddenNodes[l][i] += inputTable[j][i] * el.getFeature(j);
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

    /**
     * Classify the example.  Should only be called *after* train has been called.
     *
     * @param example
     * @return the class label predicted by the classifier for this example
     */
    @Override
    public double classify(Example example) {
        updateHidden(example);
        updateOutput();
        if (theOutput >= 0) return 1.0;
        else return -1;
    }

    @Override
    public double confidence(Example example) {
        updateHidden(example);
        updateOutput();
        return Math.abs(theOutput);
    }

    public static void main(String args[]) {
        DataSet data = new DataSet("data/titanic-train.csv", DataSet.CSVFILE);
        data = data.getCopyWithBias();
        GeneticNN classifier = new GeneticNN(3,4);
        DataSetSplit splits = data.split(.9);

        classifier.train(splits.getTrain());
        double correct = 0, total = 0;
        for(Example ex : splits.getTest().getData()) {
            double prediction = classifier.classify(ex);
            if(prediction == ex.getLabel()) {
                correct ++;
            }
            total ++;
        }
        System.out.println(correct/total);

        }

}