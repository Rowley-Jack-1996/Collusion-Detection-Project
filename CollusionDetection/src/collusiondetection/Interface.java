/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collusiondetection;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Jack
 */
public class Interface extends JFrame{
    //---------- Variables for Input Interface
    ArrayList<File> DirFileList;
    SourceCodeLoader scl;
    boolean Started;
    File codeBaseDir;
    File sourceCodeDir;
    Button startBtn;
    JFrame inputWind;
    TextField CBDir;
    JList sourceNames;
    TextField SCDir;
    JList sourceFilesNames;
    //---------- Variables for Loading Interface
    JFrame loadingWind;
    JProgressBar loadingBar;
    //---------- Variables for Output Interface
    String[][] OutputNameList;
    JFrame outputWind;
    JList sourceCodeListOutput;
    JList targetCodeListOutput;
    //---------- Variables for the results interface
    
    public Interface(SourceCodeLoader scl) {
        this.scl = scl;
    }
    
    public boolean getStarted() {
        return Started;
    }
    
    public File getSCDir() {
        return sourceCodeDir;
    }
    
    public File getCBDir() {
        return codeBaseDir;
    }
    
    private void displaySCDirectories(File f) {
        DirFileList = new ArrayList<>();
        ArrayList<String> DirList = new ArrayList<>();
        File[] SubFiles = f.listFiles();
        try {
            for (int i=0;i<SubFiles.length;i++) {
                if (SubFiles[i].isDirectory()) {
                    DirList.add(SubFiles[i].getName());
                    DirFileList.add(SubFiles[i]);
                }
            }
            sourceNames.removeAll();
            sourceNames.setListData(DirList.toArray());
            sourceNames.setSelectedIndex(0);
            generateSCFileList(DirFileList.get(0));
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(new JFrame(), "You have not selected an appropriate directory. Please select a directory containing java source code", "File Not Found", JOptionPane.ERROR_MESSAGE);
        }
        
    }
    
    private void generateSCFileList(File f) {
        ArrayList<File> InnerFiles = scl.genFileDir(f);
        String[] FilesListTxt = new String[InnerFiles.size()];
        for (int i=0;i<FilesListTxt.length;i++) {
            FilesListTxt[i] = InnerFiles.get(i).getName();
        }
        sourceFilesNames.removeAll();
        sourceFilesNames.setListData(FilesListTxt);
    }
    
