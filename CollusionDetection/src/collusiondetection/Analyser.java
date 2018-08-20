/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collusiondetection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Jack
 */
public class Analyser implements Serializable {

    public static final int GAPPEN = -2; //Gap Penalty
    public static final int MATCHSCORE = 3; //Match score

    public Analyser() {

    }

    public static double dLeven(String class1, String class2) {
        if (class1 == null || class2 == null) {

        }
        if (class1.equals(class2)) {
            return 0;
        }

        int inf = class1.length() + class2.length();

        HashMap<Character, Integer> da = new HashMap<>();
        char[] c1CharA = class1.toCharArray();
        char[] c2CharA = class2.toCharArray();
        for (int i = 0; i < c1CharA.length; i++) {
            da.put(c1CharA[i], 0);
        }
        for (int i = 0; i < c2CharA.length; i++) {
            da.put(c2CharA[i], 0);
        }

        int[][] h = new int[class1.length() + 2][class2.length() + 2];

        for (int i = 1; i <= class1.length() + 1; i++) {
            h[i][0] = inf;
            h[i][1] = i - 1;
        }

        for (int i = 1; i <= class2.length() + 1; i++) {
            h[0][i] = inf;
            h[1][i] = i - 1;
        }

        for (int a = 1; a <= class1.length(); a++) {
            int db = 0;
            for (int b = 1; b <= class2.length(); b++) {
                int a1 = da.get(c2CharA[b - 1]);
                int b1 = db;

                int d = 1;
                if (c1CharA[a - 1] == c2CharA[b - 1]) {
                    d = 0;
                    db = b;
                }
                h[a + 1][b + 1] = Math.min(
                        h[a][b] + d,
                        Math.min(h[a + 1][b] + 1,
                                Math.min(h[a][b + 1] + 1,
                                        h[a1][b1] + (a - a1 - 1) + 1 + (b - b1 - 1)
                                )));

            }
            da.put(c1CharA[a - 1], a);
        }
        return h[class1.length() + 1][class2.length() + 1];
    }

