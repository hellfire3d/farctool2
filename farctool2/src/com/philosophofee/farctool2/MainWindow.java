package com.philosophofee.farctool2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import org.riversun.bigdoc.bin.BigFileSearcher;

public class MainWindow extends javax.swing.JFrame {
    public File bigBoy = null;
    public File bigBoyFarc = null;
    public String currSHA1 = null;
    public String currFileName = null;
    
    public MainWindow() {
        initComponents();
        mapTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        mapTree.addTreeSelectionListener(new TreeSelectionListener() 
        {
            public void valueChanged(TreeSelectionEvent e) 
            {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) mapTree.getLastSelectedPathComponent();
                if (node == null) return;
                if (mapTree.getSelectionPath().getPathCount() ==1 )
                {
                    System.out.println("Root");
                    return;
                }
                String[] test = new String[mapTree.getSelectionPath().getPathCount()];
                for (int i=1; i<mapTree.getSelectionPath().getPathCount(); i++) {
                    test[i] = mapTree.getSelectionPath().getPathComponent(i).toString();
                }
                String finalString = new String();
                for (int i=1; i<mapTree.getSelectionPath().getPathCount(); i++) {
                    finalString+=test[i];
                    if (i!=mapTree.getSelectionPath().getPathCount()-1) {
                        finalString+="/";
                    }
                }

                if (finalString.contains(".")){
                    System.out.println("You currently have selected " + finalString);
                    currFileName = finalString;
                    EditorPanel.setValueAt(finalString, 0, 1);
                    KMPMatch matcher = new KMPMatch();

                    try { 
                        int offset=0;    
                        offset = matcher.indexOf(Files.readAllBytes(bigBoy.toPath()), finalString.getBytes());
                        RandomAccessFile mapAccess = new RandomAccessFile(bigBoy, "rw"); 
                        mapAccess.seek(offset);
                        offset+=finalString.length();
                        mapAccess.seek(offset);
                        offset+=4;
                        mapAccess.seek(offset);
                        
                        //Get timestamp
                        String fileTimeStamp="";
                        for (int i = 0; i < 4; i++) {
                            fileTimeStamp += String.format("%02X", mapAccess.readByte());
                            offset += 1;
                            mapAccess.seek(offset);
                        }
                        EditorPanel.setValueAt(fileTimeStamp, 1, 2); //set hex timestamp
                        Date readableDate = new Date();
                        readableDate.setTime((long)Integer.parseInt(fileTimeStamp,16)*1000);
                        EditorPanel.setValueAt(readableDate.toString(), 1, 1); //set readable timestamp
                        
                        //Get size
                        String fileSize="";
                        for (int i = 0; i < 4; i++) {
                            fileSize += String.format("%02X", mapAccess.readByte());
                            offset += 1;
                            mapAccess.seek(offset);
                        }
                        EditorPanel.setValueAt(fileSize, 2, 2); //set hex filesize
                        EditorPanel.setValueAt(Integer.parseInt(fileSize,16), 2, 1); //set readable filesize
                        
                        //Get hash
                        String fileHash="";
                        for (int i = 0; i < 20; i++) {
                            fileHash += String.format("%02X", mapAccess.readByte());
                            offset += 1;
                            mapAccess.seek(offset);
                        }
                        EditorPanel.setValueAt(fileHash, 3, 2); //set hex hash
                        currSHA1 = fileHash;
                        EditorPanel.setValueAt(fileHash, 3, 1); //set readable hash (redundant)
                        
                        //Get size
                        String fileGUID="";
                        for (int i = 0; i < 4; i++) {
                            fileGUID += String.format("%02X", mapAccess.readByte());
                            offset += 1;
                            mapAccess.seek(offset);
                        }
                        EditorPanel.setValueAt(fileGUID, 4, 2); //set hex guid
                        EditorPanel.setValueAt("g" + Integer.parseInt(fileGUID,16), 4, 1); //set readable guid
                        
                        mapAccess.close();
                    
                    } catch (IOException ex) {}

                    
                }
                else {
                    System.out.println("You currently have selected " + finalString + "/. This is a folder!");
                } 
            }
        });
        PrintStream out = new CustomPrintStream( new TextAreaOutputStream( OutputTextArea ) );
        System.setOut( out );
        System.setErr( out );
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        jFrame1 = new javax.swing.JFrame();
        PopUpMessage = new javax.swing.JOptionPane();
        jSplitPane1 = new javax.swing.JSplitPane();
        MapPanel = new javax.swing.JScrollPane();
        mapTree = new javax.swing.JTree();
        RightHandStuff = new javax.swing.JSplitPane();
        ToolsPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        EditorPanel = new javax.swing.JTable();
        ExtractButton = new javax.swing.JButton();
        pnlOutput = new javax.swing.JPanel();
        mapLoadingBar = new javax.swing.JProgressBar();
        jScrollPane2 = new javax.swing.JScrollPane();
        OutputTextArea = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        FileMenu = new javax.swing.JMenu();
        Open = new javax.swing.JMenuItem();
        OpenFarc = new javax.swing.JMenuItem();
        Exit = new javax.swing.JMenuItem();
        ToolsMenu = new javax.swing.JMenu();
        DecompressorMenuButton = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("farctool2");

