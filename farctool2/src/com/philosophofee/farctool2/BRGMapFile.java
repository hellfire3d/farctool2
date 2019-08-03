/**
 * blurayguids MAP format
 * Contains metadata about the file stored in .farc files
 */
package com.philosophofee.farctool2;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BRGMapFile {

    //private File data;    // Actual data file to work with
    private int map_type;        // Magic Number to identify file version number
    public int map_entry_count;    // Number of File Entries
    
    public boolean patchable = false;
    
    public List<MapEntry> entries = new ArrayList<>();  // List of entries
    
    public int completed_count;
    public volatile boolean completed = false;
    
    //public BRGMapFile(File data) {
    //    this.data = data;
    //}
    
    public float returnProgress() {
        if ( (float) completed_count == 0 && (float) map_entry_count == 0) {
            return 0;
        }
        return ( (float) completed_count / (float) map_entry_count )*100;
    }
    
    public int returnProgressCount() {
        return completed_count;
    }
    
    public int getMapType() {
        return map_type;
    }
    
    public int getMapEntryCount() {
        return entries.size();
    }
    
    public List<MapEntry> getEntriesList() {
        return entries;
    }
    
    public void parse(File data) {
        /**
         * This begins in a thread simply because I'd like to report back how
         * many files are currently loaded into memory before they're completed.
         * What if some mad man loaded 1,000,000,000 files into his map? Would
         * you want to sit there and wait for them all to load with no
         * indication of progress? I didn't think so.
         */
        Thread parseThread = new Thread() {
            public void run() {
                System.out.println("Hello from new Map Parser thread. Your file name is " + data.getName());
                try {
                    //Create data input stream
                    DataInputStream mapAccess = new DataInputStream(new FileInputStream(data));

                    //Read header
                    int header = mapAccess.readInt();
                    switch(header) {
                        case 256:
                            System.out.println("Detected: LBP1/2 Map File");
                            if (patchable==false) {
                                map_type=1;
                            } else {
                                if (map_type!=1) {
                                    System.out.println("MAP type mismatch!");
                                    break;
                                }
                            }
                            break;
                        case 936:
                            System.out.println("Detected: LBP Vita Map File");
                            if (patchable==false) {
                                map_type=2;
                            } else {
                                if (map_type!=2) {
                                    System.out.println("MAP type mismatch!");
                                    break;
                                }
                            }
                            break;
                        case 21496064:
                            System.out.println("Detected: LBP3 Map File");
                            if (patchable==false) {
                                map_type=3;
                            } else {
                                if (map_type!=3) {
                                    System.out.println("MAP type mismatch!");
                                    break;
                                }
                            }
                            break;
                        default:
                            System.out.println("Unknown map file detected");
                            if (patchable==false) {
                                map_type=0;
                            } else {
                                if (map_type!=0) {
                                    System.out.println("MAP type mismatch!");
                                    break;
                                }
                            }
                            break;
                    }

                    //Read map entry count
                    map_entry_count = mapAccess.readInt();
                    System.out.println(map_entry_count + " entries in file");

                    for (int i=0; i<map_entry_count; i++) {
                        /**
                         * So our documentation for each entry in this 
                         * format looks something like this:
                         * 
                         *              (all big endian...)
                         * - 4 bytes declaring filename length n [2 for LBP3, so skip 2]
                         * - n bytes declaring absolute path to file
                         * - 4 bytes of 0x00000000 [none in LBP3, so skip 4]
                         * - 4 bytes file compilation date in farc
                         * - 4 bytes file size
                         * - 20 bytes SHA1 hash for file
                         * - 4 bytes Global Unique IDentifier (referenced in-game).
                         * 
                         * Rinse and repeat map_entry_count times. 
                         * The following code is based on the above:
                         */
                        
                        // First, read the length of the filename following.
                        if (map_type!=3) { mapAccess.skip(2); } // LBP1/2/V skips 2 bytes
                        // Get filename length short (May be problematic in LBP1/2/V)
                        short fileNameLength = mapAccess.readShort(); // We will use later
                        //System.out.println("Entry size: " + fileNameLength);
                        
                        // Next, we will read the bytes of fileNameLength & send to String.
                        byte[] fileNameBytes = new byte[fileNameLength]; // later is now!
                        mapAccess.read(fileNameBytes); // Read that many bytes.
                        String fileName = new String(fileNameBytes);
                        //System.out.println(fileName);
                        
                        // padding which we will skip, 0x000000 (LBP1/2 ONLY)
                        if (map_type!=3) { mapAccess.skip(4); }
                        
                        // time stamp UNIX
                        int timestamp = mapAccess.readInt();
                        // size
                        int fileSize = mapAccess.readInt();
                        // hash
                        byte[] hash = new byte[20];
                        mapAccess.read(hash);
                        // guid
                        //byte[] guid = new byte[4];
                        int guid = mapAccess.readInt();
                        
                        boolean exists=false;
                        
                        if (patchable==true) {
                            //Iterator it = entries.iterator();
                            for (MapEntry entry : entries) {
                            //while (it.hasNext()) {
                                //MapEntry entry = (MapEntry) it.next();  
                                //System.out.println(entry.getGUID() + " | " + guid);
                                if (entry.getGUID() == guid) {
                                    exists = true;
                                    System.out.println("Entry exists. Patching " + guid);
                                    entry.setHash(hash);
                                    entry.setSize(fileSize);
                                    entry.setTimestamp(timestamp);
                                    
                                }
                            }
                            if (exists==false) {
                                //System.out.println("Entry does not exist. Adding " + guid);
                                MapEntry entry = new MapEntry(
                                fileName,       // Filename (String)
                                timestamp,              // Timestamp (int)
                                fileSize,              // Size (int)
                                hash,   // Hash (byte[20])
                                guid               // GUID (byte[4])
                                );
                                entries.add(entry);
                            }
                        } 
                        else 
                        {
                            MapEntry entry = new MapEntry(
                            fileName,       // Filename (String)
                            timestamp,              // Timestamp (int)
                            fileSize,              // Size (int)
                            hash,   // Hash (byte[20])
                            guid               // GUID (byte[4])
                            );
                            entries.add(entry);
                        }
                        completed_count++;
                    }

                    System.out.println("Completed - size: " + entries.size());
                    
                    completed = true;
                    patchable = true;
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        };
        parseThread.start();
    }
    
    
}
