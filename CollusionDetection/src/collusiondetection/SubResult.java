package collusiondetection;

import java.io.PrintWriter;

/**
 *
 * @author Jack
 */
public class SubResult {
    private final String className;
    private double overallDLevenResult;
    private int[][] overallSWResult;
    private int[] overallSWHighSim; // 0 - X coord 1 - Y Coord 2 - Highest Value
    private int[][] chainLengthA;
    public boolean flipped;
    
    /**
     * Constructor used during the comparison phase to store the generated data.
     * @param className This is the name of the class that is being compared.
     */
    public SubResult(String className) {
        this.className = className;
    }
    
    /**
     * This constructor is used when the data is being read from file to be
     * displayed to the user and does not need to be recalculated. 
     * @param className This is the name of the class that is being compared
     * @param overallDLevenResult This is the overall edit distance found.
     * @param overallSWHighSim This is an array with the pattern of {0 - Source Index, 1 - Target Index, 2 - Highest Value} on a 2 dimensional array
     * @param chainLengthA This stores an array which stores the chain length information for the comparisons made.
     * @param flipped This is a simple boolean to dictate if the source and target were flipped when the analysis was made.
     */
    public SubResult(String className, double overallDLevenResult, int[] overallSWHighSim, int[][] chainLengthA, boolean flipped) {
        this.className = className;
        this.overallDLevenResult = overallDLevenResult;
        this.overallSWHighSim = overallSWHighSim;
        this.chainLengthA = chainLengthA;
        this.flipped = flipped;
    }
    
    /**
     * This is used by the Results class to write the stored information to file.
     * Format - 
     * className
     *     overallDLevelResult
     *     overallSWHighSim[0],overallSWHighSim[1]overallSWHighSim[2]
     *     chainLengthA length, ChainLegnth[0] length
     *     chainLengthA data
     *     flipped
     * @param pw The same PrintWriter should be used so the information is written to one file only.
     */
    public void writeToFileNotation(PrintWriter pw) {
        pw.println("    " + "<" + className + ">");
        pw.println("        " + Double.toString(overallDLevenResult));
        pw.println("        " + Integer.toString(overallSWHighSim[0]) + "," + Integer.toString(overallSWHighSim[1]) + "," + Integer.toString(overallSWHighSim[2]));
        pw.println("        " + Integer.toString(chainLengthA.length) + "," + Integer.toString(chainLengthA[0].length));
        
        String linetoWrite;
        for (int a=0;a<chainLengthA.length;a++)  {
            linetoWrite = "";
            for (int b=0;b<chainLengthA[a].length;b++) {
                linetoWrite = linetoWrite + Integer.toString(chainLengthA[a][b]) + ",";
            }
            linetoWrite = linetoWrite.substring(0, linetoWrite.length()-1);
            pw.println("        " + linetoWrite);
        }
        if (flipped) {
            pw.println("        " + "1");
        } else {
            pw.println("        " + "0");
        }
    }
    
    /**
     * Returns the name of the class that was analysed.
     * @return the className variable from the object.
     */
    public String getClassName() {
        return className;
    }
    
    /**
     * Returns the overallSWHighSim array.
     * @return overallSWHighSim array
     */
    public int[] getSWOverallSim() {
        return overallSWHighSim;
    }
    
    /**
     * Returns the result from the Damerau Levenshtein distance calculation for the class.
     * @param sourceClass The source class being compared.
     * @param targetClass The target class being compared.
     */
    public void dLevenCalc(String sourceClass, String targetClass) {
        overallDLevenResult = Analyser.dLeven(sourceClass, targetClass);
    }
    
    /**
     * Calculates and populates the overallSWResult, and overallSWHighSim.
     * @param sourceClass The source class being compared.
     * @param targetClass The target class being compared.
     */
    public void overallSmWm(String sourceClass, String targetClass) {
        overallSWResult = Analyser.smithWaterman(sourceClass, targetClass);
        overallSWHighSim = Analyser.maxValSW(overallSWResult);
    }
    
    /**
     * Used to compare the two pieces of code directly using the Smith-Waterman algorithm.
     * @param sourceClass A string containing all lines of code from the source class.
     * @param targetClass A string containing all lines of code from the target class.
     */
    public void smithWatermanCall(String sourceClass, String targetClass) {
        String[] class1Lines = sourceClass.split("\n");
        String[] class2Lines = targetClass.split("\n");
        if (class1Lines.length > class2Lines.length) {
            flipped = true;
            int[][] a = Analyser.SubResultSW(class2Lines, class1Lines);
            int tempZero;
            for (int i=0;i<a.length;i++) {
                tempZero = a[i][0];
                a[i][0] = a[i][1];
                a[i][1] = tempZero;
            }
            chainLengthA = a;
        } else {
            flipped = false;
            chainLengthA = Analyser.SubResultSW(class1Lines, class2Lines);
        }
    }

    /**
     * Gets the stored value overallDLevenResult
     * @return overallDLevenResult of type int.
     */
    public int getOvDLevenResult() {
        return (int)overallDLevenResult;
    }

    /**
     * Returns the stored chain length array.
     * @return 2d int array.
     */
    public int[][] getChainLength() {
        return chainLengthA;
    }
}
