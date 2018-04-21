package com.philosophofee.farctool2;

import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
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
import java.util.zip.DataFormatException;
import javax.swing.ImageIcon;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import org.riversun.bigdoc.bin.BigFileSearcher;

import tv.porst.jhexview.*;
import tv.porst.jhexview.JHexView.DefinitionStatus;

public class MainWindow extends javax.swing.JFrame {

    public File bigBoy = null;
    public File bigBoyFarc = null;
    public String currSHA1 = null;
    public String currFileName = null;

    public MainWindow() {
        initComponents();
        PreviewLabel.setVisible(false);
        TextPrevScroll.setVisible(false);
        TextPreview.setVisible(false);
        
        setIconImage(new ImageIcon(getClass().getResource("resources/farctool2_icon.png")).getImage());
        aboutWindow.setIconImage(new ImageIcon(getClass().getResource("resources/farctool2_icon.png")).getImage());
        
        mapTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        mapTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) mapTree.getLastSelectedPathComponent();
                if (node == null) {
                    return;
                }
                if (mapTree.getSelectionPath().getPathCount() == 1) {
                    System.out.println("Root");
                    return;
                }
                String[] test = new String[mapTree.getSelectionPath().getPathCount()];
                for (int i = 1; i < mapTree.getSelectionPath().getPathCount(); i++) {
                    test[i] = mapTree.getSelectionPath().getPathComponent(i).toString();
                }
                String finalString = new String();
                for (int i = 1; i < mapTree.getSelectionPath().getPathCount(); i++) {
                    finalString += test[i];
                    if (i != mapTree.getSelectionPath().getPathCount() - 1) {
                        finalString += "/";
                    }
                }

