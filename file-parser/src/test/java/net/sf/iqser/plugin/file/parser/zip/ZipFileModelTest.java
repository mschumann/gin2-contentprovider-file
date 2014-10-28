package net.sf.iqser.plugin.file.parser.zip;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import junit.framework.TestCase;

public class ZipFileModelTest extends TestCase{

	
	public void testZipFileModel() throws IOException{
		String zipFile = System.getProperty(
				"testdata.dir", "../file-parser/testdata")
				+ "/testzipfiles.zip";
		ZipFileModel zfm = new ZipFileModel();
		zfm.setZipEntry(new ZipEntry("zipEntry"));
		zfm.setZipFile(new ZipFile(zipFile));
		
		assertNotNull(zfm.getZipFile());
		assertNotNull(zfm.getZipEntry());
		
		
	}
}
