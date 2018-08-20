package collusiondetection;

public class CodeTemplate {
    
    protected String sourceName;
    protected ClassStore[] classArray;
    
    public CodeTemplate() {
        sourceName = "";
        classArray = new ClassStore[10];
    }
    
    /**
     *
     * @return name of the source of the code (e.g the folder name)
     */
    public String getSourceName () {
        return sourceName;
    }
    
    public String getClassName (int index) {
        return classArray[index].getClassName();
    }
    
    public String getClassRaw (int index) {
        return classArray[index].getClassRaw();
    }
    
    public int getTotalClass() {
        return classArray.length;
    }
}
