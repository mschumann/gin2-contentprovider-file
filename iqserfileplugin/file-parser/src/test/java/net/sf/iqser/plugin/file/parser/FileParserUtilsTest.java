package net.sf.iqser.plugin.file.parser;

import junit.framework.TestCase;

public class FileParserUtilsTest extends TestCase{

	
	public void testGetFileTitle(){
		
		String fileTitle = FileParserUtils.getFileTitle("test.txt");
		assertNotNull(fileTitle);
		assertEquals("test", fileTitle);
		
		fileTitle = FileParserUtils.getFileTitle("");
		assertNull(fileTitle);
		
		
	
		
	}
}
