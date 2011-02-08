package net.sf.iqser.plugin.file.parser;

import junit.framework.TestCase;
import net.sf.iqser.plugin.file.parser.html.HtmlFileParser;
import net.sf.iqser.plugin.file.parser.pdf.PdfFileParser;
import net.sf.iqser.plugin.file.parser.txt.TextFileParser;

public class FileParserFactoryTest extends TestCase {

	public void testGetTextFileParser() {
		FileParserFactory factory = FileParserFactory.getInstance();
		FileParser parser = factory.getFileParser("sample.txt");
		assertNotNull(parser);
		assertTrue(parser instanceof TextFileParser);
	}

	public void testGetHtmFileParser() {
		FileParserFactory factory = FileParserFactory.getInstance();
		FileParser parser = factory.getFileParser("sample.htm");
		assertNotNull(parser);
		assertTrue(parser instanceof HtmlFileParser);
	}
	
	public void testGetHtmlFileParser() {
		FileParserFactory factory = FileParserFactory.getInstance();
		FileParser parser = factory.getFileParser("sample.html");
		assertNotNull(parser);
		assertTrue(parser instanceof HtmlFileParser);
	}
	
	public void testGetPdfFileParser() {
		FileParserFactory factory = FileParserFactory.getInstance();
		FileParser parser = factory.getFileParser("sample.pdf");
		assertNotNull(parser);
		assertTrue(parser instanceof PdfFileParser);
	}
	
	public void testGetInvalidFileParser() {
		FileParserFactory factory = FileParserFactory.getInstance();
		FileParser parser = factory.getFileParser("sample.invalid");
		assertNotNull(parser);
		assertTrue(parser instanceof DefaultFileParser);
	}
}
