package ml.classifiers;

import ml.data.DataSet;
import ml.data.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class TwoLayerNN implements Classifier {

    // instance variables for iteration and learning rate
    private double eta = .1;
    private int iterations = 200;

    // instance variable for number of hidden nodes
    private int numHidden;

    // instance variables for the arrays that contain the model
    private double inputTable[][];
    private double outputTable[];
    private double hiddenNodes[];

    // instance variable for the output value
    private double theOutput = 0;

    // instance variable for the number of features
    private int numFeatures;

    /**
     * Sets the number of hidden nodes there should be in the network
     * @param numHidden
     */
    public TwoLayerNN(int numHidden) {
        this.numHidden = numHidden;
    }

    /**
     * Sets the value for the learning rate.
     * @param eta
     */
    public void setEta(double eta) {
        this.eta = eta;
    }

    /**
     * Sets the number of iterations for the network to be trained
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
        inputTable = new double[numFeatures][numHidden+1];
        for (int i = 0; i < numFeatures; i++) {
            for (int j = 0; j < numHidden; j++) {
                // sets range of nextDouble to -.1-.1 and assigns value to table
                inputTable[i][j] = random.nextDouble() / 5 - .1;
            }

        }

        // initialize and fill output table which stores the weights between the hidden nodes and the output node
        outputTable = new double[numHidden + 1];
        for (int k = 0; k < outputTable.length; k++) {
            outputTable[k] = random.nextDouble() / 5 - .1;

        }


        // initialize and fill hidden nodes array which contains the values of the nodes
        hiddenNodes = new double[numHidden + 1];
        for (int i = 0; i < hiddenNodes.length; i++) {
            // for bias case
            if (i == numHidden) hiddenNodes[i] = 1;

            else hiddenNodes[i] = 0;
        }


        for (int i = 0; i < iterations; i++) {
            //build the model
            double backpropHidden = 0;
            for (Example el : data.getData()) {
                // update the values of the hidden nodes based on the features and weights
                updateHidden(el);

                // update the output node based on the hidden node values and weights
                updateOutput();

                // calculate the modified error for this example
                double modified_error = (el.getLabel() - theOutput) * (1 - Math.pow(theOutput,2));

                //recalculate the values of the weights from the hidden nodes to the output node based on the modified error
                for (int l = 0; l < hiddenNodes.length; l++) {
                    backpropHidden = (modified_error * hiddenNodes[l]);
                    outputTable[l] += backpropHidden*eta;

                }

                //recalculate the values of the weights from the input to the hidden nodes based on modifed error and value of hidden nodes
                double backpropInput = 0;
                for (int j = 0; j < numFeatures; j++) {
                    for (int k = 0; k < hiddenNodes.length; k++) {
                        backpropInput = (modified_error * outputTable[k] * (1 - Math.pow(hiddenNodes[k],2)) * el.getFeature(j));
                        inputTable[j][k] += backpropInput*eta;
                    }

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
        for (int i = 0; i < numHidden; i++) {
            for (int j = 0; j < numFeatures; j++) {
                hiddenNodes[i] += inputTable[j][i] * el.getFeature(j);
            }
            hiddenNodes[i] = Math.tanh(hiddenNodes[i]);
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
            output += outputTable[i] * hiddenNodes[i];
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


    /**
     * Confidence of the example.  Should only be called *after* train has been called.
     *
     * @param example
     * @return the confidence of the class label predicted by the classifier for this example
     */
    @Override
    public double confidence(Example example) {
        updateHidden(example);
        updateOutput();
        return Math.abs(theOutput);
    }



}
