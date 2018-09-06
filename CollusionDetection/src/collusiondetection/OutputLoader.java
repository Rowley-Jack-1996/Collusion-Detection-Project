package collusiondetection;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jack
 */
public class OutputLoader {
    
    /**
     * Used to read from the output file and package the data read into a Results object to be used to display the results.
     * @param a This is the name of the source code folder that you want to read.
     * @param b This is the name of the target source code folder that you want to read.
     * @return Returns an Results object with the relevant data.
     */
    public static Results loadOutputNotationBuffReader(String a, String b) {
        boolean SectionNotFound = true;
        
        int SubResultsLength, source = 0, target = 0, largestSWResult = 0;
        int[] NoOfSimilarLines;
        int[] TotalNoOfLines;
        try {
            FileReader fr = new FileReader(Controller.OutputFile);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            do { //Search through file for relevant section
                line = br.readLine();
                if (line.equals("<" + a + " --- " + b + ">") || 
                        line.equals("<" + b + " --- " + a + ">")) {
                    SectionNotFound = false;
                }
            } while(SectionNotFound);
            //Once found copy information
            br.readLine();; //Skip <Results> tag
            SubResultsLength = Integer.parseInt(br.readLine().trim());
            String[] tempLine = br.readLine().trim().split(",");
            source = Integer.parseInt(tempLine[0]);
            target = Integer.parseInt(tempLine[1]);
            largestSWResult = Integer.parseInt(br.readLine().trim());
            tempLine = br.readLine().trim().split(",");
            NoOfSimilarLines = new int[tempLine.length];
            for (int i=0;i<NoOfSimilarLines.length;i++) {
                NoOfSimilarLines[i] = Integer.parseInt(tempLine[i]);
            }
            tempLine = br.readLine().trim().split(",");
            TotalNoOfLines = new int[tempLine.length];
            for (int i=0;i<TotalNoOfLines.length;i++) {
                TotalNoOfLines[i] = Integer.parseInt(tempLine[i]);
            }
            //Begin copying data for SubResults
            boolean endOfRecord = false;
            //--SubResult variables
            ArrayList<SubResult> subResults = new ArrayList<>();
            String className;
            double overallDLevenResult;
            int[] overallSWHighSim;
            int[][] chainLengthA;
            boolean flipped;
            
            do {
                line = br.readLine();
                if (line.substring(0, 1).equals("<") || (line.equals(""))) { //Ternmination Condition
                    endOfRecord = true;
                    break;
                }
                line = line.trim();
                className = line.substring(1, line.length()-1);
                overallDLevenResult = Double.parseDouble(br.readLine().trim());
                tempLine = br.readLine().trim().split(",");
                overallSWHighSim = new int[tempLine.length];
                for (int i=0;i<tempLine.length;i++) {
                    overallSWHighSim[i] = Integer.parseInt(tempLine[i]);
                }
                tempLine = br.readLine().trim().split(",");
                chainLengthA = new int[Integer.parseInt(tempLine[0])][Integer.parseInt(tempLine[1])];
                for (int i=0;i<chainLengthA.length;i++) {
                    tempLine = br.readLine().trim().split(",");
                    chainLengthA[i][0] = Integer.parseInt(tempLine[0]);
                    chainLengthA[i][1] = Integer.parseInt(tempLine[1]);
                    chainLengthA[i][2] = Integer.parseInt(tempLine[2]);
                }
                if (br.readLine().equals("1")) {
                    flipped = true;
                } else {
                    flipped = false;
                }
                subResults.add(new SubResult(className, overallDLevenResult, overallSWHighSim, chainLengthA, flipped));
            } while (!endOfRecord);
            
            br.close();
            fr.close();
            
            subResults.trimToSize();
            SubResult[] srArray = new SubResult[subResults.size()];
            subResults.toArray(srArray);
            return new Results(srArray,source, target, largestSWResult, NoOfSimilarLines, TotalNoOfLines);
        } catch (FileNotFoundException ex) {
            
        } catch (IOException ex) {
            Logger.getLogger(OutputLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
}
