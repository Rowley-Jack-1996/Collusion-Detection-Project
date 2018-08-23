/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collusiondetection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    public Results(int source, int target) {
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
        writeToFile();
    }
    
    public Results(SubResult[] results, int source, int target, int largestSWSim) {
        this.results = results;
        this.source = source;
        this.target = target;
        this.largestSWSim = largestSWSim;
    }
    
    public boolean writeToFile() {
        String fileName = Controller.scList.get(source).sourceName + " --- " + Controller.scList.get(target).sourceName;
        File f = new File(Controller.OUTPUTDIR.getAbsoluteFile() + "\\" + fileName);
        f.mkdir();
        for (int i=0;i<results.length;i++) {
            results[i].writeToFile(f.getAbsolutePath() ,"A " + i);
        }
        try {
            FileWriter fwriter = new FileWriter(f.getPath() + "\\Results.txt");
            /*
            1-SubResultsLength
            2-source,target
            3-largestSWResult
            4-NoOfMatchingLines,more
            5-NoOfTotalLines,more
            */
            fwriter.write(Integer.toString(results.length) + "\r\n");
            fwriter.write(Integer.toString(source) + "," + Integer.toString(target) + "\r\n");
            fwriter.write(Integer.toString(largestSWSim) + "\r\n");
            
            String outputTempText = "";
            for (int i=0;i<NoOfSimilarLines.length;i++) {
                outputTempText = outputTempText + Integer.toString(NoOfSimilarLines[i]) + ",";
            }
            fwriter.write(outputTempText.substring(0, outputTempText.length()-1) + "\r\n");
            
            outputTempText = "";
            for (int i=0;i<TotalNoOfLines.length;i++) {
                outputTempText = outputTempText + Integer.toString(TotalNoOfLines[i]) + ",";
            }
            fwriter.write(outputTempText.substring(0, outputTempText.length()-1));
            fwriter.close();
            
            //Controller.pw.println(recordText());
            
        } catch (IOException ex) {
            Logger.getLogger(Results.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return true;
    }  
    
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
            //System.out.println(Controller.similarityTable[source][target][i]);
        }
    }
    
    /*
    private String recordText() {
        String textOutput = Controller.scList.get(source).getSourceName() + "," + Controller.scList.get(target).getSourceName();
        textOutput = textOutput + "/";
        for (int num:NoOfSimilarLines) {
            textOutput = textOutput + Integer.toString(num) + ",";
        }
        textOutput = textOutput.substring(0, textOutput.length()-1) + "/";
        for (int num:TotalNoOfLines) {
            textOutput = textOutput + Integer.toString(num) + ",";
        }
        textOutput = textOutput.substring(0, textOutput.length()-1);
        return textOutput;
    }
    */
    
    /**
     *
     * @param index Index of the desired sub result chain length
     * @return Returns the chain length details 
     * Primary Number - The line number of the primary source
     * Secondary Number - 0 : The starting index of the chain on the secondary source
     * 1 : The length of the chain
     */
    public int[][] getChainLength(int index) {
        return results[index].getChainLength();
    }
    
    //gets length of SubResults stored
    public int getLength() {
        return results.length;
    }
    
    public String getClassText(int targetId, int index) {
        if (targetId == SOURCE) {
            return Controller.scList.get(source).getClassRaw(index);
        } else {
            return Controller.scList.get(target).getClassRaw(index);
        }
    }
    
    public String getClassName(int index) {
        return results[index].getClassName();
    }
    
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
