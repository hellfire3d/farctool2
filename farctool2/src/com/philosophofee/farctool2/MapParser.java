package com.philosophofee.farctool2;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class MapParser {

    public DefaultTreeModel parseMapIntoMemory(TreeNode root, File file) {
        DefaultTreeModel model = new DefaultTreeModel(root);
        try {
            //Create data input stream
            DataInputStream mapAccess = new DataInputStream(new FileInputStream(file));
            
            //Starting variables
            long begin = System.currentTimeMillis();
            boolean lbp3map = false;
            
            //Read header
            int header = mapAccess.readInt();
            
            if (header == 256) {
                System.out.println("Detected: LBP1/2 Map File");
            }
            if (header == 21496064) {
                System.out.println("Detected: LBP3 Map File");
                lbp3map=true;
            }
            if (header == 936) {
                System.out.println("Detected: LBP Vita Map File");
            }
            if (header != 256 && header != 21496064 && header != 936) {
                throw new IOException("Error reading 4 bytes - not a valid .map file");
            }
            
            //Read map entry count
            int mapEntries = mapAccess.readInt();
            System.out.println(mapEntries + " entries in file");
            
            //Read entry
            int fileNameLength = 0;
            String fileName = "";
            int fileSize = 0;
            String SHA1 = "";
            int GUID = 0;
            
            for (int i = 0; i < mapEntries; i++) {
                
                //seek 2 bytes (for lbp1/2 file only)
                if (lbp3map==false) {
                    mapAccess.skip(2);
                }
                //get filename
                fileNameLength = mapAccess.readShort();
                byte[] fileNameBytes = new byte[fileNameLength];
                mapAccess.read(fileNameBytes);
                fileName = new String(fileNameBytes);
                
                //padding, 0x000000 (LBP1/2 ONLY)
                if (lbp3map==false) {
                    mapAccess.skip(4);
                }
                
                //DATE, maybe fix later but unimportant now
                mapAccess.skip(4);
                
                //File size 4 bytes
                fileSize = mapAccess.readInt();
                
                //Hash 20 bytes
                byte[] SHA1Bytes = new byte[20]; 
                mapAccess.read(SHA1Bytes);
                SHA1 = MiscUtils.byteArrayToHexString(SHA1Bytes);
                
                //GUID 4 bytes
                GUID = mapAccess.readInt();
                
                buildTreeFromString(model, fileName);
            
            }//for each map entry

            long end = System.currentTimeMillis();
            long timeTook = end - begin;

            System.out.println("Map parsed in " + (timeTook / 1000) + " seconds (" + timeTook + "ms)");
            
            //OK, we're done
            mapAccess.close();
            
        }   catch(FileNotFoundException ex) {}
            catch(IOException ex) {}
        return model;
    }
    
    private void buildTreeFromString(final DefaultTreeModel model, final String str) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();

        String[] strings = str.split("/");

        DefaultMutableTreeNode node = root;

        for (String s : strings) {
            int index = childIndex(node, s);

            if (index < 0) {
                DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(s);
                node.insert(newChild, node.getChildCount());
                node = newChild;
            } else {
                node = (DefaultMutableTreeNode) node.getChildAt(index);
            }
        }
    }

    private int childIndex(final DefaultMutableTreeNode node, final String childValue) {
        Enumeration<DefaultMutableTreeNode> children = node.children();
        DefaultMutableTreeNode child = null;
        int index = -1;

        while (children.hasMoreElements() && index < 0) {
            child = children.nextElement();

            if (child.getUserObject() != null && childValue.equals(child.getUserObject())) {
                index = node.getIndex(child);
            }
        }

        return index;
    }
}