    public void generateInputInterface() {
        Border padding = BorderFactory.createEmptyBorder(5, 10, 5, 10);
        Dimension BrowseButtonSize = new Dimension(50,20);
        Dimension InputPnlDim = new Dimension(250, 50);
        
        inputWind = new JFrame("Java Collusion Input");
        inputWind.setResizable(false);
        inputWind.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        inputWind.setLayout(new BoxLayout(inputWind.getContentPane(), BoxLayout.Y_AXIS));
        //getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        
        JPanel InputPnl = new JPanel(); //Input Container
        InputPnl.setBorder(padding);
        InputPnl.setLayout(new BoxLayout(InputPnl, BoxLayout.X_AXIS));
        inputWind.add(InputPnl);
        
        
        //----------//Leftside Panel//----------//
        JPanel leftPnl = new JPanel(); 
        leftPnl.setBorder(padding);
        leftPnl.setLayout(new BoxLayout(leftPnl, BoxLayout.Y_AXIS));
        InputPnl.add(leftPnl);
        
        JPanel TopInputLeft = new JPanel(); //Directory Specify Panel
        TopInputLeft.setLayout(new BoxLayout(TopInputLeft, BoxLayout.Y_AXIS));
        TopInputLeft.setPreferredSize(InputPnlDim);
        TopInputLeft.setMaximumSize(InputPnlDim);
        leftPnl.add(TopInputLeft);
        
        //-------------------------------------------------------
        Label SelectSourceCodeDir = new Label("Source Code Directory");
        TopInputLeft.add(SelectSourceCodeDir);
        
        JPanel SCInput = new JPanel(); //Source Code Input Panel
        SCInput.setLayout(new BoxLayout(SCInput, BoxLayout.X_AXIS));
        TopInputLeft.add(SCInput);
        
        SCDir = new TextField(); //Code Base Directory and Browse Button
        SCDir.setText(Controller.scInputTextDefault);
        SCDir.addActionListener(new SCDirListen());
        SCInput.add(SCDir);
        Button SCBrowse = new Button("Browse");
        SCBrowse.addActionListener(new SourceCodeBrowse());
        SCBrowse.setPreferredSize(BrowseButtonSize);
        SCBrowse.setMaximumSize(BrowseButtonSize);
        SCInput.add(SCBrowse);
        //-------------------------------------------------------
        
        Label SCListlbl = new Label("Source Code");
        leftPnl.add(SCListlbl);
        
        JScrollPane sourceNameScroll = new JScrollPane();
        leftPnl.add(sourceNameScroll);
        //String[] testNames = {"Zero", "One", "Two", "Three", "Four"}; //Source Code List
        sourceNames = new JList();
        sourceNameScroll.setViewportView(sourceNames);
        
        //----------//Rightside Panel//----------//
        JPanel rightPnl = new JPanel();
        rightPnl.setBorder(padding);
        rightPnl.setLayout(new BoxLayout(rightPnl, BoxLayout.Y_AXIS));
        InputPnl.add(rightPnl);
        
        JPanel TopInputRight = new JPanel(); //Directory Specify Panel (Source Code)
        TopInputRight.setLayout(new BoxLayout(TopInputRight, BoxLayout.Y_AXIS));
        TopInputRight.setPreferredSize(InputPnlDim);
        TopInputRight.setMaximumSize(InputPnlDim);
        rightPnl.add(TopInputRight);
        
        //-------------------------------------------------------
        Label SelectCodeBaseDir = new Label("Code Base Directory (Optional)");
        TopInputRight.add(SelectCodeBaseDir);
        
        JPanel CBInput = new JPanel(); //Code Base Input Section
        CBInput.setLayout(new BoxLayout(CBInput, BoxLayout.X_AXIS));
        TopInputRight.add(CBInput);
        
        CBDir = new TextField(); //Code Base Directory and Browse Button
        CBDir.setText(Controller.cbInputTextDefault);
        CBInput.add(CBDir);
        Button CBBrowse = new Button("Browse");
        CBBrowse.addActionListener(new CodeBaseBrowse());
        CBBrowse.setPreferredSize(BrowseButtonSize);
        CBBrowse.setMaximumSize(BrowseButtonSize);
        CBInput.add(CBBrowse);
        
        //-------------------------------------------------------
        
        Label SCFileListlbl = new Label("Source Code Files");
        rightPnl.add(SCFileListlbl);
        
        JScrollPane sourceFileScroll = new JScrollPane();
        rightPnl.add(sourceFileScroll);
        //String[] testFileNames = {"Zero", "One", "Two", "Three", "Four"}; //Source Code List
        sourceFilesNames = new JList();
        sourceFileScroll.setViewportView(sourceFilesNames);

        
        startBtn = new Button("Start"); //Start Button
        Dimension startDim = new Dimension(80,20);
        startBtn.addActionListener(new StartButtonListener());
        startBtn.setPreferredSize(startDim);
        startBtn.setMaximumSize(startDim);
        inputWind.add(startBtn);
        
        
        
        inputWind.pack();
        //setSize(250, 100);
        //addWindowListener(new WindowAdapter() {
        //    public void windowClosing(WindowEvent e) {
        //        dispose();
        //    }
        //});
        
        inputWind.setVisible(true);
    }
    
    private boolean delDir(File DirToDel) {
        File[] Files = DirToDel.listFiles();
        if (Files != null) {
            for (File f:Files) {
                delDir(f);
            }
        }
        return DirToDel.delete();
    }
    
