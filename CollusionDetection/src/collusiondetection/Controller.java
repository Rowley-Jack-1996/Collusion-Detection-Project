package collusiondetection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

enum resultType {
    dLevelOverall;
}

public class Controller {
    public static final File OUTPUTDIR = new File(System.getProperty("user.dir") + "\\Output");
    //public static File similartyRecord = new File(OUTPUTDIR.getPath() +"\\Record.txt");
    //public static FileWriter fw;
    //public static BufferedWriter bw;
    //public static PrintWriter pw;
    public static final double SIMILARITYNEEDED = 0.8;
    public static final int LINESREQUIRED = 3;
    public static ArrayList<SourceCode> scList;
    static CodeBase codeBase;
    static Interface inf;
    public static double[][][] similarityTable;
    public static double[][] sortedSimilarityList; //index 0: Highest Sim / last index: lowest Sim (Details the source and target indices)
    static SourceCodeLoader sCodeLoader;
    public static boolean Started = false;
    public static boolean closeInit = false;
    public static int currentAmountDone = 0;
    public static int totalToDo;
    public static int maxNumOfClassInputs = 0;
    
    //Debug Variables
    public static final String scInputTextDefault = "C:\\Users\\Jack\\Dropbox\\3rd Year Project\\Test Case - Copy\\SourceCode";
    public static final String cbInputTextDefault = null;

    public static void main(String[] args) throws InterruptedException {
        //Basic initialisation
        OUTPUTDIR.mkdir();
        boolean CBPresent;
        scList = new ArrayList();
        sCodeLoader = new SourceCodeLoader();       
        inf = new Interface(sCodeLoader);
        /*
        try {
            fw = new FileWriter(similartyRecord, true);
            bw = new BufferedWriter(fw);
            pw = new PrintWriter(bw);
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        //Generate Input Interface
        inf.generateInputInterface();
        
        //Wait until the user has finished with input
        do {
            Thread.sleep(25);
        } while (!inf.getStarted());
        
        //Check if codebase was input
        if (inf.getCBDir() == null || inf.getCBDir().equals("")) {
            CBPresent = false;
        } else {
            //If codebase was found, load it from file
            loadCodeBase(inf.getCBDir().getAbsolutePath());
            CBPresent = true;
        }
        //Load source code
        loadSourceCode(inf.getSCDir().getAbsolutePath());
        similarityTable = new double[scList.size()][scList.size()][maxNumOfClassInputs];
        //Remove codebase if present
        for (int i=0;i<scList.size();i++) {
            if (CBPresent) {
                scList.get(i).removeBaseCode(codeBase);
            }
        }
        
        //Now process results
        processSC();
        do {
            inf.updateProgressBarLoadingInterface(currentAmountDone, totalToDo);
            Thread.sleep(250);
        } while (currentAmountDone < totalToDo);

        //Sort similarity table
        sortSimTable();
        
        //Display selection interface
        //pw.close();
        inf.generateOutputInterface();
        inf.closeLoadingInterface();
    }
    
    /*
    This handles the loading of the individual code base from file and the construction of the object
    */
    public static void loadCodeBase(String loc) {
        File selectedFolder = new File(loc);
        codeBase = new CodeBase(sCodeLoader.loadSource(selectedFolder), selectedFolder.getName());
    }
    
    /*
    This handles the loading of the various number of source code folders from files into their own objects
    */
    public static void loadSourceCode(String loc) {
        File selectedFolder[] = new File(loc).listFiles();
        SourceCode temp;
        for (int i = 0; i<selectedFolder.length;i++) {
            if (selectedFolder[i].isDirectory()) {
                temp = new SourceCode(sCodeLoader.loadSource(selectedFolder[i]), selectedFolder[i].getName());
                if (temp.getTotalClass() > maxNumOfClassInputs) {maxNumOfClassInputs = temp.getTotalClass();}
                scList.add(temp);
            }
        }
    }
    
    /*
    Genereates a series of files with the necessary comparisions that have been made
    */
    public static void processSC() {
        totalToDo = ((scList.size()*scList.size()) - scList.size()) / 2;
        inf.generateLoadingInterface(totalToDo);
        for (int a=1;a<scList.size();a++) {
            for(int b=0;b<a;b++) {
                if (!closeInit) {
                    new Results(a,b);
                    currentAmountDone++;
                    inf.updateProgressBarLoadingInterface(currentAmountDone, totalToDo);
                }
            }
        }
    }
    
    private static void sortSimTable() {
        //----Populate List
        int currIndex = 0;
        sortedSimilarityList = new double[totalToDo][maxNumOfClassInputs + 3];
        for (int a=1;a<similarityTable.length;a++) {
            for(int b=0;b<a;b++) {
                double overallPerc = 0.0;
                for (int c=0;c<similarityTable[a][b].length;c++) {
                    overallPerc += similarityTable[a][b][c];
                }
                overallPerc = overallPerc / similarityTable[a][b].length;
                sortedSimilarityList[currIndex][0] = a;
                sortedSimilarityList[currIndex][1] = b;
                sortedSimilarityList[currIndex][2] = overallPerc;
                for (int indivPercIndex = 3; indivPercIndex < similarityTable[a][b].length + 3;indivPercIndex++) {
                    sortedSimilarityList[currIndex][indivPercIndex] = similarityTable[a][b][indivPercIndex-3];
                }
                currIndex++;
            }
        }
        //----Sort List
        double[] tempStorage = new double[sortedSimilarityList[0].length];
        for (int a=0;a<sortedSimilarityList.length;a++) {
            for (int b=0;b<sortedSimilarityList.length-a;b++) {
                if (b == sortedSimilarityList.length - 1) {
                    break;
                } 
                if (sortedSimilarityList[b][2] < sortedSimilarityList[b+1][2]) {
                    for (int c=0;c<tempStorage.length;c++) {
                        tempStorage[c] = sortedSimilarityList[b+1][c];
                        sortedSimilarityList[b+1][c] = sortedSimilarityList[b][c];
                        sortedSimilarityList[b][c] = tempStorage[c];
                    }
                }
            }
        }
        
        for(int i=0;i<sortedSimilarityList.length;i++) {
            for (int a=0;a<sortedSimilarityList[i].length;a++) {
                System.out.print(sortedSimilarityList[i][a] + " : ");
            }
            System.out.println("");
        }
    }
}