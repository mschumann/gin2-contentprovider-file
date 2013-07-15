package net.sf.iqser.plugin.file.parser.pdf;

import java.io.File;
import java.io.FileInputStream;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;

import com.iqser.core.model.Content;

public class PdfFileParserTest extends TestCase {

	public void testGetContent() throws Exception {
		PdfFileParser parser = new PdfFileParser();
		FileInputStream is = new FileInputStream(new File(System.getProperty("testdata.dir", "../file-parser/testdata")
				+ "/ZimbraCommunity.pdf"));
		Content content = parser.getContent("ZimbraCommunity.pdf", is);

		assertNotNull(content);
		assertEquals("PDF Document", content.getType());

		assertNotNull(content.getAttributeByName("FILENAME"));
		assertEquals("ZimbraCommunity.pdf", content.getAttributeByName("FILENAME").getValue());
		assertFalse(content.getAttributeByName("FILENAME").isKey());

		assertNotNull(content.getAttributeByName("TITLE"));
		assertEquals("Zimbra - Community", content.getAttributeByName("TITLE").getValue());
		assertTrue(content.getAttributeByName("TITLE").isKey());

		assertNotNull(content.getAttributeByName("AUTHOR"));
		assertEquals("christian.magnus", content.getAttributeByName("AUTHOR").getValue());
		assertTrue(content.getAttributeByName("AUTHOR").isKey());

		assertNotNull(content.getAttributeByName("CREATIONDATE"));
		assertEquals("1168011274000", content.getAttributeByName("CREATIONDATE").getValue());
		assertTrue(content.getAttributeByName("CREATIONDATE").isKey());

		assertNotNull(content.getFulltext());
		assertTrue(StringUtils.contains(content.getFulltext(),
				"The Zimbra Collaboration Suite is generally licensed under the terms"));
	}

}