    public void generateLoadingInterface(int lengthOfTask) {
        //Border padding = BorderFactory.createEmptyBorder(5, 10, 5, 10);
        //Dimension BrowseButtonSize = new Dimension(50,20);
        //Dimension InputPnlDim = new Dimension(250, 50);
        
        loadingWind = new JFrame("Loading");
        loadingWind.setResizable(false);
        loadingWind.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(loadingWind, "Do you wish to exit the application?") == JOptionPane.YES_OPTION) {
                    delDir(Controller.OUTPUTDIR);
                    System.exit(0);
                }
            }
        });
        
        loadingWind.setLayout(new BoxLayout(loadingWind.getContentPane(), BoxLayout.Y_AXIS));
        loadingBar = new JProgressBar(0, lengthOfTask);
        loadingBar.setValue(0);
        loadingWind.add(loadingBar);
        
        loadingWind.pack();
        
        loadingWind.setVisible(true);
    }
    
    public void updateProgressBarLoadingInterface(int newValue) {
        loadingBar.setValue(newValue);
    }
    
    public void closeLoadingInterface() {
        loadingWind.setVisible(false);
        loadingWind.dispose();
    }
    
    /**
     * This generates the ResultsTableAL list which is used to list all of the source code for the output window.
     * This also sorts the target comparisons in Smith Waterman peak similarity order.
     */
    public void generateSourceCodeListOutput() {
        //ResultsTableAL = new ArrayList<>();
        OutputNameList = new String[Controller.scList.size()][Controller.scList.size()];
        int index;
        
        for (int i=0;i<Controller.scList.size();i++) {
            index = 1;
            OutputNameList[i][0] = Controller.scList.get(i).getSourceName();
            for (int a=0;a<Controller.scList.size();a++) {
                if (!Controller.scList.get(a).getSourceName().equals(OutputNameList[i][0])) {
                    OutputNameList[i][index] = Controller.scList.get(a).getSourceName();
                    index++;
                }
            }
        }

//        for (int i=0;i<Controller.scList.size();i++) {
//            Results[] temp = new Results[resultTable.length-1];
//            int currIndex = 0;
//            String sourceName = scList.get(i).getSourceName();
//            // Copy all results that compare with the source
//            for (int a=1;a<resultTable.length;a++) {
//                for (int b=0;b<a;b++) {
//                    if (resultTable[a][b].getSource().equals(sourceName) || resultTable[a][b].getTarget().equals(sourceName)) {
//                        temp[currIndex] = resultTable[a][b];
//                        currIndex++;
//                    }
//                }
//            }
//            //Found all results that use the source
//            //Sort them into decending order
//            
//            for (int a=0;a<temp.length;a++) {
//                for (int b=0;b<temp.length;b++) {
//                    try {
//                        if (temp[b].getSWOverallSim() < temp[b+1].getSWOverallSim()) {
//                            Results tempCopy = temp[b+1];
//                            temp[b+1] = temp[b];
//                            temp[b] = tempCopy;
//                        }
//                    } catch (IndexOutOfBoundsException e) {
//                    
//                    }
//                }
//            }
//            
//            //Should be sorted
//            
//            ResultsTableAL.add(temp);
//        }

        //Finished list of all the source code
    }
    
    public void generateOutputInterface() {
        Border padding = BorderFactory.createEmptyBorder(5, 10, 5, 10);
        
        outputWind = new JFrame();
        outputWind.setTitle("Results");
        outputWind.setResizable(false);
        outputWind.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(outputWind, "Do you wish to exit the application?") == JOptionPane.YES_OPTION) {
                    delDir(Controller.OUTPUTDIR);
                    System.exit(0);
                }
            }
        });
        
        outputWind.setLayout(new BoxLayout(outputWind.getContentPane(), BoxLayout.Y_AXIS));
        
        JPanel MainContainer = new JPanel(); //Main Container
        MainContainer.setBorder(padding);
        MainContainer.setLayout(new BoxLayout(MainContainer, BoxLayout.X_AXIS));
        outputWind.add(MainContainer);
        
        //----------Left Side----------//
        JPanel LeftSideContainer = new JPanel();
        LeftSideContainer.setLayout(new BoxLayout(LeftSideContainer, BoxLayout.Y_AXIS));
        MainContainer.add(LeftSideContainer);
        
        Label sclbl = new Label("Source Code: ");
        LeftSideContainer.add(sclbl);
        
        JScrollPane sourceCodeScroll = new JScrollPane();
        LeftSideContainer.add(sourceCodeScroll);
        
        generateSourceCodeListOutput();
        String[] scListString = new String[OutputNameList.length];
        
        for(int i=0;i<OutputNameList.length;i++) {
            scListString[i] = OutputNameList[i][0];
        }
        
        sourceCodeListOutput = new JList(scListString);
        sourceCodeListOutput.addListSelectionListener(new SourceCodeOutputListListener());
        sourceCodeScroll.setViewportView(sourceCodeListOutput);
        sourceCodeListOutput.setSelectedIndex(0);
        
        //----------Right Side----------//
        JPanel RightSideContainer = new JPanel();
        RightSideContainer.setLayout(new BoxLayout(RightSideContainer, BoxLayout.Y_AXIS));
        MainContainer.add(RightSideContainer);
        
        Label tclbl = new Label("Target Code: ");
        RightSideContainer.add(tclbl);
        
        JScrollPane targetCodeScroll = new JScrollPane();
        RightSideContainer.add(targetCodeScroll);
        targetCodeListOutput = new JList();
        targetCodeScroll.setViewportView(targetCodeListOutput);
        
        //--------------------------------------
        //System.out.println(sourceCodeListOutput.getSelectedIndex());
        String source = (String)sourceCodeListOutput.getSelectedValue();
        int index = sourceCodeListOutput.getSelectedIndex();
        String[] targetCode = new String[OutputNameList[index].length - 1];
                
        for (int i=1;i<OutputNameList[index].length;i++) {
            targetCode[i-1] = OutputNameList[index][i];
        }
  
        targetCodeListOutput.removeAll();
        targetCodeListOutput.setListData(targetCode);
        //--------------------------------------
        Button ResultBtn = new Button("Results");
        ResultBtn.addActionListener(new ResultBtnListener());
        outputWind.add(ResultBtn);
        
        outputWind.pack();
        outputWind.setVisible(true);
    }
    
    class ResultsInterface extends JFrame {
        String[] RegionTxt;
        JFrame ResultsWind;
        Results result;
        JList SourceCodeTextArea;
        JList TargetCodeTextArea;
        JComboBox srcSelectDropDown;
        JComboBox RegionDropDown;
        Label OverallEditDist;
        ArrayList<int[]> DDList; //0 is Source index; 1 is target index; 2 is length
        
        public ResultsInterface(int index1, int index2) {
            result = new Results(index1, index2);
            Border padding = BorderFactory.createEmptyBorder(5, 10, 5, 10);
        
            ResultsWind = new JFrame();
            ResultsWind.setTitle("Results : " + Controller.scList.get(index1).getSourceName() + " - " + Controller.scList.get(index2).getSourceName());
            ResultsWind.setLocationRelativeTo(null);
            ResultsWind.addWindowListener(new CloseListener());
            ResultsWind.setResizable(false);
            
            ResultsWind.setLayout(new BoxLayout(ResultsWind.getContentPane(), BoxLayout.Y_AXIS));
            
            
            //---------- Source Select Panel ----------
            JPanel SourceSelectPanel = new JPanel();
            SourceSelectPanel.setLayout(new BoxLayout(SourceSelectPanel, BoxLayout.X_AXIS));
            //SourceSelectPanel.setMaximumSize(PanelSize);
            //SourceSelectPanel.setPreferredSize(PanelSize);
            ResultsWind.add(SourceSelectPanel);
            
            Label srcSelect = new Label("Source Selector: ");
            SourceSelectPanel.add(srcSelect);
            
            //-Gen dropdown list-
            String[] classTxt = new String[result.getLength()];
            for (int i=0;i<classTxt.length;i++) {
                classTxt[i] = result.getClassName(i);
            }
            srcSelectDropDown = new JComboBox(classTxt);
            srcSelectDropDown.setLightWeightPopupEnabled(false);
            srcSelectDropDown.setSelectedIndex(0);
            srcSelectDropDown.addActionListener(new srcSelectDropDownListener());
            SourceSelectPanel.add(srcSelectDropDown);
            
            //---------- Top Line Panel ----------
            JPanel TopRowPanel = new JPanel();
            //TopRowPanel.setLayout(new BoxLayout(TopRowPanel, BoxLayout.X_AXIS));
            TopRowPanel.setLayout(new GridLayout(1, 2));
            ResultsWind.add(TopRowPanel);
            
            JPanel RegionSelectPanel = new JPanel(new FlowLayout(FlowLayout.LEADING,0,0));
            TopRowPanel.add(RegionSelectPanel);
            Label matchRegionlbl = new Label("Matching Regions");
            RegionSelectPanel.add(matchRegionlbl);
            RegionDropDown = new JComboBox();
            RegionDropDown.setLightWeightPopupEnabled(false);
            RegionSelectPanel.add(RegionDropDown);
            genRegionDropDownInfo();
            
            OverallEditDist = new Label("Overall Edit Distance: " + result.getDLevenOverall(0));
            TopRowPanel.add(OverallEditDist);
            
            //---------- Middle Panel ----------
            JPanel MidRowPanel = new JPanel();
            MidRowPanel.setLayout(new BoxLayout(MidRowPanel, BoxLayout.X_AXIS));
            //MidRowPanel.setMaximumSize(PanelSize);
            //MidRowPanel.setPreferredSize(PanelSize);
            ResultsWind.add(MidRowPanel);
            Label SourceCodeText = new Label("Source Code: " + result.getSource()); //Fix this line ---------------------
            MidRowPanel.add(SourceCodeText);
            Label TargetCodelbl = new Label("Target Code: " + result.getTarget()); //Finish this line ------------
            MidRowPanel.add(TargetCodelbl);
            
            //---------- List Panel ----------
            Dimension listSize = new Dimension(300, 400);
            JPanel ListRowPanel = new JPanel();
            ListRowPanel.setLayout(new BoxLayout(ListRowPanel, BoxLayout.X_AXIS));
            ResultsWind.add(ListRowPanel);
            
            JScrollPane SourceCodeTextPane = new JScrollPane();
            ListRowPanel.add(SourceCodeTextPane);
            String[] source = result.getClassText(result.SOURCE, 0).split("\n");
            SourceCodeTextArea = new JList(source);
            SourceCodeTextPane.setViewportView(SourceCodeTextArea);
            SourceCodeTextPane.setMaximumSize(listSize);
            SourceCodeTextPane.setPreferredSize(listSize);
            
            JScrollPane TargetCodeTextPane = new JScrollPane();
            ListRowPanel.add(TargetCodeTextPane);
            String[] target = result.getClassText(result.TARGET, 0).split("\n");
            TargetCodeTextArea = new JList(target);
            TargetCodeTextPane.setViewportView(TargetCodeTextArea);
            TargetCodeTextPane.setMaximumSize(listSize);
            TargetCodeTextPane.setPreferredSize(listSize);
            
            RegionDropDown.addActionListener(new regionDropDownListener());
            
            
             
            ResultsWind.pack();
            ResultsWind.setVisible(true);
        }
        
        private void genRegionDropDownInfo() {
            int index = srcSelectDropDown.getSelectedIndex();
            DDList = new ArrayList<>();
            int[][] chain = result.getChainLength(index);
            
            for (int i=0;i<chain.length;) {
                if(chain[i][2] == 0) {
                    i++;
                    continue;
                }
                if (chain[i][0] != -1 && chain[i][1] != -1 && chain[i][2] != -1) {
                    int[] tempArray = new int[3]; //0 is Source index; 1 is target index; 2 is length 
                    tempArray[0] = chain[i][0];
                    tempArray[1] = chain[i][1];
                    tempArray[2] = chain[i][2];
                    i = i + tempArray[2];
                    DDList.add(tempArray);
                } else {
                    i++;
                }
            }
            
            String[] DDTextList = new String[DDList.size() + 1];
            DDTextList[0] = "Overall";
            for (int i=0;i<DDList.size();i++) {
                DDTextList[i+1] = "Region " + (i + 1) + " - " + DDList.get(i)[2];
            }
            DefaultComboBoxModel model = new DefaultComboBoxModel(DDTextList);
            RegionDropDown.setModel(model);
        }
        
        public void destroyInterface() {
            ResultsWind.setVisible(false);
            ResultsWind.dispose();
        }
        
        class regionDropDownListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                int index = RegionDropDown.getSelectedIndex();
                int indexOfClass = srcSelectDropDown.getSelectedIndex();
                if (index == 0) { //Overall
                    String SourceText = result.getClassText(result.SOURCE, indexOfClass);
                    String[] SourceLines = SourceText.split("\n");
                    SourceCodeTextArea.setListData(SourceLines);
                    TargetCodeTextArea.setListData(result.getClassText(result.TARGET, index).split("\n"));
                } else {
                    index--;
                    //Take the index and find the start and end locations in the source/target code
                    //copy those regions
                    //Make the lists display those
                    String[] source = result.getClassText(result.SOURCE, indexOfClass).split("\n");
                    String[] target = result.getClassText(result.TARGET, indexOfClass).split("\n");
                    int[] temp = DDList.get(index);
                    String[] sourceOutput = new String[temp[2]];
                    String[] targetOutput = new String[temp[2]];
                    
                    int sourceIndex = temp[0];
                    int targetIndex = temp[1];
                    
                    for (int i=0;i<temp[2];i++) {
                        sourceOutput[i] = source[sourceIndex];
                        targetOutput[i] = target[targetIndex];
                        sourceIndex++;
                        targetIndex++;
                    }
                    SourceCodeTextArea.setListData(sourceOutput);
                    TargetCodeTextArea.setListData(targetOutput);
                }
                RegionDropDown.hidePopup();
                srcSelectDropDown.hidePopup();
            }
            
        }
        
        class srcSelectDropDownListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = srcSelectDropDown.getSelectedIndex();
                String[] source = result.getClassText(result.SOURCE, index).split("\n");
                String[] target = result.getClassText(result.TARGET, index).split("\n");
                OverallEditDist.setText("Overall Edit Distance: " + result.getDLevenOverall(index));
                SourceCodeTextArea.setListData(source);
                TargetCodeTextArea.setListData(target);
                srcSelectDropDown.hidePopup();
                genRegionDropDownInfo();
            }
            
        }
        
        class CloseListener implements WindowListener {

            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                destroyInterface();
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
            
        }
    }
    
    class SourceCodeOutputListListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                //System.out.println(sourceCodeListOutput.getSelectedIndex());
                String source = (String)sourceCodeListOutput.getSelectedValue();
                int index = sourceCodeListOutput.getSelectedIndex();
                String[] targetCode = new String[OutputNameList[index].length];
                
                for (int i=1;i<OutputNameList[index].length;i++) {
                    targetCode[i-1] = OutputNameList[index][i];
                }
  
                targetCodeListOutput.removeAll();
                targetCodeListOutput.setListData(targetCode);
            }
        }
        
    }
    
    class CodeBaseBrowse implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            
            int returnVal = fc.showOpenDialog(Interface.this);
            
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                CBDir.setText(f.getAbsolutePath());
                codeBaseDir = f;
            }
        }
        
    }
    
    class SourceCodeBrowse implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            
            int returnVal = fc.showOpenDialog(Interface.this);
            
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                SCDir.setText(f.getAbsolutePath());
                sourceCodeDir = f;
                
                displaySCDirectories(f);
