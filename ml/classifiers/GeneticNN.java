package ml.classifiers;
import java.util.Comparator;
import java.util.Random;

public class GeneticNN implements Comparable<GeneticNN>{

    private int numHidden;
    private int numLayers = 1;
    private double inputTable[][];
    private double outputTable[];
    private double outputTable2[][];
    private double output[] = new double[2];
    private double layerTable[][][];
    private double hiddenNodes[][];
    private double theOutput = 0;
    private int numFeatures;

    public static int LEFT = 0;
    public static int RIGHT = 1;
    private int fitness=1;
    private Random r = new Random();
    private int prob = 15;

    /**
     * @param numHidden
     */
    public GeneticNN(int numHidden,int layers) {
        this.numHidden = numHidden;
        numLayers = layers;
    }

    public GeneticNN(double[][] input, double[][][] left, double[][][] gene, int cross_point, double[][] outputtab, int numHidden, int layers) {
        this.numLayers = layers;
        this.numHidden = numHidden;
        layerTable = new double[layers][numHidden+1][numHidden+1];
        outputTable2 = new double[numHidden+1][output.length];
        inputTable = input;
        outputTable2 = outputtab;
       
            layerTable = left;
        
            if(cross_point == numLayers) inputTable = gene[0]; 
            else if(cross_point == numLayers +1) outputTable2 = gene[0];
            else{
                layerTable[cross_point] = gene[0];
            }
           
        
       
            hiddenNodes = new double[numLayers][numHidden+1];
            for (int j = 0; j < numLayers; j++) {
                for (int i = 0; i < numHidden+1; i++) {
                    hiddenNodes[j][i] = 0;
                    // for bias case
                    if (i == numHidden) hiddenNodes[j][i] = 1;

                    else hiddenNodes[j][i] = 0;
                }
            }
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
        inputTable = new double[numFeatures][numHidden];
        for (int i = 0; i < numFeatures; i++) {
            for (int j = 0; j < numHidden; j++) {
                // sets range of nextDouble to -.1-.1 and assigns value to table
                inputTable[i][j] = random.nextDouble() * (random.nextInt(2)-1);
            }

        }

        if (numLayers != 0) {
            layerTable = new double[numLayers][numHidden+1][numHidden+1];
            for (int l = 0; l < numLayers; l++) {
                for (int i = 0; i < numHidden+1; i++) {
                    for (int j = 0; j < numHidden+1; j++) {
                        // sets range of nextDouble to -.1-.1 and assigns value to table
                        layerTable[l][i][j] =   random.nextDouble() / 5 - .1;
                    }
                }


            }
            // initialize and fill output table which stores the weights between the hidden nodes and the output node
            outputTable = new double[numHidden+1];
            for (int k = 0; k < outputTable.length; k++) {
                outputTable[k] =   random.nextDouble() / 5 - .1;

            }

            outputTable2 = new double[numHidden+1][output.length];
            for (int i = 0; i < numHidden+1; i++) {
                for (int j = 0; j < output.length; j++) {
                    // sets range of nextDouble to -.1-.1 and assigns value to table
                    outputTable2[i][j] =   random.nextDouble() / 5 - .1;
                }
    
            }

            // initialize and fill hidden nodes array which contains the values of the nodes
            hiddenNodes = new double[numLayers][numHidden+1];
            for (int j = 0; j < numLayers; j++) {
                for (int i = 0; i < numHidden+1; i++) {
                    hiddenNodes[j][i] = 0;
                    // for bias case
                    if (i == numHidden) hiddenNodes[j][i] = 1;

                    else hiddenNodes[j][i] = 0;
                }
            }


        }
    }

    public int getNumLayers() {
        return numLayers;
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
        double out = 0;
        for (int i = 0; i < hiddenNodes[0].length; i++) {
            out += outputTable[i] * hiddenNodes[numLayers-1][i];
        }
        theOutput = Math.tanh(out);
    }

    private void updateOutput2() {
        // calculate output value based on hidden nodes and weights coming out of them to the output node

 
        for (int i = 0; i < hiddenNodes[0].length; i++) {
            for (int j = 0; j < output.length; j++) {
            output[j] += outputTable2[i][j] * hiddenNodes[numLayers-1][i];
        }
    }

        for (int i = 0; i < output.length; i++) {
            output[i] = Math.tanh(output[i]);
    }
        }
        

    public double[][] getInputTable(){
        return inputTable;
    }
    public double[] getOutputTable(){
        return outputTable;
    }

    public double[][] getOutputTable2(){
        return outputTable2;
    }

    public double[][][] getLayerTable(int cross_point, int direction ){
        if(direction == LEFT){
            if(r.nextInt(prob) == 0) {
            for(int x = 0; x < layerTable[0].length; x ++) {
                for(int y = 0; y < layerTable[0][0].length; y ++) {
               
                  layerTable[r.nextInt(layerTable.length)][x][y] +=  r.nextDouble() / 5 - .1;
                
                }
            }
        }

               
            
            return layerTable;

        }

        else if(direction == RIGHT){
            double[][][] geneTable = new double[1][numHidden][numHidden];
           
            if(cross_point == numLayers) geneTable[0] = inputTable; 
            else if(cross_point == numLayers +1) geneTable[0] = outputTable2;
            else{
                geneTable[0] = layerTable[cross_point];
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
        if(-1/3 < theOutput && theOutput< 1/3) return 0;
        else if(theOutput > 1/3) return 1;
        else return -1;
    }
    public double[] classify2(int[] example) {
        updateHidden(example);
        updateOutput2();
        for(int i = 0; i< output.length; i++){
            if(output[i] < 0) output[i] =-1;
            else  output[i] = 1;
        }
        return output;
    }

    public Integer fitness(){
        return fitness;
    }

    public void mutate(double mutationRate) {
        for(int m = 0; m < numLayers; m ++) {
            for (int i = 0; i < layerTable[m].length; i++) {
                for (int j = 0; j < layerTable[m][i].length; j++) {
                    double rand = r.nextDouble();
                    if (rand < mutationRate) {
                        layerTable[m][i][j] += r.nextGaussian() / 5;

                        if (layerTable[m][i][j] > 1) {
                            layerTable[m][i][j] = 1;
                        }
                        if (layerTable[m][i][j] < -1) {
                            layerTable[m][i][j] = -1;
                        }
                    }
                }
            }
        }
    }

    public void increaseFitness(int count){
        fitness = fitness * count;
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