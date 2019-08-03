package com.philosophofee.farctool2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import com.philosophofee.farctool2.BRGMapFile;
import com.philosophofee.farctool2.MapEntry;
import java.awt.datatransfer.*;
import java.awt.Toolkit;
import java.time.Instant;
import java.util.Iterator;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

public class MapEditorGUI extends javax.swing.JFrame {

    JProgressBar pbProgress = new JProgressBar(0, 100);
    
    private int map_type;
    private int map_entry_count;
    private int map_entries_parsed;
    
    // Initiate UI elements (messy)
    private JTextField tfSearch1;
    public JPopupMenu popupMenu;
    
    // Warning dialogues
    boolean shownPasteMessage = false;
    int stopAskingMeTheseSillyQuestions = 0;
    
    // The one and only
    BRGMapFile theChosenOne;
    
    public MapEditorGUI() {
                
        // Set the icon
        //this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("resources/map.png")));
        
        // Initiate components from netbeans 
        initComponents();
        this.setLocationRelativeTo(null);
        
        // Make check box columns un-resizable for sake of UX
        tblMap1.getColumnModel().getColumn(0).setMaxWidth(0);        
        
        // Initiate text fields search
        tfSearch1 = RowFilterUtil.createRowFilter(tblMap1);
        tbMap1.add(tfSearch1);
        
        // Progress bar (it's in a pretty terrible location...)
        jMenuBar1.add(pbProgress);
        pbProgress.setStringPainted(true);
        pbProgress.setString("0/0 (100%)");
        
        
        // Pop up menu for table editing
        popupMenu = new JPopupMenu();
        
        ActionListener menuListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                switch (event.getActionCommand()) {
                    case "Prime Highlighted Entries":
                        toggleSelectedEntriesFromTable(tblMap1);
                        break;
                    case "Copy Primed Entries":
                        copyPrimedEntriesFromTable(tblMap1);
                        break;
                    case "Paste":
                        break;
                    case "Nullify Primed Entries":
                        //System.out.println(tblMap1.getValueAt(entries[i], 1));
                        break;
                }
            }
        };
        JMenuItem item;
        item = new JMenuItem("Prime Highlighted Entries");
        popupMenu.add(item);
        item.addActionListener(menuListener);
        item = new JMenuItem("Copy Primed Entries");
        popupMenu.add(item);
        item.addActionListener(menuListener);
        item = new JMenuItem("Paste");
        popupMenu.add(item);
        item.addActionListener(menuListener);
        item = new JMenuItem("Nullify Primed Entries");
        popupMenu.add(item);
        item.addActionListener(menuListener);
        
        
        tblMap1.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int r = tblMap1.rowAtPoint(e.getPoint());
                boolean manyRowsSelected = false;
                if (tblMap1.getSelectedRowCount() > 1) {
                    // OK, so you have many rows selected. Are you right-clicking one of them?
                    Integer[] selectedRows = Arrays.stream(tblMap1.getSelectedRows()).boxed().toArray(Integer[]::new);
                    ArrayList<Integer> mySelectedRows = new ArrayList<>(Arrays.asList(selectedRows));
                    // this code sucks. if you have any ways to improve it, let me know.
                    if (mySelectedRows.contains(r)) {
                        manyRowsSelected = true;
                    }
                }
                if (r >= 0 && r < tblMap1.getRowCount() && !manyRowsSelected) {
                    tblMap1.setRowSelectionInterval(r, r);
                }
                if (tblMap1.getSelectedRow() < 0) {
                    return;
                }
                if (e.getButton()==MouseEvent.BUTTON3 && e.getComponent() instanceof JTable) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        tblMap1.setDragEnabled(false);
    }
    public void toggleSelectedEntriesFromTable(JTable table) {
        int[] entries = table.getSelectedRows();
        for (int i = 0; i < table.getSelectedRowCount(); i++) {
            boolean isTrue = (boolean) table.getValueAt(entries[i], 0);
            table.setValueAt(!isTrue, entries[i], 0);
        }
    }
    
    public void copyPrimedEntriesFromTable(JTable table) {
        int dialogButton = stopAskingMeTheseSillyQuestions;
        if (stopAskingMeTheseSillyQuestions == 0 || stopAskingMeTheseSillyQuestions == 1) {
            String[] options = new String[4];
            options[0] = "Yes";
            options[1] = "No";
            options[2] = "Always Yes";
            options[3] = "Always No";
            dialogButton = JOptionPane.showOptionDialog(null, "Would you like me to set the timestamp on the copied entries to be right now?\n\n"
                    + "The benefit to doing this would be that you could sort entries by time,\n"
                    + "and view all the newest entries, in whatever map you are planning to copy to.",
                    "Question", 0, JOptionPane.INFORMATION_MESSAGE, null, options, null);
        }
        if (dialogButton == 2) {
            dialogButton = 0;
            stopAskingMeTheseSillyQuestions = 2;
        }
        if (dialogButton == 3) {
            dialogButton = 1;
            stopAskingMeTheseSillyQuestions = 3;
        }
        if (dialogButton == -1) {
            return;
        }
        
        int selectedCount = 0;
        for (int i=0; i<table.getRowCount(); i++) {
            if ((boolean)table.getValueAt(i, 0)==true) {
                selectedCount++;
            }
        }
        StringBuilder builder=new StringBuilder();
        builder.append(selectedCount);
        for (int i=0; i<table.getRowCount(); i++) {
            if ((boolean)table.getValueAt(i, 0)==true) {
                builder.append("|entry:"+
                        table.getValueAt(i, 1).toString()+":");
                if (dialogButton==0) builder.append(Instant.now().getEpochSecond()+":");
                if (dialogButton==1) builder.append(table.getValueAt(i, 2).toString()+":");
                builder.append(table.getValueAt(i, 3).toString()+":"+
                        table.getValueAt(i, 4).toString()+":"+
                        table.getValueAt(i, 5).toString());   
            }
        }
        StringSelection stringSelection = new StringSelection(builder.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
    
    public void pastePrimedEntriesFromTable(JTable table) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        
        Transferable contents = clipboard.getContents(this);
        System.out.println(contents);
        DefaultTableModel modelt = (DefaultTableModel)table.getModel();
        Object[] rowToAdd = { Boolean.FALSE, "null", 0, 0, null, null }; //TODO: parse clipboard
        modelt.addRow(rowToAdd);
    }
    
    public void nullifyPrimedEntriesFromTable(){}
    
    public void rebuildTable() {
        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "",
                    "Filename",
                    "Timestamp",
                    "Size",
                    "Hash",
                    "GUID"
                }
        ) {
            Class[] types = new Class[]{
                java.lang.Boolean.class,
                java.lang.String.class,
                java.lang.Integer.class,
                java.lang.Integer.class,
                java.lang.String.class,
                java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };

        for (MapEntry entry : theChosenOne.entries) {
            Object[] rowToAdd = {Boolean.FALSE, entry.getPath(), entry.getTimestamp(), entry.getSize(), MiscUtils.byteArrayToHexString(entry.getHash()), entry.getGUID()};
            tableModel.addRow(rowToAdd);
        }

        tblMap1.setModel(tableModel);
    }
    
    public void updateUIElements() {
        cbVersionPickerMap1.setSelectedIndex(map_type);
        tblMap1.getColumnModel().getColumn(0).setMaxWidth(0);
        
        //remove the search and replace with a new formatted one :)
        tbMap1.remove(tfSearch1);
        tfSearch1 = RowFilterUtil.createRowFilter(tblMap1);
        tbMap1.add(tfSearch1);
        tbMap1.revalidate(); // not sure if this is proper but okay :)
        pbProgress.setString(tblMap1.getRowCount() + 
                            "/" + tblMap1.getRowCount() + 
                            " (100%)" );
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jProgressBar1 = new javax.swing.JProgressBar();
        jPanel2 = new javax.swing.JPanel();
        tbMap1 = new javax.swing.JToolBar();
        buttonSaveMap1 = new javax.swing.JButton();
        buttonSaveAsMap1 = new javax.swing.JButton();
        buttonNullifyMap1 = new javax.swing.JButton();
        buttonPortMap1 = new javax.swing.JButton();
        buttonAddMap1 = new javax.swing.JButton();
        buttonRemoveMap1 = new javax.swing.JButton();
        cbVersionPickerMap1 = new javax.swing.JComboBox<>();
        spMap1 = new javax.swing.JScrollPane();
        tblMap1 = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        mnuItemOpenMap1 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        mnuItemNewWindow = new javax.swing.JMenuItem();
        mnuItemExit = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        mnuItemCopy = new javax.swing.JMenuItem();
        mnuItemPaste = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();

        setTitle("guidmapper 1.0");

        tbMap1.setFloatable(false);
        tbMap1.setRollover(true);

        buttonSaveMap1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/zone/arctic/guidmapper/resources/disk.png"))); // NOI18N
        buttonSaveMap1.setToolTipText("Save");
        buttonSaveMap1.setEnabled(false);
        buttonSaveMap1.setFocusable(false);
        buttonSaveMap1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonSaveMap1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbMap1.add(buttonSaveMap1);

        buttonSaveAsMap1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/zone/arctic/guidmapper/resources/disk_multiple.png"))); // NOI18N
        buttonSaveAsMap1.setToolTipText("Save As...");
        buttonSaveAsMap1.setFocusable(false);
        buttonSaveAsMap1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonSaveAsMap1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbMap1.add(buttonSaveAsMap1);

        buttonNullifyMap1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/zone/arctic/guidmapper/resources/page_white_dvd.png"))); // NOI18N
        buttonNullifyMap1.setToolTipText("Void Selected Entries");
        buttonNullifyMap1.setEnabled(false);
        buttonNullifyMap1.setFocusable(false);
        buttonNullifyMap1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonNullifyMap1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbMap1.add(buttonNullifyMap1);

        buttonPortMap1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/zone/arctic/guidmapper/resources/arrow_right.png"))); // NOI18N
        buttonPortMap1.setToolTipText("Port Selected Entries to Map B");
        buttonPortMap1.setEnabled(false);
        buttonPortMap1.setFocusable(false);
        buttonPortMap1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonPortMap1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonPortMap1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPortMap1ActionPerformed(evt);
            }
        });
        tbMap1.add(buttonPortMap1);

        buttonAddMap1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/zone/arctic/guidmapper/resources/add.png"))); // NOI18N
        buttonAddMap1.setToolTipText("Port Selected Entries to Map B");
        buttonAddMap1.setEnabled(false);
        buttonAddMap1.setFocusable(false);
        buttonAddMap1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonAddMap1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbMap1.add(buttonAddMap1);

        buttonRemoveMap1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/zone/arctic/guidmapper/resources/delete.png"))); // NOI18N
        buttonRemoveMap1.setToolTipText("Port Selected Entries to Map B");
        buttonRemoveMap1.setEnabled(false);
        buttonRemoveMap1.setFocusable(false);
        buttonRemoveMap1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonRemoveMap1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbMap1.add(buttonRemoveMap1);

        cbVersionPickerMap1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Version", "LBP1/2", "LBPV", "LBP3" }));
        cbVersionPickerMap1.setMaximumSize(new java.awt.Dimension(100, 23));
        cbVersionPickerMap1.setMinimumSize(new java.awt.Dimension(100, 25));
        cbVersionPickerMap1.setPreferredSize(new java.awt.Dimension(100, 25));
        tbMap1.add(cbVersionPickerMap1);

        tblMap1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Filename", "Timestamp", "Size", "Hash", "GUID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblMap1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        spMap1.setViewportView(tblMap1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tbMap1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(spMap1)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(tbMap1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(spMap1, javax.swing.GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        jMenu1.setText("File");

        mnuItemOpenMap1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        mnuItemOpenMap1.setText("Open...");
        mnuItemOpenMap1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemOpenMap1ActionPerformed(evt);
            }
        });
        jMenu1.add(mnuItemOpenMap1);

        jMenuItem1.setText("Patch with .map...");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        mnuItemNewWindow.setText("I need another window!");
        mnuItemNewWindow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemNewWindowActionPerformed(evt);
            }
        });
        jMenu1.add(mnuItemNewWindow);

        mnuItemExit.setText("Exit");
        mnuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemExitActionPerformed(evt);
            }
        });
        jMenu1.add(mnuItemExit);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        mnuItemCopy.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        mnuItemCopy.setText("Copy Primed Entries");
        mnuItemCopy.setEnabled(false);
        mnuItemCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemCopyActionPerformed(evt);
            }
        });
        jMenu2.add(mnuItemCopy);

        mnuItemPaste.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        mnuItemPaste.setText("Paste");
        mnuItemPaste.setEnabled(false);
        mnuItemPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemPasteActionPerformed(evt);
            }
        });
        jMenu2.add(mnuItemPaste);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Tools");

        jMenuItem2.setText("Fix PSVita Blanks");
        jMenuItem2.setToolTipText("For every item that is named e.g. \".plan\" \".tex\" with no real path, a path to a GUID will be given. USE AFTER PATCHING BRG.MAP WITH THE UPDATE BRG.MAP");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem2);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mnuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItemExitActionPerformed
        //Exit app
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_mnuItemExitActionPerformed

    private void mnuItemNewWindowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItemNewWindowActionPerformed
        MapEditorGUI window = new MapEditorGUI();
        window.setVisible(true);
    }//GEN-LAST:event_mnuItemNewWindowActionPerformed

    private void mnuItemOpenMap1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItemOpenMap1ActionPerformed
        //Allow the user to select a map file to work with
        FilePicker pickMyFile = new FilePicker(this);
        File newMap = pickMyFile.pickFile(".map", "Map Files");
        
        //If the user selected nothing, then do nothing
        if (newMap==null)
            return;
        
        //Create map file in memory that we will work with
        theChosenOne = new BRGMapFile();
        
        loadMap(newMap);
    }//GEN-LAST:event_mnuItemOpenMap1ActionPerformed

    private void mnuItemCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItemCopyActionPerformed
        copyPrimedEntriesFromTable(tblMap1);
    }//GEN-LAST:event_mnuItemCopyActionPerformed

    private void mnuItemPasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItemPasteActionPerformed
        if (shownPasteMessage==false) {
            JOptionPane.showMessageDialog(null, "Just as a fair warning, the entries will be at the very bottom, until you sort the entries.");
            shownPasteMessage=true;
        }
        pastePrimedEntriesFromTable(tblMap1);
        updateUIElements();
    }//GEN-LAST:event_mnuItemPasteActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        //Allow the user to select a map file to work with
        FilePicker pickMyFile = new FilePicker(this);
        File newMap = pickMyFile.pickFile(".map", "Map Files");
        
        //If the user selected nothing, then do nothing
        if (newMap==null)
            return;
        
        // use the same one already in memory
        //theChosenOne = new BRGMapFile();
        
        loadMap(newMap);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void buttonPortMap1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPortMap1ActionPerformed

    }//GEN-LAST:event_buttonPortMap1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        for(MapEntry p : theChosenOne.entries) {
            //System.out.println(p.getGUID());
            if (p.getPath().startsWith(".")) {
                String previousPath = p.getPath();
                p.setPath("gamedata/__unsorted/" + p.getGUID() + previousPath );
            }
        }
        rebuildTable();
        updateUIElements();
    }//GEN-LAST:event_jMenuItem2ActionPerformed


    
    
    public void loadMap(File newMap) {
        Thread parseThread = new Thread() {
            public void run() {
                /**
                 * Thread that will parse through the map file, and when the
                 * parsing is complete, it will fill up whats necessary in the
                 * table.
                 */
                theChosenOne.parse(newMap);
                theChosenOne.completed = false;
                theChosenOne.completed_count = 0;
                
                while (theChosenOne.completed!=true) {
                    pbProgress.setValue((int) theChosenOne.returnProgress());
                    pbProgress.setString(theChosenOne.returnProgressCount() + 
                            "/" + theChosenOne.map_entry_count + 
                            " (" +(int) theChosenOne.returnProgress() + "%)" );
                } // forces the program to wait & update the progress bar
                // Once parsing thread is done, update the program.
                map_type = theChosenOne.getMapType();
                map_entry_count = theChosenOne.getMapEntryCount();
                
                rebuildTable();
                updateUIElements();
            }
        };
        parseThread.start();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddMap1;
    private javax.swing.JButton buttonNullifyMap1;
    private javax.swing.JButton buttonPortMap1;
    private javax.swing.JButton buttonRemoveMap1;
    private javax.swing.JButton buttonSaveAsMap1;
    private javax.swing.JButton buttonSaveMap1;
    private javax.swing.JComboBox<String> cbVersionPickerMap1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JMenuItem mnuItemCopy;
    private javax.swing.JMenuItem mnuItemExit;
    private javax.swing.JMenuItem mnuItemNewWindow;
    private javax.swing.JMenuItem mnuItemOpenMap1;
    private javax.swing.JMenuItem mnuItemPaste;
    private javax.swing.JScrollPane spMap1;
    private javax.swing.JToolBar tbMap1;
    private javax.swing.JTable tblMap1;
    // End of variables declaration//GEN-END:variables
}
