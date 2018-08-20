package collusiondetection;

public class ClassStore {
    private String className;
    private String classCode;
    public ClassStore(String className, String classCode) {
        this.className = className;
        this.classCode = classCode;
    }
    
    public String getClassName() {
        return className;
    }
    
    public String getClassRaw() {
        return classCode;
    }
    
    public void setClassRaw(String code) {
        classCode = code;
    }
}
