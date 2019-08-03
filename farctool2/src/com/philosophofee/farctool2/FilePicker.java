/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.philosophofee.farctool2;

import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author benb
 */
public class FilePicker {
 
    FileFilter ff;
    Component parent;
    FilePicker(Component parent) {
        this.parent = parent;
    }
        
    public File pickFile(String extension, String description) {
        JFileChooser fileChooser = new JFileChooser();
        
        ff = new FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory()) { return true; } //Accept directories
                else if (f.getName().endsWith(extension)) { return true; } //Accept all files with extension 
                else { return false; } //Otherwise hide
            }
            public String getDescription() { return description; }
        };
        fileChooser.removeChoosableFileFilter(ff);
        fileChooser.setFileFilter(ff);
        int returnVal = fileChooser.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        else {
            System.out.println("User cancelled");
            return null;
        }
    }
    
}
