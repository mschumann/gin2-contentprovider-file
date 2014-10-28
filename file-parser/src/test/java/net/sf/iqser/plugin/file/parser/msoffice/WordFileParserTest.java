package net.sf.iqser.plugin.file.parser.msoffice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import junit.framework.TestCase;
import net.sf.iqser.plugin.file.parser.FileParser;
import net.sf.iqser.plugin.file.parser.FileParserException;
import net.sf.iqser.plugin.file.parser.tika.msoffice.WordFileParser;

import com.iqser.core.model.Content;

public class WordFileParserTest extends TestCase {

	public void testWordFileParser() throws IOException, FileParserException{
		
		FileParser parser = new WordFileParser();
		FileInputStream is = new FileInputStream(new File(System.getProperty(
				"testdata.dir", "../file-parser/testdata")
				+ "/WordDataTest.doc"));
		Content content = parser.getContent("WordDataTest.doc", is);

		assertNotNull(content);
		assertEquals("DOC Document", content.getType());

		assertNotNull(content.getAttributeByName("FILENAME"));
		assertEquals("WordDataTest.doc",
			content.getAttributeByName("FILENAME").getValue());
		assertFalse(content.getAttributeByName("FILENAME").isKey());
		
		assertNotNull(content.getAttributeByName("TITLE"));
		assertTrue(content.getAttributeByName("TITLE").isKey());
		
		assertNotNull(content.getFulltext());
		
	}
	
	public void testWordFileParserODT() throws IOException, FileParserException{		
		
		FileParser parser = new WordFileParser();
		FileInputStream is = new FileInputStream(new File(System.getProperty(
				"testdata.dir", "../file-parser/testdata")
				+ "/ODFDataTest.odt"));
		Content content = parser.getContent("ODFDataTest.odt", is);

		assertNotNull(content);
		assertEquals("DOC Document", content.getType());

		assertNotNull(content.getAttributeByName("FILENAME"));
		assertEquals("ODFDataTest.odt",
			content.getAttributeByName("FILENAME").getValue());
		assertFalse(content.getAttributeByName("FILENAME").isKey());
		
		assertNotNull(content.getAttributeByName("TITLE"));
		assertTrue(content.getAttributeByName("TITLE").isKey());
		
		assertNotNull(content.getFulltext());
	}
}
