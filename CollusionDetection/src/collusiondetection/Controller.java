package collusiondetection;

import java.io.File;
import java.util.ArrayList;

enum resultType {
    dLevelOverall;
}

public class Controller {
    public static final File OUTPUTDIR = new File(System.getProperty("user.dir") + "\\Output");
    public static final double SIMILARITYNEEDED = 0.8;
    public static final int LINESREQUIRED = 3;
    public static ArrayList<SourceCode> scList;
    static CodeBase codeBase;
    static Interface inf;
    static Results[][] resultTable;
    static SourceCodeLoader sCodeLoader;
    public static boolean Started = false;
    public static int currentAmountDone = 0;
    public static int totalToDo;
    
    //Debug Variables
    public static final String scInputTextDefault = "C:\\Users\\Jack\\Dropbox\\3rd Year Project\\Test Case\\SourceCode";
    public static final String cbInputTextDefault = null;

    public static void main(String[] args) throws InterruptedException {
        //Basic initialisation
        OUTPUTDIR.mkdir();
        boolean CBPresent;
        scList = new ArrayList();
        sCodeLoader = new SourceCodeLoader();       
        inf = new Interface(sCodeLoader);
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
        //Remove codebase if present
        for (int i=0;i<scList.size();i++) {
            if (CBPresent) {
                scList.get(i).removeBaseCode(codeBase);
            }
        }
        //scList.trimToSize();
        //Now process results
        processSC();
        do {
            inf.updateProgressBarLoadingInterface(currentAmountDone);
            Thread.sleep(250);
        } while (currentAmountDone < totalToDo);
        
        //Display selection interface
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
        
        for (int i = 0; i<selectedFolder.length;i++) {
            if (selectedFolder[i].isDirectory()) {
                scList.add(new SourceCode(sCodeLoader.loadSource(selectedFolder[i]), selectedFolder[i].getName()));
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
                new Results(a,b).run();
                inf.updateProgressBarLoadingInterface(currentAmountDone);
            }
        }
    }
}