//                String[] listFiles = f.list();
//                sourceNames.removeAll();
//                sourceNames.setListData(listFiles);
            }
        }
        
    }
    
    class SourceCodeListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            
        }
    }
    
    class StartButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int noOfSubDir = 0;
            boolean fileFound = false;
            
            sourceCodeDir = new File(SCDir.getText());
            codeBaseDir = new File(CBDir.getText());
            
            //Check if directory has at least two subdirectories which both contain 
            try {
                File[] fArr = sourceCodeDir.listFiles();
                for (File f: fArr) {
                    if (f.isDirectory()) {
                        if (scl.getIfJavaPresent(f)) {
                            noOfSubDir++;
                        }
                    }
                }
            } catch (NullPointerException exc) {
                
            }

            if (noOfSubDir > 2) {
                Started = true;
                inputWind.setVisible(false);
                inputWind.dispose();
            } else {
                JOptionPane.showMessageDialog(new JFrame(), "The directory selected has less than two collections of source code");
            }
            
        }
        
    }
    
    class ResultBtnListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int x = sourceCodeListOutput.getSelectedIndex();
            int y = targetCodeListOutput.getSelectedIndex();
            
            if (x == -1 || y == -1) {
                JOptionPane.showMessageDialog(new JFrame(), "You have not selected a source and/or target");
            } else {
                //resultInfStorage.add(new ResultsInterface(ResultsTableAL.get(x)[y]));
                
                String sourceName = (String) sourceCodeListOutput.getSelectedValue();
                String targetName = (String) targetCodeListOutput.getSelectedValue();
                int index1 = 0,index2 = 0;
                
                for (int i = 0;i<Controller.scList.size();i++) {
                    if (Controller.scList.get(i).getSourceName().equals(sourceName)) {
                        index1 = i;
                    }
                    if (Controller.scList.get(i).getSourceName().equals(targetName)) {
                        index2 = i;
                    }
                }
                new ResultsInterface(index1, index2);
            }
            
        }
        
    }
    
    class SCDirDocListen implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            changedUpdate(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            changedUpdate(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            //displaySCDirectories(new File(SCDir.getText()));
        }
        
    }
    
    class SCDirListen implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            displaySCDirectories(new File(SCDir.getText()));
        }
    }
    
    class SourceListListen implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            int index = sourceNames.getSelectedIndex();
            generateSCFileList(DirFileList.get(index));
        }
    }
}
