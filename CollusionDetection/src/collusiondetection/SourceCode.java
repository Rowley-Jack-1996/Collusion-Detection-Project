package collusiondetection;

import java.util.ArrayList;
import java.util.Arrays;

public class SourceCode extends CodeTemplate{
    
    public SourceCode(ClassStore[] scArray, String name) {
        sourceName = name;
        classArray = scArray;
    }
    
    /*
        Parameter - codeBase (This is the codebase that will be removed from the source code)
        Return - void
        Function -
        Iterate through classArray
        Iterate through codeBase classArray
        Comapre SourceCode ClassArray index name with the classArray index name of codebase
        if they are equal then copy the class strings into string arrays (line by line basis)
        Iterate through lines of sourceCode and iterate through lines of CodeBase
        if they match delete the line from sourceCode and break loop of codeBase code to continue next sourceCode line
    */

    /**
     * This will remove the passed codebase from the source code instance
     * @param codeBase This is the codebase that will be removed from the source code
     */
    
    public void removeBaseCode (CodeBase codeBase) {
        for (int i=0;i<classArray.length;i++) {
            for (int a=0;a<codeBase.getTotalClass();a++) {
                if (classArray[i].getClassName().equals(codeBase.getClassName(a))) {
                    ArrayList<String> scCodeList = new ArrayList(Arrays.asList(classArray[i].getClassRaw().split("\n")));
                    ArrayList<String> cbCodeList = new ArrayList(Arrays.asList(codeBase.getClassRaw(a).split("\n")));
                    for (int scCodeIndex = 0; scCodeIndex<scCodeList.size();scCodeIndex++) {
                        for (int cbCodeIndex=0;cbCodeIndex<cbCodeList.size();cbCodeIndex++) {
                            if (scCodeList.get(scCodeIndex).equals(cbCodeList.get(cbCodeIndex))) {
                                //System.out.println("Matched SC: " + scCodeList.get(scCodeIndex) + " CB: " + cbCodeList.get(cbCodeIndex));
                                scCodeList.remove(scCodeIndex);
                                cbCodeList.remove(cbCodeIndex);
                                scCodeIndex--;
                                break;
                            }
                        }
                    }
                    //Save SC
                    StringBuilder sb = new StringBuilder();
                    for (String scC:scCodeList) {
                        //System.out.println(scC);
                        sb.append(scC);
                        sb.append("\n");
                    }
                    classArray[i].setClassRaw(sb.toString());
                }
            }
        }
    }
}
