/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
