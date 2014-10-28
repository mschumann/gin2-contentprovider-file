package net.sf.iqser.plugin.file.parser.zip;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * zip file bean.
 * 
 * @author Alexandru Galos
 * 
 */
public class ZipFileModel {

	/**
	 * a zip file which represents the zip archive with all the contents.
	 */
	private ZipFile zipFile;

	/**
	 * a zip entry which represents a file from a zip archive.
	 */
	private ZipEntry zipEntry;

	/**
	 * returns a zip file.
	 * 
	 * @return the zipFile a zip file
	 */
	public ZipFile getZipFile() {
		return zipFile;
	}

	/**
	 * 
	 * sets a zip file.
	 * 
	 * @param zipFile a zip file
	 */
	public void setZipFile(ZipFile zipFile) {
		this.zipFile = zipFile;
	}

	/**
	 * returns a zip entry.
	 * 
	 * @return zipEntry  an entry in a zip file
	 */
	public ZipEntry getZipEntry() {
		return zipEntry;
	}

	/**
	 * sets a zip entry.
	 * 
	 * @param zipEntry an entry in a zip file
	 */
	public void setZipEntry(ZipEntry zipEntry) {
		this.zipEntry = zipEntry;
	}

}