    public static int[][] SubResultSW(String[] class1Lines, String[] class2Lines) {
        int[][] chainLengthA;

        ArrayList<ArrayList<int[][]>> Store = new ArrayList<>();
        //Build results table
        for (int a = 0; a < class1Lines.length; a++) {
            if (class1Lines[a].equals("")) {
                class1Lines[a] = " ";
            }
            ArrayList<int[][]> tempStore = new ArrayList<>();
            for (int b = 0; b < class2Lines.length; b++) {
                if (class2Lines[b].equals("")) {
                    class2Lines[b] = " ";
                }
                int[][] tempCarrier = Analyser.smithWaterman(class1Lines[a], class2Lines[b]);
                tempStore.add(tempCarrier);
            }
            Store.add(tempStore);
        }

        //Max size is the largest length of the 2nd class array (this was done incase one happens to be larger to ensure no index errors occur)
        int maxSize = 0;
        for (int i = 0; i < Store.size(); i++) {
            if (Store.get(i).size() > maxSize) {
                maxSize = Store.get(i).size();
            }
        }

        //Init maxScores
        /*
        1d = length of the lines in Store (no of class1 lines)
        2d = max number of lines in store (no of class2 lines)
        It stores the highest value found in each smith waterman table for each line
        this allows me to check how similar 1 line in class 1 is to all other lines in class2
         */
        int[][] maxScores = new int[Store.size()][maxSize];

        //Populate maxScores
        for (int a = 0; a < Store.size(); a++) {
            for (int b = 0; b < Store.get(a).size(); b++) {
                //System.out.println("Line 1 : '" + class1Lines[a] + "' index: " + a + ", Line 2: '" + class2Lines[b] + "' index: " + b);
                if (class1Lines[a].trim().equals("") || class2Lines[b].trim().equals("")) {
                    //System.out.println("Line 1 : '" + class1Lines[a].trim() + "' index: " + a + ", Line 2: '" + class2Lines[b].trim() + "' index: " + b);
                    maxScores[a][b] = -1;
                } else {
                    maxScores[a][b] = Analyser.maxValSW(Store.get(a).get(b))[2];
                }
            }
        }

        /*
        Secondary number is :
        0 - starting index of the chain on the primary source
        1 - the starting index of the chain on the secondary source
        2- the length of the chain
         */
 /*
        There are issues with the chainlength calculation. I think that the major issue is to do with
        the calculation of what the next highest value line is. This doesnt take into account
        lines that have multiple similar lines, e.g } or try { / catch (Exception e) {
        
        Solution
        1: Build a sorted array and check if it is within a top percentile of the options
        2: Check the similarity and check if the similarity is within a certain percent difference
        These two methods require me to focus specifically on the next line and not on the best line found
        Which in hindsight is a more reliable approach
         */
        chainLengthA = new int[maxScores.length][3];

        /* Following for loop explained
        iterate through the results of class 1 line by line
        store the highest similarity found (this is stored as the index of the line with the highest similarity
        int array s is initialised with the for loop value, the index of the highest sim, and a starting chainlength 1
        chainlength array is populated with the x/y index and the chainlength from recurScoreLength
         */

        /* Changes required
        iterate through the results of class 1 line by line
        create a list of all indices with a sufficently high similarity 
        iterate through each of these 
        calculate the chain length in a fuzzy manner (if the similarity is sufficently high)
        Once the highest chainlength index has been found store the results for that chain and move on with the loop
         */
        for (int a = 0; a < maxScores.length; a++) {
            ArrayList<int[]> similarIndex = new ArrayList(); //Stores the chain details for each line that is similar enough
            for (int b = 0; b < maxScores[a].length; b++) {
                double simiPerc = maxScores[a][b] / Analyser.maxAlignmentSW(class1Lines[a], class2Lines[b]);
                if (simiPerc > Controller.SIMILARITYNEEDED) { //The lines are similar enough
                    int[] tempChain = new int[4];
                    tempChain[0] = a;
                    tempChain[1] = b;
                    tempChain[2] = 1;
                    tempChain[3] = 0; //Index of last actual match
                    tempChain[2] = recurScoreLength(tempChain, maxScores, class1Lines, class2Lines);
                    similarIndex.add(tempChain);
                }
            }
            // Find which has the highest chain length and use that as the 'official' connection
            int maxChainLength = 0;
            int index = 0;
            for (int i = 0; i < similarIndex.size(); i++) {
                if (similarIndex.get(i)[2] > maxChainLength) {
                    maxChainLength = similarIndex.get(i)[2];
                    index = i;
                }
            }
            //System.out.println("Current Loop a:" + a);
            
            if (similarIndex.size() != 0) {
                if (similarIndex.get(index)[2] >= 3) {
                    chainLengthA[a][0] = similarIndex.get(index)[0];
                    chainLengthA[a][1] = similarIndex.get(index)[1];
                    chainLengthA[a][2] = similarIndex.get(index)[2];
                }
            } else {
                chainLengthA[a][0] = -1;
                chainLengthA[a][1] = -1;
                chainLengthA[a][2] = -1;  
            }
        }
        /*
        Store is used to store the results for the SmithWaterman algorithm for each line 
        The first array links to a specific line on the class1 side
        the second arraylsit links to all lines on the class2 side and the int[][] stores the SW results
         */
        return chainLengthA;
    }

    private static int recurScoreLength(int[] s, int[][] maxScores, String[] class1Lines, String[] class2Lines) {
        int x = s[0] + 1; //This shifts the next line being looked at 
        int y = s[1] + 1;

        //Check boundaries
        if (x >= maxScores.length || y >= maxScores[x].length) {
            return s[3];
        }

        //Changed from original now this will check the line and make sure it has a reasonably high similarity
        //If the line is similar enough the chain can continue
        double simiPerc = maxScores[x][y] / Analyser.maxAlignmentSW(class1Lines[x], class2Lines[y]);
        if (simiPerc > Controller.SIMILARITYNEEDED) { //The lines are similar enough
            int[] temp = {x, y, s[2] + 1, s[2] + 1};
            return recurScoreLength(temp, maxScores, class1Lines, class2Lines);
        } else if (maxScores[x][y] == -1) {
            int[] temp = {x, y, s[2] + 1 , s[3]};
            return recurScoreLength(temp, maxScores, class1Lines, class2Lines);
        } else {
            return s[3];
        }
        
    }

