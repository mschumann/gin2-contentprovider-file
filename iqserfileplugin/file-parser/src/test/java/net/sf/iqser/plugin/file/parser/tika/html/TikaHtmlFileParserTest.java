package net.sf.iqser.plugin.file.parser.tika.html;

import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;
import net.sf.iqser.plugin.file.parser.FileParser;
import net.sf.iqser.plugin.file.parser.FileParserException;

import com.iqser.core.model.Attribute;
import com.iqser.core.model.Content;

public class TikaHtmlFileParserTest extends TestCase{

	public void testTikaHtmlFileParser() throws FileParserException, IOException{
		
		FileParser fp = new TikaHtmlFileParser();
		URL url = new URL("http://www.iqser.com/");
		
		
		Content content = fp.getContent("iqser.com", url.openStream());

		assertNotNull(content);
		System.out.println(content.getFulltext());
		System.out.println(content.getAttributeByName("FILENAME"));
		System.out.println(content.getAttributeByName("TITLE"));
		
		for (Attribute attribute : content.getAttributes()) {
			String name = attribute.getName();
			System.out.println(name);
			String value = attribute.getValue();
			System.out.println(value );
		}
		
//		assertEquals("HTML Document", content.getType());
//
//		assertNotNull(content.getAttributeByName("FILENAME"));
//		assertEquals("iqser.com", content.getAttributeByName("FILENAME")
//			.getValue());
//		assertFalse(content.getAttributeByName("FILENAME").isKey());
//
//		assertNotNull(content.getAttributeByName("TITLE"));
//		assertEquals("iqser", content.getAttributeByName("TITLE").getValue());
//		assertTrue(content.getAttributeByName("TITLE").isKey());
//
//		assertNotNull(content.getFulltext());
	}
}
