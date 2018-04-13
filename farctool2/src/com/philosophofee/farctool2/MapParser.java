package com.philosophofee.farctool2;

import java.io.File;
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
            long begin = System.currentTimeMillis();
            MapParser self = new MapParser();
            RandomAccessFile mapAccess = new RandomAccessFile(file, "rw");
            int seek = 0;

            //Read header
            int header = mapAccess.readInt();
            if (header == 256) {
                System.out.println("Detected: LBP1/2 Map File");
            }
            if (header == 21496064) {
                System.out.println("Detected: LBP3 Map File");
            }
            if (header == 936) {
                System.out.println("Detected: LBP Vita Map File");
            }
            if (header != 256 && header != 21496064 && header != 936) {
                throw new IOException("Error reading 4 bytes - not a valid .map file");
            }
            seek += 4;
            mapAccess.seek(seek); //ok, were done!

            //Read map entry count
            int mapEntries = mapAccess.readInt();
            System.out.println(mapEntries + " entries in file");
            seek += 4;
            mapAccess.seek(seek); //ok, were done!

            //Read entry
            int fileNameLength = 0;
            String fileName = "";
            int fileSize = 0;
            String SHA1 = "";
            int GUID = 0;

            for (int i = 0; i < mapEntries; i++) {

                //get filename length
                fileNameLength = mapAccess.readInt();
                //System.out.println("entry length=" + fileNameLength);
                fileName = ""; // reset
                seek += 4;
                mapAccess.seek(seek);

                //get filename string
                for (int i2 = 0; i2 < fileNameLength; i2++) {
                    fileName += (char) mapAccess.readByte();
                    seek += 1;
                    mapAccess.seek(seek);
                }//for filename

                //padding, 0x000000
                seek += 4;
                mapAccess.seek(seek);

                //DATE, maybe fix later but unimportant now
                seek += 4;
                mapAccess.seek(seek);

                //File size 4 bytes
                fileSize = mapAccess.readInt();
                seek += 4;
                mapAccess.seek(seek);

                //Hash 20 bytes
                SHA1 = "";
                for (int i3 = 0; i3 < 20; i3++) {
                    SHA1 += String.format("%02X", mapAccess.readByte());
                    seek += 1;
                    mapAccess.seek(seek);
                }
                //GUID 4 bytes
                GUID = 0;
                GUID = mapAccess.readInt();
                seek += 4;
                mapAccess.seek(seek);
                //self.addPath(fileName);
                buildTreeFromString(model, fileName);
                //System.out.println(fileName + " | size: " + fileSize + " | sha1: " + SHA1 + " | GUID: " + GUID);
            }//for each map entry

            //Close map
            mapAccess.close();
            long end = System.currentTimeMillis();
            long timeTook = end - begin;

            System.out.println("Map parsed in " + (timeTook / 1000) + " seconds (" + timeTook + "ms)");
            //self.printHtml(System.out);
        } catch (IOException e) {
            System.out.println("IOException:");
            e.printStackTrace();
        }
        return model;
    }

    //public void loadMap(File file) {
    //    parseMapIntoMemory(file);
    //}
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
