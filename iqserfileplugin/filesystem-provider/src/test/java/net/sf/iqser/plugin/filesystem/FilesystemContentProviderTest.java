package net.sf.iqser.plugin.filesystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import junit.framework.TestCase;

import com.iqser.core.model.Content;

public class FilesystemContentProviderTest extends TestCase {

    public void testDestroy() {
	fail("Not yet implemented");
    }

    public void testInit() {
	fail("Not yet implemented");
    }

    public void testDoSynchonization() {
	
    	System.setProperty("com.iqser.core.configfile", "../iqser-config.xml");
    	FilesystemContentProvider fscp = new FilesystemContentProvider();
    	fscp.doSynchonization();
    }

    public void testDoHousekeeping() {
	fail("Not yet implemented");
    }

    public void testGetBinaryData() {
	fail("Not yet implemented");
    }

    public void testGetFile() {
	fail("Not yet implemented");
    }

    public void testGetActionsContent() {
	fail("Not yet implemented");
    }

    public void testGetContentString() {
    	
    	//pdf
    	FilesystemContentProvider fscp = new FilesystemContentProvider();
    	String contentUrl = System.getProperty(
    			"testdata.dir", "../file-parser/testdata/ZimbraCommunity.pdf");
		Content content = fscp.getContent(contentUrl);
		assertNotNull(content);
		String fulltext = content.getFulltext();
		assertNotNull(fulltext);
		
		//excel
	    fscp = new FilesystemContentProvider();
    	contentUrl = System.getProperty(
    			"testdata.dir", "../file-parser/testdata/ExcelDataTest.xls");
		content = fscp.getContent(contentUrl);
		assertNotNull(content);
		fulltext = content.getFulltext();
		assertNotNull(fulltext);
		
		//odf
		fscp = new FilesystemContentProvider();
	    contentUrl = System.getProperty(
	    		"testdata.dir", "../file-parser/testdata/ODFDataTest.odt");
		content = fscp.getContent(contentUrl);
		assertNotNull(content);
		fulltext = content.getFulltext();
		assertNotNull(fulltext);
		
		//powerpoint
		fscp = new FilesystemContentProvider();
	    contentUrl = System.getProperty(
	    		"testdata.dir", "../file-parser/testdata/PowerPointTestData.ppt");
		content = fscp.getContent(contentUrl);
		assertNotNull(content);
		fulltext = content.getFulltext();
		assertNotNull(fulltext);
		
		//rtf
		fscp = new FilesystemContentProvider();
	    contentUrl = System.getProperty(
	    		"testdata.dir", "../file-parser/testdata/TestDocument.rtf");
		content = fscp.getContent(contentUrl);
		assertNotNull(content);
		fulltext = content.getFulltext();
		assertNotNull(fulltext);
		
		//doc
		fscp = new FilesystemContentProvider();
	    contentUrl = System.getProperty(
	    		"testdata.dir", "../file-parser/testdata/WordDataTest.doc");
		content = fscp.getContent(contentUrl);
		assertNotNull(content);
		fulltext = content.getFulltext();
		assertNotNull(fulltext);
		
		//text
		fscp = new FilesystemContentProvider();
	    contentUrl = System.getProperty(
	    		"testdata.dir", "../file-parser/testdata/TxtDataTest.txt");
		content = fscp.getContent(contentUrl);
		assertNotNull(content);
		fulltext = content.getFulltext();
		assertNotNull(fulltext);
		
    }

