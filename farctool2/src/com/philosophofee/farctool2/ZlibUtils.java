
package com.philosophofee.farctool2;

import java.io.FileOutputStream;
import java.io.IOException;
/*

Custom Compression Full Format // Compresses files in 32 KiB slices, includes dependencies
	4 byte magic_number // Magic Number to identify file type
	4 byte game_revision // Revision of the game, when this file was added/last modified
	4 byte offset_dependency_list // Offset from start of the file to dependency list
	2 byte load_dynamic_flag // Indicates dynamic data that changes at runtime? 0x4C44 "LD" = true, 0x0000 = false (game_revision == 626)
	2 byte unknown_1 // Unknown, known values: 0x0004 (4) - 0x0017 (23) (game_revision == 626)
	1 byte unknown_2 // Unknown, known value: 0x07 (game_revision == 626 && load_dynamic_flag == "LD")
	1 byte unknown_3 // Unknown, known value: 0x01 (0x00000190 (394) <= game_revision <= 0x0000026E (622) or game_revision == 626)
	2 byte encrypted_body // 0x01 = false, 0x02 = true, encryption covers everything from here up to footer (dependency_entry_count)
	2 byte stream_entry_count // Number of zlib streams (= ceil(file_size_uncompressed / 2^15))
	stream_entry_count times: // Per stream a custom 4 byte header with size infos
		2 byte size_compressed // Size of the zlib stream (including zlib header, and checksum)
		2 byte size_uncompressed // Size of file slice (for non final stream: 2^15)
	stream_entry_count times: // Actual zlib streams containing the file slices (concatenate uncompressed for real file)
		size_compressed byte zlib_stream // Single complete zlib stream
	4 byte dependency_entry_count // Number of File Depenencies
	dependency_entry_count times: // List of dependencies
		1 byte dependency_type // The type of this dependency
		dependency_type 0x01: // A dependency defined by SHA1 Hash of file
			20 byte hash // SHA1 Hash of this dependency
		dependency_type 0x02: // A dependency defined by GUID of file
			4 byte guid // GUID of this dependency
		4 byte spu_affinity // affinity of asset towards specific SPUs (0b00000000 = PPU)? file type?

Custom Compression Texture Format // Compresses files in 32 KiB slices, .tex Magic Number
	4 byte magic_number // Magic Number to identify file type
	2 byte encrypted_body // 0x01 = false, 0x02 = true, encryption covers everything from here onwards
	2 byte stream_entry_count // Number of zlib streams (= ceil(file_size_uncompressed / 2^15))
	stream_entry_count times: // Per stream a custom 4 byte header with size infos
		2 byte size_compressed // Size of the zlib stream (including zlib header, and checksum)
		2 byte size_uncompressed // Size of file slice (for non final stream: 2^15)
	stream_entry_count times: // Actual zlib streams containing the file slices (concatenate uncompressed for real file)
		size_compressed byte zlib_stream // Single complete zlib stream


*/
public class ZlibUtils {

    public static byte[] decompressThis(byte[] input) {
        //usage: byte[] input LBP zlib file with chunks.
        // outputs byte[] decompressed data stream
        try (FileOutputStream fos = new FileOutputStream("temp1")) {
            fos.write(input);
        } catch (IOException ex) {}
        
        
        
        
        return null;
    }
    
}