        jSplitPane1.setDividerLocation(120);

        mapTree.setModel(null);
        MapPanel.setViewportView(mapTree);

        jSplitPane1.setLeftComponent(MapPanel);

        RightHandStuff.setDividerLocation(200);
        RightHandStuff.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        EditorPanel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Filename", null, null},
                {"Time Created", null, null},
                {"Size", null, null},
                {"Hash", null, null},
                {"GUID", null, null}
            },
            new String [] {
                "Variable", "Value", "Value (HEX)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(EditorPanel);

        ExtractButton.setText("Extract");
        ExtractButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExtractButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ToolsPanelLayout = new javax.swing.GroupLayout(ToolsPanel);
        ToolsPanel.setLayout(ToolsPanelLayout);
        ToolsPanelLayout.setHorizontalGroup(
            ToolsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ToolsPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane1)
                .addGap(3, 3, 3))
            .addGroup(ToolsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ExtractButton)
                .addContainerGap(335, Short.MAX_VALUE))
        );
        ToolsPanelLayout.setVerticalGroup(
            ToolsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ToolsPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ExtractButton)
                .addGap(0, 59, Short.MAX_VALUE))
        );

        RightHandStuff.setTopComponent(ToolsPanel);

        OutputTextArea.setEditable(false);
        OutputTextArea.setColumns(20);
        OutputTextArea.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        OutputTextArea.setLineWrap(true);
        OutputTextArea.setRows(5);
        jScrollPane2.setViewportView(OutputTextArea);

        javax.swing.GroupLayout pnlOutputLayout = new javax.swing.GroupLayout(pnlOutput);
        pnlOutput.setLayout(pnlOutputLayout);
        pnlOutputLayout.setHorizontalGroup(
            pnlOutputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mapLoadingBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
        );
        pnlOutputLayout.setVerticalGroup(
            pnlOutputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlOutputLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mapLoadingBar, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        RightHandStuff.setRightComponent(pnlOutput);

        jSplitPane1.setRightComponent(RightHandStuff);

        FileMenu.setText("File");

        Open.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        Open.setText("Open .map...");
        Open.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OpenActionPerformed(evt);
            }
        });
        FileMenu.add(Open);

        OpenFarc.setText("Open .farc...");
        OpenFarc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OpenFarcActionPerformed(evt);
            }
        });
        FileMenu.add(OpenFarc);

        Exit.setText("Exit");
        Exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExitActionPerformed(evt);
            }
        });
        FileMenu.add(Exit);

        jMenuBar1.add(FileMenu);

        ToolsMenu.setText("Tools");
        ToolsMenu.setToolTipText("");

        DecompressorMenuButton.setText("Decompressor...");
        DecompressorMenuButton.setToolTipText("Decompress a game data file to a raw, editable file.");
        DecompressorMenuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DecompressorMenuButtonActionPerformed(evt);
            }
        });
        ToolsMenu.add(DecompressorMenuButton);

        jMenuItem1.setText("Compressor...");
        jMenuItem1.setToolTipText("Compress a raw data file to a file loadable by the game.");
        ToolsMenu.add(jMenuItem1);

        jMenuBar1.add(ToolsMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    
    private void OpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpenActionPerformed
        System.out.println("A haiku for the impatient:\n"
                + "Map parsing takes time.\n"
                + "I might freeze, I have not crashed.\n"
                + "Wait, please bear with me!");
        FileFilter ff = new FileFilter(){
            public boolean accept(File f){
                if(f.isDirectory()) return true;
                else if(f.getName().endsWith(".map")) return true;
                    else return false;
            }
            public String getDescription(){
                return "Map Files";
            }
        };
        fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
        fileChooser.setFileFilter(ff);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            bigBoy = fileChooser.getSelectedFile();
            System.out.println("Sucessfully opened " + bigBoy.getName());
            
            MapParser self = new MapParser();
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(bigBoy.getName());
            mapTree.setModel(null);
            DefaultTreeModel model = self.parseMapIntoMemory(root, bigBoy);

            mapTree.setModel(model);
            
            
            //self.loadMap(file);
            //self.printHtml(System.out);
        } else {
            System.out.println("...nevermind, you cancelled!");
        }
    }//GEN-LAST:event_OpenActionPerformed

    private void ExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitActionPerformed
        System.out.println("Shutting down. Goodbye!");
        System.exit(0);
    }//GEN-LAST:event_ExitActionPerformed

    private void DecompressorMenuButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DecompressorMenuButtonActionPerformed
        FileFilter ff = new FileFilter(){
            public boolean accept(File f){
                if(f.isDirectory()) return true;
                else if (f.getName().endsWith(".anim") || 
                        f.getName().endsWith(".bev") ||
                        f.getName().endsWith(".bin") ||
                        f.getName().endsWith(".cld") ||
                        f.getName().endsWith(".ff") ||
                        f.getName().endsWith(".gmat") ||
                        f.getName().endsWith(".mat") ||
                        f.getName().endsWith(".mol") ||
                        f.getName().endsWith(".pck") ||
                        f.getName().endsWith(".plan") ||
                        f.getName().endsWith(".sbu") ||
                        f.getName().endsWith(".slt") ||
                        f.getName().endsWith(".tex") 
                        ) return true;
                    else return false;
            }
            public String getDescription(){
                return "Compressed LittleBigPlanet Data Files (*.bin, *.mol, *.tex...)";
            }
        };
        fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
        fileChooser.setFileFilter(ff);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            System.out.println("Sucessfully opened " + file.getName());
            
            System.out.println("This is where I would do something to " + file.getName() + ", but I'm feeling lazy today.");
            //self.loadMap(file);
            //self.printHtml(System.out);
        } else {
            System.out.println("File access cancelled by user.");
        }
    }//GEN-LAST:event_DecompressorMenuButtonActionPerformed

    private void ExtractButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExtractButtonActionPerformed
        System.out.println("Attempting to extract " + currSHA1 + "..." );
        if (bigBoyFarc == null) {showUserDialog("Warning","FARC not loaded or mismatch"); System.out.println("failed."); return; }
        
        int fileCount = 0;
        long tableOffset = 0;
        try {
            RandomAccessFile farcAccess = new RandomAccessFile(bigBoyFarc, "rw"); 
            farcAccess.seek(bigBoyFarc.length() - 8);
            fileCount = farcAccess.readInt();
            tableOffset = (bigBoyFarc.length() - 8 - (fileCount * 28));

            //System.out.println("file count in farc: " + fileCount);
            //System.out.println("file table offset: " + tableOffset);
            
            farcAccess.close();
        } catch (IOException ex) {}
        
        BigFileSearcher searcher = new BigFileSearcher();
        
        long fileTableOffset = searcher.indexOf(bigBoyFarc, hexStringToByteArray(currSHA1), tableOffset);
        if (fileTableOffset==-1 ) { System.out.println("This file isn't in the farc, dummy!"); return; }
        System.out.println("entry position in table: " + fileTableOffset); 
        
        //Let's do some extraction
        int newFileSize = 0;
        int newFileOffset = 0;
        byte[] newSHA1 = new byte[20];
        try {;
            RandomAccessFile farcAccess = new RandomAccessFile(bigBoyFarc, "rw"); 
            
            //go to the file table, and grab the hash for verification later
            farcAccess.seek(fileTableOffset);
            farcAccess.readFully(newSHA1);
            System.out.println("entry SHA1 in farc: " + byteArrayToHexString(newSHA1));
            
            //seek past the sha1 and grab the offset to know where to extract the file
            farcAccess.seek(fileTableOffset + 20);
            newFileOffset = farcAccess.readInt();
            System.out.println("entry offset: " + newFileOffset );
            
            //get file size so we can know how much data to pull later
            farcAccess.seek(fileTableOffset + 24);
            newFileSize = farcAccess.readInt();
            System.out.println("entry size: " + newFileSize );
            
            //get name of output file
            String outputFileName = currFileName.substring(currFileName.lastIndexOf("/") + 1);
            File outputFile = new File(outputFileName);
            
            fileChooser.setFileFilter(null);
            fileChooser.setSelectedFile(outputFile);
            int returnVal = fileChooser.showSaveDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                outputFile = fileChooser.getSelectedFile();
                System.out.println("Gonna try extracting now!");
                long begin = System.currentTimeMillis();
                FileInputStream fin = new FileInputStream(bigBoyFarc);
                fin.skip(newFileOffset);
                byte[] outputbytes = new byte[newFileSize];
                int output=0;
                output = fin.read(outputbytes);
                
                FileOutputStream fos = new FileOutputStream(outputFile);
                fos.write(outputbytes);
                fos.close();
                fin.close();
                long end = System.currentTimeMillis();
                long timeTook = end - begin;

                System.out.println("Done in " + (timeTook / 1000) + " seconds (" + timeTook + "ms). ");

            } else {
            System.out.println("File access cancelled by user.");
            }
            farcAccess.close();
        } catch (IOException ex) {}
        
    }//GEN-LAST:event_ExtractButtonActionPerformed

    private void OpenFarcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpenFarcActionPerformed
        if (bigBoy == null) {showUserDialog("Warning","Please keep in mind opening a .farc file alone will not display anything within farctool2. A .map file is required for any file functionality.");}
        FileFilter ff = new FileFilter(){
            public boolean accept(File f){
                if(f.isDirectory()) return true;
                else if(f.getName().endsWith(".farc")) return true;
                    else return false;
            }
            public String getDescription(){
                return "FARC Files";
            }
        };
        fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
        fileChooser.setFileFilter(ff);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            bigBoyFarc = fileChooser.getSelectedFile();
            System.out.println("Sucessfully opened " + bigBoyFarc.getName());
        }
    }//GEN-LAST:event_OpenFarcActionPerformed

    private void showUserDialog(String title, String message) {
        if (title=="Warning") {
            PopUpMessage.showMessageDialog(PopUpMessage, message, title, PopUpMessage.WARNING_MESSAGE);
        }
        else {
            PopUpMessage.showMessageDialog(PopUpMessage, message, title, PopUpMessage.PLAIN_MESSAGE);    
        }
    }
    
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    public static String byteArrayToHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
    
    public static void main(String args[]) {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("CDE/Motif".equals(info.getName())) {
                    //javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
                    //javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem DecompressorMenuButton;
    private javax.swing.JTable EditorPanel;
    private javax.swing.JMenuItem Exit;
    private javax.swing.JButton ExtractButton;
    private javax.swing.JMenu FileMenu;
    private javax.swing.JScrollPane MapPanel;
    private javax.swing.JMenuItem Open;
    private javax.swing.JMenuItem OpenFarc;
    private javax.swing.JTextArea OutputTextArea;
    private javax.swing.JOptionPane PopUpMessage;
    private javax.swing.JSplitPane RightHandStuff;
    private javax.swing.JMenu ToolsMenu;
    private javax.swing.JPanel ToolsPanel;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JProgressBar mapLoadingBar;
    private javax.swing.JTree mapTree;
    private javax.swing.JPanel pnlOutput;
    // End of variables declaration//GEN-END:variables
}
