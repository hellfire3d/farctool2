/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.philosophofee.farctool2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Formatter;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
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
