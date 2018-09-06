package collusiondetection;

import java.io.PrintWriter;

/**
 *
 * @author Jack
 */
public class Results {
    private SubResult[] results;
    int source, target;
    private int largestSWSim; //Largest Smith Waterman Similarity result across all subresults
    private int[] NoOfSimilarLines;
    private int[] TotalNoOfLines;
    public final int SOURCE = 0;
    public final int TARGET = 1;
    
    /**
     * Results constructor that is used when first generating the data and saving it to disk.
     * @param source This is the source code number that is relative to the one stored in the scList in Controller.
     * @param target This is the target code number that is relative to the one stored in the scList in Controller.
     * @param pw This is the PrintWriter used to store the data. This must be the same PrintWriter and not a new instance every time.
     */
    public Results(int source, int target, PrintWriter pw) {
        this.source = source;
        this.target = target;
        this.largestSWSim = 0;
        this.results = new SubResult[getMaxClassLength()];
        this.NoOfSimilarLines = new int[results.length];
        this.TotalNoOfLines = new int[results.length];
        for (int i=0;i<results.length;i++) {
            results[i] = new SubResult(Controller.scList.get(source).getClassName(i));
            results[i].dLevenCalc(Controller.scList.get(source).getClassRaw(i), Controller.scList.get(target).getClassRaw(i));
            results[i].overallSmWm(Controller.scList.get(source).getClassRaw(i), Controller.scList.get(target).getClassRaw(i));
            results[i].smithWatermanCall(Controller.scList.get(source).getClassRaw(i), Controller.scList.get(target).getClassRaw(i)); //Populate ChainLengthA
        }
        populateSimilarity();
        for(int i=0;i<results.length;i++) {
            if (results[i].getSWOverallSim()[2] > largestSWSim) {
                largestSWSim = results[i].getSWOverallSim()[2];
            }
        }
        writeToFile(pw);
    }
    
    /**
     * Results Constructor used when reading the data from file to be used to display the results.
     * @param results SubResults array.
     * @param source This is the source code number that is relative to the one stored in the scList in Controller.
     * @param target This is the target code number that is relative to the one stored in the scList in Controller.
     * @param largestSWSim Largest Smith Waterman Similarity result across all SubResults.
     * @param NoOfSimiarLines Integer array keeping track of the number of similar lines.
     * @param TotalNoOfLines Integer array keeping track of the number of total lines.
     */
    public Results(SubResult[] results, int source, int target, int largestSWSim, int[] NoOfSimiarLines, int[] TotalNoOfLines) {
        this.results = results;
        this.source = source;
        this.target = target;
        this.largestSWSim = largestSWSim;
    }
    
    /**
     * Writes the overall information to file and then calls to write the SubResult information.
     * @param pw The same PrintWriter should be used so the information is written to one file only.
     */
    private void writeToFile(PrintWriter pw) {
        //Set up main section and overall results
        pw.println("<" + Controller.scList.get(source).sourceName + " --- " + Controller.scList.get(target).sourceName + ">");
        pw.println("    " + "<Results>");
        pw.println("        " + Integer.toString(results.length));
        pw.println("        " + Integer.toString(source) + "," + Integer.toString(target));
        pw.println("        " + Integer.toString(largestSWSim));
        
        String outputTempText = "";
        for (int i=0;i<NoOfSimilarLines.length;i++) {
            outputTempText = outputTempText + Integer.toString(NoOfSimilarLines[i]) + ",";
        }
        pw.println("        " + outputTempText.substring(0, outputTempText.length()-1));
        
        outputTempText = "";
        for (int i=0;i<TotalNoOfLines.length;i++) {
            outputTempText = outputTempText + Integer.toString(TotalNoOfLines[i]) + ",";
        }
        pw.println("        " + outputTempText.substring(0, outputTempText.length()-1));
        //Write SubResults Info
        for (int i=0;i<results.length;i++) {
            results[i].writeToFileNotation(pw);
        }
    }
    
    /**
     * This method is used to calculate the number of similar lines against the
     * number of total lines and save those results to the Controller
     * similarity table as a double percentage.
     */
    private void populateSimilarity() {
        for (int i=0;i<results.length;i++) {
            for (int array=0;i<results[i].getChainLength().length;array++) {
                if (array > (results[i].getChainLength().length - 1)) {
                    break;
                }
                if (results[i].getChainLength()[array][2] > 0) {
                    NoOfSimilarLines[i]+=results[i].getChainLength()[array][2];
                    array+=results[i].getChainLength()[array][2];
                }
            }
            TotalNoOfLines[i] = results[i].getChainLength().length;
            Controller.similarityTable[source][target][i] = (double)NoOfSimilarLines[i] / (double)TotalNoOfLines[i];
        }
    }
    
    /**
     * This will return the Chain Length array for the specified Index of the SubResult.
     * @param index Index of the desired SubResult chain length array.
     * @return The chain length array.
     * Primary Number - The line number of the primary source.
     * Secondary Number - 0 : The starting index of the chain on the secondary source.
     * 1 : The length of the chain
     */
    public int[][] getChainLength(int index) {
        return results[index].getChainLength();
    }
    
    /**
     * Used to get the number of SubResults that are stored.
     * @return The length of the SubResult array.
     */
    public int getLength() {
        return results.length;
    }
    
    /**
     * This is used to get the source code text from the specified source code folder and class.
     * @param targetId The index number of the target source code folder.
     * @param index The index of the SubResult
     * @return The source code that was compared.
     */
    public String getClassText(int targetId, int index) {
        if (targetId == SOURCE) {
            return Controller.scList.get(source).getClassRaw(index);
        } else {
            return Controller.scList.get(target).getClassRaw(index);
        }
    }
    
    /**
     * Returns the class name of the specified SubResult
     * @param index The index of the SubResult stored in the array.
     * @return String containing the class name.
     */
    public String getClassName(int index) {
        return results[index].getClassName();
    }
    
    /**
     * Returns the Damerau Levenshtein result for the specified SubResult.
     * @param index The index of the SubResult stored in the array.
     * @return The edit distance of the SubResult.
     */
    public int getDLevenOverall(int index) {
        return results[index].getOvDLevenResult();
    }
    
    /**
     * Returns the largest value similarity produced by Smith Waterman from the sub results
     * @return 
     */
    public int getSWOverallSim() {
        return largestSWSim;
    }
    
    /**
     * Returns the similarity value for a specific SubResult
     * @param index (Which SubResult to refer to)
     * @return
     */
    public int getSWOverallSim(int index) {
        return results[index].getSWOverallSim()[2];
    }
    
    /**
     * 
     * @return 
     */
    public int getMaxClassLength() {
        int max = Controller.scList.get(source).getTotalClass();
        if (Controller.scList.get(source).getTotalClass() > max) max = Controller.scList.get(source).getTotalClass();
        return max;
    }
    
    /**
     * 
     * @return Returns the source class name
     */
    public String getSource() {
        return Controller.scList.get(source).sourceName;
    }

    /**
     *
     * @return Returns the target class name
     */
    public String getTarget() {
        return Controller.scList.get(target).sourceName;
    }
}
