/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collusiondetection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
    
    public SubResult(String className) {
        this.className = className;
    }
    
    public SubResult(String className, double overallDLevenResult, int[] overallSWHighSim, int[][] chainLengthA, boolean flipped) {
        this.className = className;
        this.overallDLevenResult = overallDLevenResult;
        this.overallSWHighSim = overallSWHighSim;
        this.chainLengthA = chainLengthA;
        this.flipped = flipped;
    }
    
    public boolean writeToFile(String subdir, String name){
        String linetoWrite;
        File f = new File(subdir + "/" + name + ".txt");
        
        try {
            FileWriter fwriter = new FileWriter(f);
            //New Line Character is \r\n
            /*  File Format
            1- className
            2- overallDLevenResult
                3- 1st length of overallSWResult <comma> 2nd Length of ""
                4- data from overallSWResult with each column separeted with commas and rows with new lines
            5- overall SWHighSim with the 3 values separeted with commas
            6- 1st length of chainlengtha <comma> 2nd length of chainlengtha
            7- chainlengtha in a similar manner as overallswresult
            8- boolean result as 0 or 1 (false or true respective)
            */
            
            fwriter.write(className + "\r\n");
            fwriter.write(Double.toString(overallDLevenResult) + "\r\n");
            
            //Need to compensate for a lack of data for this (Maybe print false if there is no data?)
            /*
            fwriter.write(Integer.toString(overallSWResult.length) + "," + Integer.toString(overallSWResult[0].length));
            for (int a=0;a<overallSWResult.length;a++)  {
                linetoWrite = "";
                for (int b=0;b<overallSWResult[a].length;b++) {
                    linetoWrite = linetoWrite + Integer.toString(overallSWResult[a][b]) + ",";
                }
                linetoWrite = linetoWrite.substring(0, linetoWrite.length()-1) + "\r\n";
                fwriter.write(linetoWrite);
            }
            */
            fwriter.write(Integer.toString(overallSWHighSim[0]) + "," + Integer.toString(overallSWHighSim[1]) + "," + Integer.toString(overallSWHighSim[2]) + "\r\n");
            
            //Need to compensate for a lack of data for this (Maybe print false if there is no data?)
            fwriter.write(Integer.toString(chainLengthA.length) + "," + Integer.toString(chainLengthA[0].length) + "\r\n");
            for (int a=0;a<chainLengthA.length;a++)  {
                linetoWrite = "";
                for (int b=0;b<chainLengthA[a].length;b++) {
                    linetoWrite = linetoWrite + Integer.toString(chainLengthA[a][b]) + ",";
                }
                linetoWrite = linetoWrite.substring(0, linetoWrite.length()-1) + "\r\n";
                fwriter.write(linetoWrite);
            }
            if (flipped) {
                fwriter.write("1\r\n");
            } else {
                fwriter.write("0\r\n");
            }
            
            fwriter.close();
        } catch (IOException e) {
            
        }
        
        return true;
    }
    
    public String getClassName() {
        return className;
    }

    public int[] getSWOverallSim() {
        return overallSWHighSim;
    }
    
    public void dLevenCalc(String sourceClass, String targetClass) {
        overallDLevenResult = Analyser.dLeven(sourceClass, targetClass);
    }
    
    public void overallSmWm(String sourceClass, String targetClass) {
        overallSWResult = Analyser.smithWaterman(sourceClass, targetClass);
        overallSWHighSim = Analyser.maxValSW(overallSWResult);
    }
    
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

    public int getOvDLevenResult() {
        return (int)overallDLevenResult;
    }

    public int[][] getChainLength() {
        return chainLengthA;
    }
}
