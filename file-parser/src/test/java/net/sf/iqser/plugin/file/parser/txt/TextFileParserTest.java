package net.sf.iqser.plugin.file.parser.txt;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import com.iqser.core.model.Content;

public class TextFileParserTest extends TestCase {

    public void testGetContent() throws Exception {
		TextFileParser parser = new TextFileParser();
		String contentSample = "This is the content from the InputStream.";
		InputStream is = new ByteArrayInputStream(contentSample.getBytes());
		Content content = parser.getContent("sample.txt", is);
	
		assertNotNull(content);
		assertEquals("Text Document", content.getType());
	
		assertNotNull(content.getAttributeByName("FILENAME"));
		assertEquals("sample.txt", content.getAttributeByName("FILENAME")
			.getValue());
		assertFalse(content.getAttributeByName("FILENAME").isKey());
	
		assertNotNull(content.getAttributeByName("TITLE"));
		assertEquals("sample", content.getAttributeByName("TITLE").getValue());
		assertTrue(content.getAttributeByName("TITLE").isKey());
	
		assertNotNull(content.getFulltext());
		assertEquals(contentSample, content.getFulltext());
    }

    public void testGetContentNullInputStream() throws Exception {
    	TextFileParser parser = new TextFileParser();    	
    	InputStream is = null;
    	Content content = parser.getContent("sample.txt", is);

    	assertNotNull(content);
    	assertEquals("Text Document", content.getType());

    	assertNotNull(content.getAttributeByName("FILENAME"));
    	assertEquals("sample.txt", content.getAttributeByName("FILENAME").getValue());
    	assertFalse(content.getAttributeByName("FILENAME").isKey());

    	assertNotNull(content.getAttributeByName("TITLE"));
    	assertEquals("sample", content.getAttributeByName("TITLE").getValue());
    	assertTrue(content.getAttributeByName("TITLE").isKey());

    	assertNotNull(content.getFulltext());
    	assertEquals("", content.getFulltext());
   }
       
}
