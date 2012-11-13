package net.sf.iqser.plugin.filesystem;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;

import com.iqser.core.exception.IQserRuntimeException;

/**
 * File Scanner--also for zip files.
 * 
 * @author alexandru galos
 */
public class FileScanner {
	/**
	 * Logger for this class.
	 */
	private static Logger logger = Logger.getLogger(FileScanner.class);

	/**
	 * Folder Filter.
	 */
	private FileFilter pathFilter;

	/**
	 * Folders.
	 */
	private List<String> folders;

	/**
	 * Constructor.
	 * 
	 * @param roots
	 *            A Collection of Pathnames (String)
	 * @param pathFilter
	 *            A AcceptedPathFilter
	 */
	public FileScanner(Collection<String> roots, FileFilter pathFilter) {
		folders = new ArrayList<String>();

		this.pathFilter = pathFilter;

		if (roots != null) {
			for (Iterator<String> iter = roots.iterator(); iter.hasNext();) {
				File root = new File(iter.next());
				folders.addAll(scanFolder(root));
			}
		}
	}

	/**
	 * Constructor.
	 */
	public FileScanner() {
		folders = new ArrayList<String>();
	}

	/**
	 * Scan for accepted Files in a folder.
	 * 
	 * @param filter
	 *            FileFilter.
	 * 
	 * @return A List of Filenames (String). Guaranteed not null.
	 */
	public Collection<String> scanFiles(FileFilter filter) {
		ArrayList<String> list = new ArrayList<String>();

		for (Iterator<String> iter = folders.iterator(); iter.hasNext();) {
			String s = iter.next();
			File folder = new File(s);

			if (folder.exists()) {

				File[] subs = folder.listFiles(filter);

				if (subs != null) {
					for (int i = 0; i < subs.length; i++) {
						if (subs[i].getName().toLowerCase().endsWith(".zip")) {
							try {
								List<String> zipContent = listZipContent(subs[i], filter);
								list.addAll(zipContent);
							} catch (ZipException e) {
								throw new IQserRuntimeException(e);
							} catch (IOException e) {
								throw new IQserRuntimeException(e);
							}
						} else
							list.add(subs[i].getAbsolutePath());
					}
				}
			}
		}

		return list;
	}

	private List<String> listZipContent(File folder, FileFilter filter) throws ZipException, IOException {

		ZipFile zipFile = new ZipFile(folder);

		Enumeration<? extends ZipEntry> entries = zipFile.entries();

		List<String> zipContent = new ArrayList<String>();

		while (entries.hasMoreElements()) {

			ZipEntry zipEntry = entries.nextElement();
			String name = zipEntry.getName();

			if (!zipEntry.isDirectory()) {

				String path = "zip://" + folder + "!/" + name;
				File file = new File(path);
				if (filter.accept(file)) {
					zipContent.add(path);
				}

			}

		}

		return zipContent;
	}

	/**
	 * Scan a Folder for accepted Sub-Folders.
	 * 
	 * @param parent
	 *            Folder to scan.
	 * 
	 * @return A Collection of Foldernames (String).
	 */
	private Collection<String> scanFolder(File parent) {
		logger.debug("scanFolder(File parent=" + parent + ") - start");

		ArrayList<String> list = new ArrayList<String>();
		list.add(parent.getAbsolutePath());

		if (parent != null && parent.isDirectory()) {
			File[] subs = parent.listFiles(pathFilter);

			if (subs != null) {
				for (int i = 0; i < subs.length; i++) {
					list.addAll(scanFolder(subs[i]));
				}

				// list.add( parent.getAbsolutePath() );
			}
		}

		logger.debug("scanFolder(File parent=" + parent + ") - end - return value=" + list);
		return list;
	}

	/**
	 * Returns the folders.
	 * 
	 * @return A List of the folders
	 */
	public List<String> getFolders() {
		logger.debug("getFolders() - start");

		logger.debug("getFolders() - end - return value=" + folders);
		return folders;
	}

	/**
	 * Sets the folders.
	 * 
	 * @param folders
	 *            The folders to set.
	 */
	public void setFolders(List<String> folders) {
		logger.debug("setFolders(List folders=" + folders + ") - start");

		this.folders = folders;

		logger.debug("setFolders(List folders=" + folders + ") - end");
	}

	/**
	 * Returns the pathFilter.
	 * 
	 * @return AcceptedPathFilter
	 */
	public FileFilter getPathFilter() {
		logger.debug("getPathFilter() - start");

		logger.debug("getPathFilter() - end - return value=" + pathFilter);
		return pathFilter;
	}

	/**
	 * Sets the pathFilter.
	 * 
	 * @param pathFilter
	 *            The pathFilter to set.
	 */
	public void setPathFilter(FileFilter pathFilter) {
		logger.debug("setPathFilter(AcceptedPathFilter pathFilter=" + pathFilter + ") - start");

		this.pathFilter = pathFilter;

		logger.debug("setPathFilter(AcceptedPathFilter pathFilter=" + pathFilter + ") - end");
	}
}
