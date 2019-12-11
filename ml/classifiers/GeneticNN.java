package ml.classifiers;
import java.util.Comparator;
import java.util.Random;

public class GeneticNN implements Comparable<GeneticNN>{

    private int numHidden;
    private int numLayers = 1;
    private double inputTable[][];
    private double outputTable[][];
    
    private double output[];
    private double layerTable[][][];
    private double hiddenNodes[][];
   
    private int numFeatures;

    public static int LEFT = 0;
    public static int RIGHT = 1;
    private int fitness=1;
    private int genNum;
    private Random r = new Random();
  

    /**
     * @param numHidden
     */
    public GeneticNN(int numHidden,int layers, int numFeatures, int gen_num) {
        this.numHidden = numHidden;
        this.numFeatures = numFeatures;
        genNum = gen_num;
        numLayers = layers;
        output = new double[3];
        inputTable = new double[numFeatures][numHidden];
        layerTable = new double[numLayers][numHidden+1][numHidden+1];
        outputTable = new double[numHidden+1][output.length];
        hiddenNodes = new double[numLayers][numHidden+1];
       
        for (int j = 0; j < numLayers; j++) {
            for (int i = 0; i < numHidden+1; i++) {
                hiddenNodes[j][i] = 0;                    // for bias case
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
    public void train() {
        // init random
        Random random = new Random();
        
        

        // initialize and fill input table which stores the weights between the input features and hidden weights
        
        for (int i = 0; i < numFeatures; i++) {
            for (int j = 0; j < numHidden; j++) {
                // sets range of nextDouble to -.1-.1 and assigns value to table
                inputTable[i][j] = random.nextGaussian();
            }

        }

        if (numLayers != 0) {
           
            for (int l = 0; l < numLayers; l++) {
                for (int i = 0; i < numHidden+1; i++) {
                    for (int j = 0; j < numHidden+1; j++) {
                        // sets range of nextDouble to -.1-.1 and assigns value to table
                        layerTable[l][i][j] =  random.nextGaussian();
                    }
                }


            }
          

          
            for (int i = 0; i < numHidden+1; i++) {
                for (int j = 0; j < output.length; j++) {
                    // sets range of nextDouble to -.1-.1 and assigns value to table
                    outputTable[i][j] +=  random.nextGaussian();
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

  

    private void updateOutput() {
        // calculate output value based on hidden nodes and weights coming out of them to the output node


        for (int i = 0; i < hiddenNodes[0].length; i++) {
            for (int j = 0; j < output.length; j++) {
                output[j] += outputTable[i][j] * hiddenNodes[numLayers - 1][i];
            }
        }

        for (int i = 0; i < output.length; i++) {
            output[i] = Math.tanh(output[i]);
        }
    }
        

    public double[][] getInputTable(){
        return inputTable;
    }
    public double[][] getOutputTable(){
        return outputTable;
    }


    public double[][][] getLayerTable(){
        return layerTable;
    }
   

       
    /**
     * Classify the example.  Should only be called *after* train has been called.
     *
     * @param example
     * @return the class label predicted by the classifier for this example
     */
    public double[] classify(int[] example) {
        double[] tempOutput = new double[output.length];

        updateHidden(example);
        updateOutput();
        for(int i = 0; i< output.length; i++){
            if(output[i] < 0) tempOutput[i] =-1;
            else tempOutput[i] = 1;
        }
        return tempOutput;
    }




    public double[] confidence(int[] example) {
        double[] tempOutput = new double[output.length];

        updateHidden(example);
        updateOutput();

        for(int i = 0; i< output.length; i++){
            tempOutput[i] = Math.abs(output[i]);
        }
        return tempOutput;
    }

    public Integer fitness(){
        return fitness;
    }

   
   
    public void mutate(double mutationRate) {
        for(int m = 0; m < layerTable.length; m ++) {
            for (int i = 0; i < layerTable[m].length; i++) {
                for (int j = 0; j < layerTable[m][i].length; j++) {
                    double rand = r.nextDouble();
                    if (rand < mutationRate) {
                        layerTable[m][i][j] += r.nextGaussian()/5 ;


                    }
                }
            }
        }
        for(int i = 0; i < inputTable.length; i ++) {
            for(int j = 0; j < inputTable[i].length; j ++) {
                double rand = r.nextDouble();
                if (rand < mutationRate) {
                    inputTable[i][j] = r.nextGaussian()/5;
                }
    
            }
        }
        for(int j = 0; j < outputTable.length; j ++) {
            for(int k = 0; k < outputTable[0].length; k ++) {
            double rand = r.nextDouble();
            if (rand < mutationRate) {
                outputTable[j][k] = r.nextGaussian()/5;
            }


        }
    }
}
    public void decreaseFitness(int count){
        fitness = fitness - count;
    }

    public void increaseFitness(int count){
        fitness = fitness + count;
    }

    public void deathFitness() {
        if(genNum > 5) fitness = fitness - 1000;
        else fitness -= 100 + genNum*10;
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

    public void crossOver(double[][] input1, double[][][] layers1, double[][] output1, double[][] input2, double[][][] layers2, double[][] output2){
       
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
            //sets output weights
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


}