    public void testGetContentInputStream() {
    	
    	//pdf
    	FilesystemContentProvider fscp = new FilesystemContentProvider();
    	String contentUrl = System.getProperty(
    			"testdata.dir", "../file-parser/testdata/ZimbraCommunity.pdf");
    	InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(contentUrl);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Content content = fscp.getContent(inputStream);
		assertNotNull(content);
		String fulltext = content.getFulltext();
		assertNotNull(fulltext);
		
		//excel
		fscp = new FilesystemContentProvider();
    	contentUrl = System.getProperty(
    			"testdata.dir", "../file-parser/testdata/ExcelDataTest.xls");
    	inputStream = null;
		try {
			inputStream = new FileInputStream(contentUrl);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		content = fscp.getContent(inputStream);
		assertNotNull(content);
		fulltext = content.getFulltext();
		assertNotNull(fulltext);
		
		//odf
		fscp = new FilesystemContentProvider();
    	contentUrl = System.getProperty(
    			"testdata.dir", "../file-parser/testdata/ODFDataTest.odt");
    	inputStream = null;
		try {
			inputStream = new FileInputStream(contentUrl);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		content = fscp.getContent(inputStream);
		assertNotNull(content);
		fulltext = content.getFulltext();
		assertNotNull(fulltext);
		
		//powerpoint
		fscp = new FilesystemContentProvider();
    	contentUrl = System.getProperty(
    			"testdata.dir", "../file-parser/testdata/PowerPointTestData.ppt");
    	inputStream = null;
		try {
			inputStream = new FileInputStream(contentUrl);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		content = fscp.getContent(inputStream);
		assertNotNull(content);
		fulltext = content.getFulltext();
		assertNotNull(fulltext);
		
		//rtf
		fscp = new FilesystemContentProvider();
    	contentUrl = System.getProperty(
    			"testdata.dir", "../file-parser/testdata/TestDocument.rtf");
    	inputStream = null;
		try {
			inputStream = new FileInputStream(contentUrl);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		content = fscp.getContent(inputStream);
		assertNotNull(content);
		fulltext = content.getFulltext();
		assertNotNull(fulltext);
		
		//doc
		fscp = new FilesystemContentProvider();
    	contentUrl = System.getProperty(
    			"testdata.dir", "../file-parser/testdata/WordDataTest.doc");
    	inputStream = null;
		try {
			inputStream = new FileInputStream(contentUrl);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		content = fscp.getContent(inputStream);
		assertNotNull(content);
		fulltext = content.getFulltext();
		assertNotNull(fulltext);
		
		//text
		fscp = new FilesystemContentProvider();
    	contentUrl = System.getProperty(
    			"testdata.dir", "../file-parser/testdata/TxtDataTest.txt");
    	inputStream = null;
		try {
			inputStream = new FileInputStream(contentUrl);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		content = fscp.getContent(inputStream);
		assertNotNull(content);
		fulltext = content.getFulltext();
		assertNotNull(fulltext);
		
		
    }

    public void testGetContentUrls() {
		FilesystemContentProvider fscp = new FilesystemContentProvider();
		fscp.init();
		fscp.setId("net.sf.iqser.plugin.filesystem");
		Collection urls = fscp.getContentUrls();
		for (Object url : urls) {
			System.out.println(url);
		}
    }

    public void testOnChangeEventEvent() {
	fail("Not yet implemented");
    }

    public void testPerformActionStringContent() {
    	
    	FilesystemContentProvider fscp = new FilesystemContentProvider();
    	String contentUrl = System.getProperty(
    			"testdata.dir", "../file-parser/testdata/WordDataTest.doc");
    	InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(contentUrl);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Content content = fscp.getContent(inputStream);
		assertNotNull(content);
		String fulltext = content.getFulltext();
		assertNotNull(fulltext);
		content.setContentUrl("D:/test/testdoc.doc");
		fscp.performAction("save", content);
		
		
		//xls
		fscp = new FilesystemContentProvider();
    	contentUrl = System.getProperty(
    			"testdata.dir", "../file-parser/testdata/ExcelDataTest.xls");
    	inputStream = null;
		try {
			inputStream = new FileInputStream(contentUrl);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		content = fscp.getContent(inputStream);
		assertNotNull(content);
		fulltext = content.getFulltext();
		assertNotNull(fulltext);
		content.setContentUrl("D:/test/testxls.xls");
		fscp.performAction("save", content);
		
		//odt
		fscp = new FilesystemContentProvider();
    	contentUrl = System.getProperty(
    			"testdata.dir", "../file-parser/testdata/ODFDataTest.odt");
    	inputStream = null;
		try {
			inputStream = new FileInputStream(contentUrl);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		content = fscp.getContent(inputStream);
		assertNotNull(content);
		fulltext = content.getFulltext();
		assertNotNull(fulltext);
		content.setContentUrl("D:/test/testodt.odt");
		fscp.performAction("save", content);
		
		//ppt
		fscp = new FilesystemContentProvider();
    	contentUrl = System.getProperty(
    			"testdata.dir", "../file-parser/testdata/PowerPointTestData.ppt");
    	inputStream = null;
		try {
			inputStream = new FileInputStream(contentUrl);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		content = fscp.getContent(inputStream);
		assertNotNull(content);
		fulltext = content.getFulltext();
		assertNotNull(fulltext);
		content.setContentUrl("D:/test/testppt.ppt");
		fscp.performAction("save", content);
		
		//rtf
		fscp = new FilesystemContentProvider();
    	contentUrl = System.getProperty(
    			"testdata.dir", "../file-parser/testdata/TestDocument.rtf");
    	inputStream = null;
		try {
			inputStream = new FileInputStream(contentUrl);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		content = fscp.getContent(inputStream);
		assertNotNull(content);
		fulltext = content.getFulltext();
		assertNotNull(fulltext);
		content.setContentUrl("D:/test/testrtf.rtf");
		fscp.performAction("save", content);
		
		
		//pdf
		fscp = new FilesystemContentProvider();
    	contentUrl = System.getProperty(
    			"testdata.dir", "../file-parser/testdata/ZimbraCommunity.pdf");
    	inputStream = null;
		try {
			inputStream = new FileInputStream(contentUrl);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		content = fscp.getContent(inputStream);
		assertNotNull(content);
		fulltext = content.getFulltext();
		assertNotNull(fulltext);
		content.setContentUrl("D:/test/testpdf.pdf");
		fscp.performAction("save", content);
		
		
		fscp = new FilesystemContentProvider();
    	contentUrl = System.getProperty(
    			"testdata.dir", "../file-parser/testdata/ZimbraCommunity.pdf");
    	inputStream = null;
		try {
			inputStream = new FileInputStream(contentUrl);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		content = fscp.getContent(inputStream);
		assertNotNull(content);
		fulltext = content.getFulltext();
		assertNotNull(fulltext);
		content.setContentUrl("D:/test/testpdf.pdf");
		fscp.performAction("delete", content);
    }

}
