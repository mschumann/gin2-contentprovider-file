package net.sf.iqser.plugin.file.parser;

import junit.framework.TestCase;

import com.iqser.core.model.Content;

public class DefaultFileParserTest extends TestCase {

	public void testGetContent() throws Exception {
		DefaultFileParser parser = new DefaultFileParser();
		Content content = parser.getContent("sample.xzy", null);

		assertNotNull(content);
		assertEquals("XZY Document", content.getType());

		assertNotNull(content.getAttributeByName("FILENAME"));
		assertEquals("sample.xzy", content.getAttributeByName("FILENAME")
				.getValue());
		assertFalse(content.getAttributeByName("FILENAME").isKey());

		assertNotNull(content.getAttributeByName("TITLE"));
		assertEquals("sample", content.getAttributeByName("TITLE").getValue());
		assertTrue(content.getAttributeByName("TITLE").isKey());
	}

}