    public static int[][] smithWaterman(String class1, String class2) {
        String class1Input = class1;
        String class2Input = class2;
        int[][] h = new int[class1Input.length() + 1][class2Input.length() + 1];
        String[] class1Arr = class1Input.split("");
        String[] class2Arr = class2Input.split("");

        for (int i = 0; i < class1Input.length() + 1; i++) {
            h[i][0] = 0;
        }
        for (int i = 1; i < class2Input.length() + 1; i++) {
            h[0][i] = 0;
        }

        int Diag = 0;
        int Top = 0;
        int Left = 0;

        for (int a = 1; a < class1Input.length() + 1; a++) {
            for (int b = 1; b < class2Input.length() + 1; b++) {
                Diag = h[a - 1][b - 1] + alignment(class1Arr[a - 1], class2Arr[b - 1]);
                Top = h[a - 1][b] + GAPPEN;
                Left = h[a][b - 1] + GAPPEN;

                h[a][b] = Math.max(Diag, Math.max(Top, Math.max(Left, 0)));
            }
        }

        /*Create substitution matrix (instead of this i will compare equality)
        Specify Gap penalty (Global Variable)
        Construct Scoring Matrix h and init first row and first column (0)
        The size of the scoring matrix is 1+class1.length * 1+class2.length
        Fill the matrix using the math max formula
        Perform Traceback
        */
        return h;
    }

    private static int alignment(String a, String b) {
        if (a.equals(b)) {
            return MATCHSCORE;
        } else {
            return GAPPEN;
        }
    }

    /**
     * A possible method to quickly calculate the highest possible score the SW
     * algorithm can return if the smaller string is a direct substring of the
     * longer. This is used to generate a maximum 'likeness' value to compare
     * the true result against
     *
     * @param a The two strings to compare
     * @param b
     * @return The maximum possible alignment score
     */
    public static int maxAlignmentSW(String a, String b) {
        String[] classAArr = a.split("");
        String[] classBArr = b.split("");
        int output = 0;

        if (classAArr.length > classBArr.length) {
            output = classBArr.length * MATCHSCORE;
            //int temp = (classAArr.length - classBArr.length) * -GAPPEN;
            //output = output - temp;
        } else {
            output = classAArr.length * MATCHSCORE;
            //int temp = (classBArr.length - classAArr.length) * -GAPPEN;
            //output = output - temp;
        }
        return output;
    }

    //This traceback may need to be redone using recursion to find the furthest path possible instead of the current implementation
    /*
    This will require a new function
    Traceback will be called and start the traceback, it will check all three avenues and continue probing and eventually return a total length
     */

    /*
    Output 
    0 - X index
    1 - Y index
    2 - Max Value found
     */
    
    /**
     * This takes the results table from Smith-Waterman and uses it to find the
     * largest value in the results table
     *
     * @param h
     * @return integer array. 0-X index. 1-Y index. 2-The actual value
     */
    public static int[] maxValSW(int[][] h) {
        int[] output = new int[3];

        //Iterate to find the largest score and save its index
        for (int a = 0; a < h.length; a++) {
            for (int b = 0; b < h[0].length; b++) {
                if (h[a][b] > output[2]) {
                    output[2] = h[a][b];
                    output[0] = a;
                    output[1] = b;
                }
            }
        }

        return output;
    }

    public static ArrayList traceback(int[][] h) {
        ArrayList output = new ArrayList();
        boolean Complete = false;

        int[] max = maxValSW(h);

        int currA = max[0];
        int currB = max[1];

        int topVal = 0;
        int leftVal = 0;
        int diagVal = 0;
        int maxVal = 0;

        do {
            topVal = h[currA - 1][currB];
            leftVal = h[currA][currB - 1];
            diagVal = h[currA - 1][currB - 1];

            maxVal = Math.max(topVal, Math.max(leftVal, diagVal));

            if (maxVal == 0) {
                Complete = true;
                break;
            }

            //Check for zero first?
            //This is the terminating condition where Complete = false;
            if (diagVal == maxVal) {
                currA -= 1;
                currB -= 1;
                int[] a = {currA, currB};
                output.add(a);
            } else if (topVal == maxVal) {
                currA -= 1;
                int[] a = {currA, currB};
                output.add(a);
            } else if (leftVal == maxVal) {
                currB -= 1;
                int[] a = {currA, currB};
                output.add(a);
            }

        } while (!Complete);

        return output;
    }
}
