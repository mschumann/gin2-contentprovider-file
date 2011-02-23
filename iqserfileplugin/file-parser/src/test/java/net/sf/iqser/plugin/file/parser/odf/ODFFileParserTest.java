package net.sf.iqser.plugin.file.parser.odf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import junit.framework.TestCase;
import net.sf.iqser.plugin.file.parser.FileParser;
import net.sf.iqser.plugin.file.parser.FileParserException;
import net.sf.iqser.plugin.file.parser.tika.odf.ODFFileParser;

import com.iqser.core.model.Content;

public class ODFFileParserTest extends TestCase{

	public void testODFFileParser() throws IOException, FileParserException{		
		
		FileParser parser = new ODFFileParser();
		FileInputStream is = new FileInputStream(new File(System.getProperty(
				"testdata.dir", "../file-parser/testdata")
				+ "/ODFDataTest.odt"));
		Content content = parser.getContent("ODFDataTest.odt", is);

		assertNotNull(content);
		assertEquals("ODF Document", content.getType());

		assertNotNull(content.getAttributeByName("FILENAME"));
		assertEquals("ODFDataTest.odt",
			content.getAttributeByName("FILENAME").getValue());
		assertFalse(content.getAttributeByName("FILENAME").isKey());
		
		assertNotNull(content.getAttributeByName("TITLE"));
		assertTrue(content.getAttributeByName("TITLE").isKey());
		
		assertNotNull(content.getFulltext());
	}
}
