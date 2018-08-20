/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collusiondetection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Jack
 */
public class OutputLoader {
    public static void OutputLoader() {
    }
    
    public static Results loadOutput(String a, String b) {
        
        //Setting File Filter
        FilenameFilter fnf = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                //Checking if the current directory has both of the targets
                if (name.equals(a + " --- " + b) || name.equals(b + " --- " + a)) {
                    return true;
                }
                return false;
            }
        };
        //Generates a list of matching directories (should only be one)
        File[] fileList = Controller.OUTPUTDIR.listFiles(fnf);
        
        //--ResultsVariables
        Results r;
        int SubResultsLength, source = 0, target = 0, largestSWResult = 0;
        
        //--SubResult variables
        ArrayList<SubResult> subResults = new ArrayList<>();
        String className;
        double overallDLevenResult;
        int[] overallSWHighSim;
        int[][] chainLengthA;
        boolean flipped;
        
        try {
            File[] innerFiles = fileList[0].listFiles();
            Scanner sc;
            for (File f:innerFiles) {
                if (f.getName().equals("Results.txt")) {
                    sc = new Scanner(f);
                    SubResultsLength = Integer.parseInt(sc.nextLine());
                    String[] tempLine = sc.nextLine().split(",");
                    source = Integer.parseInt(tempLine[0]);
                    target = Integer.parseInt(tempLine[1]);
                    largestSWResult = Integer.parseInt(sc.nextLine());
                    sc.close();
                } else {
                    sc = new Scanner(f);
                    className = sc.nextLine(); //Class Name
                    overallDLevenResult = Double.parseDouble(sc.nextLine()); //Overall Dleven result
                    String[] tempLine = sc.nextLine().split(",");
                    overallSWHighSim = new int[3]; //High Sim copy
                    overallSWHighSim[0] = Integer.parseInt(tempLine[0]);
                    overallSWHighSim[1] = Integer.parseInt(tempLine[1]);
                    overallSWHighSim[2] = Integer.parseInt(tempLine[2]);
                    tempLine = sc.nextLine().split(",");
                    int[] arrayDims = new int[2]; 
                    arrayDims[0] = Integer.parseInt(tempLine[0]);
                    arrayDims[1] = Integer.parseInt(tempLine[1]);
                    chainLengthA = new int[arrayDims[0]][arrayDims[1]];
                    for (int i=0;i<arrayDims[0];i++) {
                        tempLine = sc.nextLine().split(",");
                        chainLengthA[i][0] = Integer.parseInt(tempLine[0]);
                        chainLengthA[i][1] = Integer.parseInt(tempLine[1]);
                        chainLengthA[i][2] = Integer.parseInt(tempLine[2]);
                    }
                    if (sc.nextLine().equals("1")) {
                        flipped = true;
                    } else {
                        flipped = false;
                    }
                    sc.close();
                    subResults.add(new SubResult(className, overallDLevenResult, overallSWHighSim, chainLengthA, flipped));
                }
            }
            subResults.trimToSize();
            SubResult[] srArray = new SubResult[subResults.size()];
            subResults.toArray(srArray);
            return new Results(srArray,source, target, largestSWResult);
            
        } catch (FileNotFoundException e) {
            // Need to handle when the file isnt found
        }
        
        return null;
    }
}
