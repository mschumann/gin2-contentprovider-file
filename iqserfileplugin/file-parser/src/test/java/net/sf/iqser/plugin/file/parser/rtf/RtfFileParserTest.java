package net.sf.iqser.plugin.file.parser.rtf;

import java.io.File;
import java.io.FileInputStream;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;

import com.iqser.core.model.Content;

public class RtfFileParserTest extends TestCase {

    public void testGetContent() throws Exception {
	RtfFileParser parser = new RtfFileParser();
	FileInputStream is = new FileInputStream(
		new File(System.getProperty("testdata.dir",
			"./file-parser/testdata") + "/TestDocument.rtf"));
	Content content = parser.getContent("TestDocument.rtf", is);

	assertNotNull(content);
	assertEquals("RTF Document", content.getType());

	assertNotNull(content.getAttributeByName("FILENAME"));
	assertEquals("TestDocument.rtf", content.getAttributeByName("FILENAME")
		.getValue());
	assertFalse(content.getAttributeByName("FILENAME").isKey());

	assertNotNull(content.getAttributeByName("TITLE"));
	assertEquals("TestDocument", content.getAttributeByName("TITLE")
		.getValue());
	assertTrue(content.getAttributeByName("TITLE").isKey());

	assertNotNull(content.getFulltext());
	assertTrue(StringUtils.contains(content.getFulltext(),
		"Ziel des Dokuments ist es den Prozess der Qualit�tssicherung"));
    }

}
