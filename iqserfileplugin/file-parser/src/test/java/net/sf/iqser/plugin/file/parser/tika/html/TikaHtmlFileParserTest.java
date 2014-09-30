package net.sf.iqser.plugin.file.parser.tika.html;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;
import net.sf.iqser.plugin.file.parser.FileParser;
import net.sf.iqser.plugin.file.parser.FileParserException;

import org.apache.log4j.Logger;

import com.iqser.core.model.Attribute;
import com.iqser.core.model.Content;

public class TikaHtmlFileParserTest extends TestCase{

	private static Logger log = Logger.getLogger(TikaHtmlFileParserTest.class); 
	
	public void testTikaHtmlFileParser() throws FileParserException, IOException{
		
		FileParser fp = new TikaHtmlFileParser();
		InputStream is = TikaHtmlFileParserTest.class.getResourceAsStream("/Streit-um-Marburger-Professor-FAZ.html");
		
		Content content = fp.getContent("iqser.com", is);

		assertNotNull(content);
		log.debug(content.getFulltext());
		log.debug(content.getAttributeByName("FILENAME"));
		log.debug(content.getAttributeByName("TITLE"));
		
		for (Attribute attribute : content.getAttributes()) {
			String name = attribute.getName();			
			String value = attribute.getValue();
			log.debug("Attribute name="+name + " value="+value);
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

	public void testSetLastModifiedDate() {
		Content c = new Content();
		c.addAttribute(new Attribute("LAST-MODIFIED", "2014-03-30T16:46:13+0200", Attribute.ATTRIBUTE_TYPE_TEXT));
		TikaHtmlFileParser.setLastModifiedDate(c);
		
		assertEquals(1396190773000L, c.getModificationDate());
	}
}
