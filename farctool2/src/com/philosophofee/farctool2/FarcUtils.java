package com.philosophofee.farctool2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.riversun.bigdoc.bin.BigFileSearcher;

public class FarcUtils {

    public static byte[] pullFromFarc(String currSHA1, File bigBoyFarc) {
        System.out.println("Converting " + currSHA1 + " to byte array...");
        
        //If the farc provided is nonexistant, boot us out
        if (bigBoyFarc == null) {
            return null;
        }

        //Grab file count
        int fileCount = 0;
        long tableOffset = 0;
        try {
            RandomAccessFile farcAccess = new RandomAccessFile(bigBoyFarc, "rw");
            farcAccess.seek(bigBoyFarc.length() - 8);
            fileCount = farcAccess.readInt();
            tableOffset = (bigBoyFarc.length() - 8 - (fileCount * 28));

            farcAccess.close();
        } catch (IOException ex) {}

        //Go to offset where SHA1's start
        BigFileSearcher searcher = new BigFileSearcher();
        long fileTableOffset = searcher.indexOf(bigBoyFarc, MiscUtils.hexStringToByteArray(currSHA1), tableOffset);
        if (fileTableOffset == -1) {
            System.out.println("This SHA1 isn't in the farc, dummy!");
            return null;
        }
        //System.out.println("entry position in table: " + fileTableOffset);

        //Let's do some extraction
        int newFileSize = 0;
        int newFileOffset = 0;
        byte[] newSHA1 = new byte[20];
        try {;
            RandomAccessFile farcAccess = new RandomAccessFile(bigBoyFarc, "rw");

            //go to the file table, and grab the hash for verification later
            farcAccess.seek(fileTableOffset);
            farcAccess.readFully(newSHA1);
            System.out.println("entry SHA1 in farc: " + MiscUtils.byteArrayToHexString(newSHA1));

            //seek past the sha1 and grab the offset to know where to extract the file
            farcAccess.seek(fileTableOffset + 20);
            newFileOffset = farcAccess.readInt();
            System.out.println("entry offset: " + newFileOffset);

            //get file size so we can know how much data to pull later
            farcAccess.seek(fileTableOffset + 24);
            newFileSize = farcAccess.readInt();
            System.out.println("entry size: " + newFileSize);


            System.out.println("Gonna try extracting now!");
            long begin = System.currentTimeMillis();
            FileInputStream fin = new FileInputStream(bigBoyFarc);
            fin.skip(newFileOffset);
            byte[] outputbytes = new byte[newFileSize];
            int output = 0;
            output = fin.read(outputbytes);

            fin.close();

            //System.out.println("Done in " + (timeTook / 1000) + " seconds (" + timeTook + "ms). ");
            //This whole process takes like 10 milliseconds so timing it is useless
            farcAccess.close();
            
            //finally, return our bytes!
            return outputbytes;
            
        } catch (IOException ex) {}
    return null; //something messed up
    }
}
