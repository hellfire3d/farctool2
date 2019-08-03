/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.philosophofee.farctool2;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Formatter;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import net.npe.dds.DDSReader;

/**
 *
 * @author hasben
 */
public class MiscUtils {
    
    public static void deleteFile(String file) throws IOException {
        Files.deleteIfExists(Paths.get(file)); 
    }
    
    public static File returnFile(String file) {
        File to = new File(file);
        return to;
    }
    
    public static String getFileNameFromGUID(String myGUID, File inputMap) {
        int usableGUID = Integer.parseInt(myGUID, 16);
        try {
            //Create data input stream
            DataInputStream mapAccess = new DataInputStream(new FileInputStream(inputMap));
            
            //Starting variables
            boolean lbp3map = false;
            
            //Read header
            int header = mapAccess.readInt();
            
            if (header == 21496064) {
                lbp3map=true;
            }
            if (header != 256 && header != 21496064 && header != 936) {
                throw new IOException("Error reading 4 bytes - not a valid .map file");
            }
            
            //Read map entry count
            int mapEntries = mapAccess.readInt();
            
            //Read entry
            int fileNameLength = 0;
            String fileName = "";
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
                //File size 4 bytes
                //Hash 20 bytes
                mapAccess.skip(28);
                
                //GUID 4 bytes
                GUID = mapAccess.readInt();
                
                if (GUID==usableGUID) {
                    return fileName;
                }
                //buildTreeFromString(model, fileName);
            
            }//for each map entry

            //OK, we're done
            mapAccess.close();
            
        }   catch(FileNotFoundException ex) {}
            catch(IOException ex) {}
        return "Error finding GUID filename";
    }
    
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
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
    
    public static String getHeader(File input) {
        String header = "";
        try {
            RandomAccessFile fileAccess = new RandomAccessFile(input, "rw");
            for (int i2 = 0; i2 < 4; i2++) {
                    header += (char) fileAccess.readByte();
            }
            return header;
        } catch (IOException e) {}
        return header;
    }
    
    public static String getHeaderHexString(File input) {
                String header = "";
        try {
            RandomAccessFile fileAccess = new RandomAccessFile(input, "rw");
            for (int i2 = 0; i2 < 4; i2++) {
                    header += String.format("%02X", fileAccess.readByte());
            }
            return header;
        } catch (IOException e) {}
        return header;
    }
    
    public static String convertShortHexStringToLittleEndian(String input) {
        //what a mouthful
        char[] ch=input.toCharArray();
        String output = new String();
        output+=ch[2];
        output+=ch[3];
        output+=ch[0];
        output+=ch[1];
        return output;
    }
    
    public static ImageIcon createDDSIcon(String path) throws IOException {

        FileInputStream fis = new FileInputStream(path);
        byte [] buffer = new byte[fis.available()];
        fis.read(buffer);
        fis.close();

        int [] pixels = DDSReader.read(buffer, DDSReader.ARGB, 0);
        int width = DDSReader.getWidth(buffer);
        int height = DDSReader.getHeight(buffer);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, width, height, pixels, 0, width);
        
        if (width>256 || height>256) {
            if (width>height) {
                return new ImageIcon(image.getScaledInstance(256, 128, BufferedImage.SCALE_SMOOTH));
            }
            if (width<height) {
                return new ImageIcon(image.getScaledInstance(128, 256, BufferedImage.SCALE_SMOOTH));
            }
            return new ImageIcon(image.getScaledInstance(256, 256, BufferedImage.SCALE_SMOOTH));
        }
        return new ImageIcon(image.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH));
    }
    
    
    
    public static void DDStoSavePNG(String path, File outputfile) throws IOException, NullPointerException {
        FileInputStream fis = new FileInputStream(path);
        byte [] buffer = new byte[fis.available()];
        fis.read(buffer);
        fis.close();
        int [] pixels = DDSReader.read(buffer, DDSReader.ARGB, 0);
        int width = DDSReader.getWidth(buffer);
        int height = DDSReader.getHeight(buffer);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, width, height, pixels, 0, width);
        
        ImageIO.write(image, "png", outputfile);
    }
    
    public static String reverseHex(String originalHex) {
        // TODO: Validation that the length is even
        int lengthInBytes = originalHex.length() / 2;
        char[] chars = new char[lengthInBytes * 2];
        for (int index = 0; index < lengthInBytes; index++) {
            int reversedIndex = lengthInBytes - 1 - index;
            chars[reversedIndex * 2] = originalHex.charAt(index * 2);
            chars[reversedIndex * 2 + 1] = originalHex.charAt(index * 2 + 1);
        }
        return new String(chars);
    }

}
