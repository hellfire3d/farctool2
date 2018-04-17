package tv.porst.splib.file;

import java.io.File;

/**
 * Interface to be implemented by all callback objects for directory traversal.
 */
public interface IDirectoryTraversalVisitor {

	/**
	 * Invoked when a new file is found during traversal.
	 * 
	 * @param file The next file.
	 */
	void visit(File file);
}