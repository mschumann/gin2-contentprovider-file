package net.sf.iqser.plugin.file.parser.html;

import java.net.URL;

import junit.framework.TestCase;

import com.iqser.core.model.Content;

public class HtmlFileParserTest extends TestCase {

	public void testGetContent() throws Exception {
		URL url = new URL(
				"http://www.iqser.com/");
		HtmlFileParser parser = new HtmlFileParser();

		Content content = parser.getContent("iqser.com", url.openStream());

		assertNotNull(content);
		assertEquals("HTML Document", content.getType());

		assertNotNull(content.getAttributeByName("FILENAME"));
		assertEquals("iqser.com", content.getAttributeByName("FILENAME")
				.getValue());
		assertFalse(content.getAttributeByName("FILENAME").isKey());

		assertNotNull(content.getAttributeByName("TITLE"));
		assertEquals("iqser", content.getAttributeByName("TITLE").getValue());
		assertTrue(content.getAttributeByName("TITLE").isKey());

		assertNotNull(content.getFulltext());

	}

}
