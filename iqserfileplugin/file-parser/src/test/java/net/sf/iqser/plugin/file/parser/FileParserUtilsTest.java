package net.sf.iqser.plugin.file.parser;

import junit.framework.TestCase;

public class FileParserUtilsTest extends TestCase {

	public void testGetFileTitle() {

		String fileTitle = FileParserUtils.getFileTitle("test.txt");
		assertNotNull(fileTitle);
		assertEquals("test", fileTitle);

		fileTitle = FileParserUtils.getFileTitle("");
		assertNull(fileTitle);

	}

	public void testCleanUpText() {
		String nullCharacter = "\u0000";

		String validText = "Valid text without NULL-characters ...";
		String invalidText = "Inalid text without NULL-characters ...";

		assertEquals(validText, FileParserUtils.cleanUpText(validText));
		assertEquals(invalidText,
				FileParserUtils.cleanUpText(nullCharacter + invalidText + nullCharacter + nullCharacter));
	}
}
