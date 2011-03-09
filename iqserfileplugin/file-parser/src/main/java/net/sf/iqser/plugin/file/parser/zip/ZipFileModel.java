package net.sf.iqser.plugin.file.parser.zip;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipFileModel {

	private ZipFile zipFile;
	
	public ZipFile getZipFile() {
		return zipFile;
	}

	public void setZipFile(ZipFile zipFile) {
		this.zipFile = zipFile;
	}

	public ZipEntry getZipEntry() {
		return zipEntry;
	}

	public void setZipEntry(ZipEntry zipEntry) {
		this.zipEntry = zipEntry;
	}

	private ZipEntry zipEntry;
	
	
}