                if (finalString.contains(".")) {
                    //System.out.println("You currently have selected " + finalString); //this is annoying
                    currFileName = finalString;
                    EditorPanel.setValueAt(finalString, 0, 1);
                    KMPMatch matcher = new KMPMatch();

                    try {
                        int offset = 0;
                        boolean lbp3map=false;
                        offset = matcher.indexOf(Files.readAllBytes(bigBoy.toPath()), finalString.getBytes());
                        RandomAccessFile mapAccess = new RandomAccessFile(bigBoy, "rw");
                        mapAccess.seek(0);
                        if (mapAccess.readInt()==21496064) {lbp3map=true;}
                        
                        mapAccess.seek(offset);
                        offset += finalString.length();
                        mapAccess.seek(offset);
                        
                        if (lbp3map==false) {
                            offset += 4;
                            mapAccess.seek(offset);
                        }

                        //Get timestamp
                        String fileTimeStamp = "";
                        for (int i = 0; i < 4; i++) {
                            fileTimeStamp += String.format("%02X", mapAccess.readByte());
                            offset += 1;
                            mapAccess.seek(offset);
                        }
                        EditorPanel.setValueAt(fileTimeStamp, 1, 2); //set hex timestamp
                        Date readableDate = new Date();
                        readableDate.setTime((long) Integer.parseInt(fileTimeStamp, 16) * 1000);
                        EditorPanel.setValueAt(readableDate.toString(), 1, 1); //set readable timestamp

                        //Get size
                        String fileSize = "";
                        for (int i = 0; i < 4; i++) {
                            fileSize += String.format("%02X", mapAccess.readByte());
                            offset += 1;
                            mapAccess.seek(offset);
                        }
                        EditorPanel.setValueAt(fileSize, 2, 2); //set hex filesize
                        EditorPanel.setValueAt(Integer.parseInt(fileSize, 16), 2, 1); //set readable filesize

                        //Get hash
                        String fileHash = "";
                        for (int i = 0; i < 20; i++) {
                            fileHash += String.format("%02X", mapAccess.readByte());
                            offset += 1;
                            mapAccess.seek(offset);
                        }
                        EditorPanel.setValueAt(fileHash, 3, 2); //set hex hash
                        currSHA1 = fileHash;
                        EditorPanel.setValueAt(fileHash, 3, 1); //set readable hash (redundant)

                        //Get size
                        String fileGUID = "";
                        for (int i = 0; i < 4; i++) {
                            fileGUID += String.format("%02X", mapAccess.readByte());
                            offset += 1;
                            mapAccess.seek(offset);
                        }
                        EditorPanel.setValueAt(fileGUID, 4, 2); //set hex guid
                        EditorPanel.setValueAt("g" + Integer.parseInt(fileGUID, 16), 4, 1); //set readable guid

                        if (bigBoyFarc!=null) {
                            PreviewLabel.setVisible(true);
                            PreviewLabel.setIcon(null);
                            PreviewLabel.setText("No preview available");
                            TextPrevScroll.setVisible(false);
                            TextPreview.setVisible(false);
                            
                            byte[] workWithData = FarcUtils.pullFromFarc(currSHA1, bigBoyFarc);
                            if (workWithData==null) {
                                System.out.println("As a result, I wasn't able to preview anything...");
                                hexViewer.setData(null);
                                hexViewer.setDefinitionStatus(DefinitionStatus.UNDEFINED);
                                hexViewer.setEnabled(false);
                                return;
                            }
                            if (
                                    workWithData[3]==0x74 || 
                                    currFileName.contains(".nws") || 
                                    currFileName.contains(".txt") || 
                                    currFileName.contains(".rlst") || 
                                    currFileName.contains(".xml") || 
                                    currFileName.contains(".cha")
                                ) 
                            {
                                //Text file we can read with the text preview pane
                                PreviewLabel.setVisible(false);
                                TextPrevScroll.setVisible(true);
                                TextPreview.setVisible(true);
                                TextPreview.setText(new String(workWithData));
                                TextPreview.setCaretPosition(0);                            
                            }
                            if (currFileName.contains(".tex")) {
                                ZlibUtils.decompressThis(workWithData);
                                PreviewLabel.setVisible(true);
                                TextPrevScroll.setVisible(false);
                                TextPreview.setVisible(false);
                                PreviewLabel.setText(null);
                                PreviewLabel.setIcon(MiscUtils.createDDSIcon("temp_prev_tex"));
                            }
                            
                            hexViewer.setData(new SimpleDataProvider(workWithData));
                            hexViewer.setDefinitionStatus(DefinitionStatus.DEFINED);
                            hexViewer.setEnabled(true);
                            
                        }
                        
                        mapAccess.close();

                    } catch (IOException ex) {
                    } catch (DataFormatException ex) {
                        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                } else {
                    //System.out.println("You currently have selected " + finalString + "/. This is a folder!");
                }
            }
        });
        PrintStream out = new CustomPrintStream(new TextAreaOutputStream(OutputTextArea));
        System.setOut(out);
        System.setErr(out);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        jFrame1 = new javax.swing.JFrame();
        PopUpMessage = new javax.swing.JOptionPane();
        aboutWindow = new javax.swing.JFrame();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSplitPane1 = new javax.swing.JSplitPane();
        MapPanel = new javax.swing.JScrollPane();
        mapTree = new javax.swing.JTree();
        RightHandStuff = new javax.swing.JSplitPane();
        pnlOutput = new javax.swing.JPanel();
        mapLoadingBar = new javax.swing.JProgressBar();
        jScrollPane2 = new javax.swing.JScrollPane();
        OutputTextArea = new javax.swing.JTextArea();
        jSplitPane2 = new javax.swing.JSplitPane();
        ToolsPanel = new javax.swing.JPanel();
        jSplitPane3 = new javax.swing.JSplitPane();
        PreviewPanel = new javax.swing.JPanel();
        hexViewer = new tv.porst.jhexview.JHexView();
        ToolsPanel2 = new javax.swing.JPanel();
        PreviewLabel = new javax.swing.JLabel();
        TextPrevScroll = new javax.swing.JScrollPane();
        TextPreview = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        EditorPanel = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        FileMenu = new javax.swing.JMenu();
        Open = new javax.swing.JMenuItem();
        OpenFarc = new javax.swing.JMenuItem();
        Exit = new javax.swing.JMenuItem();
        ToolsMenu = new javax.swing.JMenu();
        ExtractMenuButton = new javax.swing.JMenuItem();
        ExportTextureButton = new javax.swing.JMenuItem();
        PrintDependenciesButton = new javax.swing.JMenuItem();
        DecompressorMenuButton = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        HelpMenu = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();

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

        aboutWindow.setTitle("farctool2 :)");
        aboutWindow.setAlwaysOnTop(true);
        aboutWindow.setMinimumSize(new java.awt.Dimension(450, 225));
        aboutWindow.setPreferredSize(new java.awt.Dimension(450, 225));
        aboutWindow.setResizable(false);

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("Courier New", 0, 10)); // NOI18N
        jTextArea2.setRows(5);
        jTextArea2.setText("Special thanks to:\n\n \"The man in the shadows\"\n                          Aluigi (http://zenhax.com)\n  http://xentax.com\n                              Jon, TBA, and friends\n     npedotnet (NPESDK_GWT) licensed under MIT license\n                 riversun (bigdoc) licensed under MIT license\n  Sporst (JHexView, splib) licensed under GPL 2.0");
        jTextArea2.setFocusable(false);
        jScrollPane4.setViewportView(jTextArea2);

        jLabel1.setFont(new java.awt.Font("Courier New", 0, 18)); // NOI18N
        jLabel1.setText("farctool2!");

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/philosophofee/farctool2/resources/1002.gif"))); // NOI18N

        jLabel3.setFont(new java.awt.Font("Courier New", 0, 11)); // NOI18N
        jLabel3.setText("written by Philosophofee");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 384, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout aboutWindowLayout = new javax.swing.GroupLayout(aboutWindow.getContentPane());
        aboutWindow.getContentPane().setLayout(aboutWindowLayout);
        aboutWindowLayout.setHorizontalGroup(
            aboutWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 450, Short.MAX_VALUE)
            .addGroup(aboutWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, aboutWindowLayout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        aboutWindowLayout.setVerticalGroup(
            aboutWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 225, Short.MAX_VALUE)
            .addGroup(aboutWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, aboutWindowLayout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("farctool2");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jSplitPane1.setDividerLocation(150);

        mapTree.setModel(null);
        MapPanel.setViewportView(mapTree);

        jSplitPane1.setLeftComponent(MapPanel);

        RightHandStuff.setDividerLocation(400);
        RightHandStuff.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

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
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 756, Short.MAX_VALUE)
        );
        pnlOutputLayout.setVerticalGroup(
            pnlOutputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlOutputLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mapLoadingBar, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        RightHandStuff.setRightComponent(pnlOutput);

        jSplitPane2.setDividerLocation(256);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jSplitPane3.setDividerLocation(256);

        javax.swing.GroupLayout PreviewPanelLayout = new javax.swing.GroupLayout(PreviewPanel);
        PreviewPanel.setLayout(PreviewPanelLayout);
        PreviewPanelLayout.setHorizontalGroup(
            PreviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(hexViewer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        PreviewPanelLayout.setVerticalGroup(
            PreviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(hexViewer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jSplitPane3.setRightComponent(PreviewPanel);

        PreviewLabel.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        PreviewLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        PreviewLabel.setAlignmentX(0.5F);

        TextPreview.setColumns(20);
        TextPreview.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        TextPreview.setRows(5);
        TextPrevScroll.setViewportView(TextPreview);

        javax.swing.GroupLayout ToolsPanel2Layout = new javax.swing.GroupLayout(ToolsPanel2);
        ToolsPanel2.setLayout(ToolsPanel2Layout);
        ToolsPanel2Layout.setHorizontalGroup(
            ToolsPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 255, Short.MAX_VALUE)
            .addGroup(ToolsPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(PreviewLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
            .addGroup(ToolsPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(TextPrevScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
        );
        ToolsPanel2Layout.setVerticalGroup(
            ToolsPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 253, Short.MAX_VALUE)
            .addGroup(ToolsPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(PreviewLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE))
            .addGroup(ToolsPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(TextPrevScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE))
        );

        jSplitPane3.setLeftComponent(ToolsPanel2);

        javax.swing.GroupLayout ToolsPanelLayout = new javax.swing.GroupLayout(ToolsPanel);
        ToolsPanel.setLayout(ToolsPanelLayout);
        ToolsPanelLayout.setHorizontalGroup(
            ToolsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane3)
        );
        ToolsPanelLayout.setVerticalGroup(
            ToolsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane3)
        );

        jSplitPane2.setTopComponent(ToolsPanel);

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

        jSplitPane2.setRightComponent(jScrollPane1);

        RightHandStuff.setLeftComponent(jSplitPane2);

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

        ExtractMenuButton.setText("Extract Selected...");
        ExtractMenuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExtractMenuButtonActionPerformed(evt);
            }
        });
        ToolsMenu.add(ExtractMenuButton);

        ExportTextureButton.setText("Export texture to file...");
        ExportTextureButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportTextureButtonActionPerformed(evt);
            }
        });
        ToolsMenu.add(ExportTextureButton);

        PrintDependenciesButton.setText("Print dependencies...");
        PrintDependenciesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PrintDependenciesButtonActionPerformed(evt);
            }
        });
        ToolsMenu.add(PrintDependenciesButton);

        DecompressorMenuButton.setText("Decompressor...");
        DecompressorMenuButton.setToolTipText("Decompress a game data file to a raw, editable file.");
        DecompressorMenuButton.setEnabled(false);
        DecompressorMenuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DecompressorMenuButtonActionPerformed(evt);
            }
        });
        ToolsMenu.add(DecompressorMenuButton);

        jMenuItem1.setText("Compressor...");
        jMenuItem1.setToolTipText("Compress a raw data file to a file loadable by the game.");
        jMenuItem1.setEnabled(false);
        ToolsMenu.add(jMenuItem1);

        jMenuBar1.add(ToolsMenu);

        HelpMenu.setText("Help");

        jMenuItem2.setText("About...");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        HelpMenu.add(jMenuItem2);

        jMenuBar1.add(HelpMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 914, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void OpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpenActionPerformed
        System.out.println("A haiku for the impatient:\n"
                + "Map parsing takes time.\n"
                + "I might freeze, I have not crashed.\n"
                + "Wait, please bear with me!");
        FileFilter ff = new FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else if (f.getName().endsWith(".map")) {
                    return true;
                } else {
                    return false;
                }
            }

            public String getDescription() {
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
        FileFilter ff = new FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else if (f.getName().endsWith(".anim")
                        || f.getName().endsWith(".bev")
                        || f.getName().endsWith(".bin")
                        || f.getName().endsWith(".cld")
                        || f.getName().endsWith(".ff")
                        || f.getName().endsWith(".gmat")
                        || f.getName().endsWith(".mat")
                        || f.getName().endsWith(".mol")
                        || f.getName().endsWith(".pck")
                        || f.getName().endsWith(".plan")
                        || f.getName().endsWith(".sbu")
                        || f.getName().endsWith(".slt")
                        || f.getName().endsWith(".tex")) {
                    return true;
                } else {
                    return false;
                }
            }

            public String getDescription() {
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

    private void OpenFarcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpenFarcActionPerformed
        if (bigBoy == null) {
            showUserDialog("Warning", "Please keep in mind opening a .farc file alone will not display anything within farctool2. A .map file is required for any file functionality.");
        }
        FileFilter ff = new FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else if (f.getName().endsWith(".farc")) {
                    return true;
                } else {
                    return false;
                }
            }

            public String getDescription() {
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

    private void ExtractMenuButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExtractMenuButtonActionPerformed
        if (bigBoyFarc == null) {
            showUserDialog("Warning", "FARC not loaded or mismatch");
            return;
        }
        
        byte[] bytesToSave = FarcUtils.pullFromFarc(currSHA1, bigBoyFarc);

        try {
            //get name of output file
            String outputFileName = currFileName.substring(currFileName.lastIndexOf("/") + 1);
            File outputFile = new File(outputFileName);

            fileChooser.setFileFilter(null);
            fileChooser.setSelectedFile(outputFile);
            int returnVal = fileChooser.showSaveDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                outputFile = fileChooser.getSelectedFile();
                System.out.println("Gonna try extracting now!");

                FileOutputStream fos = new FileOutputStream(outputFile);
                fos.write(bytesToSave);
                fos.close();

            } else {
                System.out.println("File access cancelled by user.");
            }
        } catch (IOException ex) {}      
        

    }//GEN-LAST:event_ExtractMenuButtonActionPerformed

    private void ExportTextureButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportTextureButtonActionPerformed
        try {
            //get name of output file
            if (currFileName.contains(".tex")==false) { 
                System.out.println("You're trying to convert a non-texture to a png, dummy!");
                return; }
            String outputFileName = currFileName.substring(currFileName.lastIndexOf("/") + 1) + ".png";
            File outputFile = new File(outputFileName);

            fileChooser.setFileFilter(null);
            fileChooser.setSelectedFile(outputFile);
            int returnVal = fileChooser.showSaveDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                outputFile = fileChooser.getSelectedFile();
                System.out.println("Gonna try extracting now!");
                MiscUtils.DDStoSavePNG("temp_prev_tex", outputFile);
            }
        } catch (IOException ex) {
        } catch (NullPointerException ex) {
        }
    }//GEN-LAST:event_ExportTextureButtonActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed

        
    }//GEN-LAST:event_formWindowClosed

    private void PrintDependenciesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PrintDependenciesButtonActionPerformed
        try {
            byte[] bytesToRead = FarcUtils.pullFromFarc(currSHA1, bigBoyFarc);
            ByteArrayInputStream fileAccess = new ByteArrayInputStream(bytesToRead);
            fileAccess.skip(8);
            //Get dependencies offset
            byte[] offsetDependenciesByte = new byte[4];
            fileAccess.read(offsetDependenciesByte);
            int offsetDependencies = Integer.parseInt(MiscUtils.byteArrayToHexString(offsetDependenciesByte), 16);
            //System.out.println("Dependencies offset in hex: " + MiscUtils.byteArrayToHexString(offsetDependenciesByte));
            System.out.println("Dependencies offset: " + offsetDependencies);
            
            fileAccess.skip(offsetDependencies-12);
            
            byte[] dependenciesCountByte = new byte[4];
            fileAccess.read(dependenciesCountByte);
            int dependenciesCount = Integer.parseInt(MiscUtils.byteArrayToHexString(dependenciesCountByte), 16);
            
            System.out.println("Dependencies count: " + dependenciesCount);
            
            byte[] dependencyKindByte = new byte[1];
            byte[] dependencyGUIDByte = new byte[4];
            int dependencyGUID = 0;
            byte[] dependencyTypeByte = new byte[4];
            int dependencyType = 0;
            boolean levelFail = false;
            
            for (int i=0; i<dependenciesCount; i++ ) {
                fileAccess.read(dependencyKindByte);
                if (dependencyKindByte[0]==0x01) {
                    fileAccess.skip(20); //sha1
                    fileAccess.skip(4);
                }
                if (dependencyKindByte[0]==0x02) {
                    fileAccess.read(dependencyGUIDByte);
                    dependencyGUID = Integer.parseInt(MiscUtils.byteArrayToHexString(dependencyGUIDByte), 16);
                    
                    fileAccess.read(dependencyTypeByte);
                    dependencyType = Integer.parseInt(MiscUtils.byteArrayToHexString(dependencyTypeByte), 16);
                    String fileNameNew = MiscUtils.getFileNameFromGUID(MiscUtils.byteArrayToHexString(dependencyGUIDByte),bigBoy);
                    if (fileNameNew.contains("Error")) {
                        levelFail=true;
                    }
                    System.out.println((i+1) + ": " + fileNameNew + " | " + "g" + dependencyGUID + " | " + dependencyType);
                }
                
            }
            if (levelFail==true) {
                System.out.println("ERROR! The level contains at least one dependency that does not exist. You will have problems.");
            }
            
        } catch (IOException ex) {
        } catch (NullPointerException ex) {
        }
    }//GEN-LAST:event_PrintDependenciesButtonActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        aboutWindow.show();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void showUserDialog(String title, String message) {
        if (title == "Warning") {
            PopUpMessage.showMessageDialog(PopUpMessage, message, title, PopUpMessage.WARNING_MESSAGE);
        } else {
            PopUpMessage.showMessageDialog(PopUpMessage, message, title, PopUpMessage.PLAIN_MESSAGE);
        }
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
    private javax.swing.JMenuItem ExportTextureButton;
    private javax.swing.JMenuItem ExtractMenuButton;
    private javax.swing.JMenu FileMenu;
    private javax.swing.JMenu HelpMenu;
    private javax.swing.JScrollPane MapPanel;
    private javax.swing.JMenuItem Open;
    private javax.swing.JMenuItem OpenFarc;
    private javax.swing.JTextArea OutputTextArea;
    private javax.swing.JOptionPane PopUpMessage;
    private javax.swing.JLabel PreviewLabel;
    private javax.swing.JPanel PreviewPanel;
    private javax.swing.JMenuItem PrintDependenciesButton;
    private javax.swing.JSplitPane RightHandStuff;
    private javax.swing.JScrollPane TextPrevScroll;
    private javax.swing.JTextArea TextPreview;
    private javax.swing.JMenu ToolsMenu;
    private javax.swing.JPanel ToolsPanel;
    private javax.swing.JPanel ToolsPanel2;
    private javax.swing.JFrame aboutWindow;
    private javax.swing.JFileChooser fileChooser;
    private tv.porst.jhexview.JHexView hexViewer;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JProgressBar mapLoadingBar;
    private javax.swing.JTree mapTree;
    private javax.swing.JPanel pnlOutput;
    // End of variables declaration//GEN-END:variables
}
