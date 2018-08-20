package collusiondetection;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class SourceCodeLoader {
    public SourceCodeLoader () {
    }
    
    public ClassStore[] loadSource(File scFolder) {
        ArrayList<File> al;
        ClassStore[] Output;
        File srcFiles = new File(scFolder + ""); 
        al = genFileDir(srcFiles);
        Output = new ClassStore[al.size()];
        for (int i = 0; i< al.size();i++) {
            Output[i] = new ClassStore(al.get(i).getName().split("\\.")[0], cleanSingleLineComments(cleanMultiComments(readFile(al.get(i)))));
        }

        return Output;
    }
    
    private String readFile(File location) {
        String output = "";
        try {
            Scanner scn = new Scanner(location);
            while (scn.hasNextLine()) {
                output = output + scn.nextLine() + "\n";
            }
            scn.close();
        } catch (Exception FileNotFoundException){
            System.out.println("File not found");
        } finally {
             return output;
        }
    }
    
    public ArrayList<File> genFileDir(File directory) {
        ArrayList<File> fileLocs = new ArrayList();
        for (File f:directory.listFiles()) {
            if (f.isDirectory()) {
                ArrayList<File> temp = genFileDir(f);
                for (File tempFile:temp) {
                    fileLocs.add(tempFile);
                }
            } else {
                if (isJavaFile(f)) {
                    fileLocs.add(f);
                }
            }
        }
        return fileLocs;
    }
    
    public boolean getIfJavaPresent(File directory) {
        boolean output = false;
        for (File f:directory.listFiles()) {
            if (f.isDirectory()) {
                output = getIfJavaPresent(f);
            } else {
                if (isJavaFile(f)) {
                    output = true;
                }
            }
        }
        return output;
    }
    
    private boolean isJavaFile(File fileToTest) {
        String[] output = fileToTest.getName().split("\\.");
        if ((output[output.length-1]).toLowerCase().equals("java")) {
            return true;
        } else {
            return false;
        }
    }
    
    private String cleanSingleLineComments(String s) {
        return s.replaceAll("//.*\n", " \n");
    }
    
    private String cleanMultiComments(String s) {
        return s.replaceAll("/\\*.*\\*/", " ");
    }
}
