package net.sf.iqser.plugin.filesystem;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * 
 * filter for files.
 *
 */
public class AcceptFileFilter implements FileFilter {

	/** Default Logger. */
	private static Logger logger = Logger.getLogger(AcceptFileFilter.class);

	/** A List of all accepted Filetypes. */
	private List<String> accptedFiletypes = null;

	/** Minimum length of the extention. */
	private int minLength = 1;

	/**
	 * Constructor.
	 */
	public AcceptFileFilter() {
		accptedFiletypes = new LinkedList<String>();
	}

	/**
	 * Constructor.
	 * 
	 * @param accptedFiletypes
	 *            List of Filetypes
	 */
	public AcceptFileFilter(List<String> accptedFiletypes) {
		this.accptedFiletypes = accptedFiletypes;
	}

	/**
	 * Interface implementation.
	 * 
	 * @param file
	 *            A File
	 * @return boolean
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File file) {
		logger.debug("accept(File file=" + file + ") - start");

		if (file == null) {
			logger.debug("accept(File file=" + file + ") - end - return value=" + false);
			return false;
		}

		if (file.isHidden()) {
			logger.debug("accept(File file=" + file + ") - end - return value=" + false);
			return false;
		}

		if (file.isDirectory()) {
			logger.debug("accept(File file=" + file + ") - end - return value=" + false);
			return false;
		}

		if (accptedFiletypes != null) {
			for (String string : accptedFiletypes) {
				String accept = string;

				if (file.getName().endsWith(accept)) {
					logger.debug("accept(File file=" + file + ") - end - return value=" + true);
					return true;
				}
			}
		}

		logger.debug("accept(File file=" + file + ") - end - return value=" + false);
		return false;
	}

	/**
	 * Add a new Filetype to the list.
	 * 
	 * @param type
	 *            Files extension (For instance: doc, rtf, etc.)
	 */
	public void addAcceptedFiletype(String type) {
		logger.debug("addAcceptedFiletype(String type=" + type + ") - start");

		if (minLength < type.length()) {
			minLength = type.length();
		}

		accptedFiletypes.add(type);

		logger.debug("addAcceptedFiletype(String type=" + type + ") - end");
	}

}
