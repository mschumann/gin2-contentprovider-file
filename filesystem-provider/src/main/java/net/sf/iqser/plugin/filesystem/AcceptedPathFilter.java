package net.sf.iqser.plugin.filesystem;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * 
 * the accepted paths filter.
 * @author alexandru.galos
 *
 */
public class AcceptedPathFilter implements FileFilter {
	/** Logger for this class. */
	private static Logger logger = Logger.getLogger(AcceptedPathFilter.class);

	/** List of path names that will be included. */
	private final List<String> acceptedPath = new ArrayList<String>();

	/** List of path names that will be ignored. */
	private final List<String> deniedPath = new ArrayList<String>();

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

		if (!file.isDirectory()) {
			logger.debug("accept(File file=" + file + ") - end - return value="
					+ false);
			return false;
		}

		if (!acceptedPath.isEmpty()){
			// Is Path explizit accepted?
			for (String path : acceptedPath) {
				File compare = new File(path);

				if (file.getAbsolutePath().startsWith(compare.getAbsolutePath())) {
					// Is Path sub dir of a accepted dir and explict denied?
					for (Object element2 : deniedPath) {
						compare = new File((String) element2);

						if (file.getAbsolutePath().startsWith(
								compare.getAbsolutePath())) {
							logger.debug("accept(File file=" + file
									+ ") - end - return value=" + false);
							return false;
						}
					}

					logger.debug("accept(File file=" + file
							+ ") - end - return value=" + true);
					return true;
				}
			}
		}else{
			// Is Path sub dir of a accepted dir and explict denied?
			for (Object element : deniedPath) {
				File compare = new File((String) element);

				if (file.getAbsolutePath().startsWith(
						compare.getAbsolutePath())) {
					logger.debug("accept(File file=" + file
							+ ") - end - return value=" + false);
					return false;
				}
			}
			return true;
		}
		logger.debug("accept(File file=" + file + ") - end - return value="
				+ false);
		return false;
	}

	/**
	 * Add a Collection of accepted Pathnames.
	 * 
	 * @param accepted
	 *            A Collection of Pathnames (String)
	 */
	public void addAccepted(Collection<String> accepted) {
		logger.debug("addAccepted(Collection accepted=" + accepted
				+ ") - start");

		if (accepted != null) {
			for (String string : accepted) {
				String s = string;
				addAcceptedPath(s);
			}
		}

		logger.debug("addAccepted(Collection accepted=" + accepted + ") - end");
	}

	/**
	 * Add a accepted Pathname.
	 * 
	 * @param path
	 *            A Pathname
	 */
	public void addAcceptedPath(String path) {
		logger.debug("addAcceptedPath(String path=" + path + ") - start");

		if (path != null && !path.equals("")) {
			acceptedPath.add(path);
		}

		logger.debug("addAcceptedPath(String path=" + path + ") - end");
	}

	/**
	 * Add a Collection of denied Pathnames.
	 * 
	 * @param denied
	 *            A Collection of Pathnames (String)
	 */
	public void addDenied(Collection<String> denied) {
		logger.debug("addDenied(Collection denied=" + denied + ") - start");

		if (denied != null) {
			for (String string : denied) {
				String s = string;
				addDeniedPath(s);
			}
		}

		logger.debug("addDenied(Collection denied=" + denied + ") - end");
	}

	/**
	 * Add a denied Pathname.
	 * 
	 * @param path
	 *            A Pathname
	 */
	public void addDeniedPath(String path) {
		logger.debug("addDeniedPath(String path=" + path + ") - start");

		if (path != null && !path.equals("")) {
			deniedPath.add(path);
		}

		logger.debug("addDeniedPath(String path=" + path + ") - end");
	}

}
