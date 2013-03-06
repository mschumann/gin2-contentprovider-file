package net.sf.iqser.plugin.file.parser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import junit.framework.TestCase;
import net.sf.iqser.plugin.file.parser.pdf.PdfFileParser;
import net.sf.iqser.plugin.file.parser.tika.TikaFileParser;
import net.sf.iqser.plugin.file.parser.tika.html.TikaHtmlFileParser;
import net.sf.iqser.plugin.file.parser.tika.msoffice.ExcelFileParser;
import net.sf.iqser.plugin.file.parser.tika.msoffice.PowerPointFileParser;
import net.sf.iqser.plugin.file.parser.tika.msoffice.WordFileParser;
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
		assertTrue(parser instanceof TikaHtmlFileParser);

		parser = factory.getFileParser("sample.html");
		assertNotNull(parser);
		assertTrue(parser instanceof TikaHtmlFileParser);
	}

	public void testGetPdfFileParser() {
		FileParserFactory factory = FileParserFactory.getInstance();
		FileParser parser = factory.getFileParser("sample.pdf");
		assertNotNull(parser);
		assertTrue(parser instanceof PdfFileParser);
	}

	public void testGetWordFileParser() {
		FileParserFactory factory = FileParserFactory.getInstance();
		FileParser parser = factory.getFileParser("sample.doc");
		assertNotNull(parser);
		assertTrue(parser instanceof WordFileParser);

		parser = factory.getFileParser("sample.docx");
		assertNotNull(parser);
		assertTrue(parser instanceof WordFileParser);

		parser = factory.getFileParser("sample.odt");
		assertNotNull(parser);
		assertTrue(parser instanceof WordFileParser);
	}

	public void testGetExcelFileParser() {
		FileParserFactory factory = FileParserFactory.getInstance();
		FileParser parser = factory.getFileParser("sample.xls");
		assertNotNull(parser);
		assertTrue(parser instanceof ExcelFileParser);

		parser = factory.getFileParser("sample.xlsx");
		assertNotNull(parser);
		assertTrue(parser instanceof ExcelFileParser);

		parser = factory.getFileParser("sample.ods");
		assertNotNull(parser);
		assertTrue(parser instanceof ExcelFileParser);
	}

	public void testGetPowerPointFileParser() {
		FileParserFactory factory = FileParserFactory.getInstance();
		FileParser parser = factory.getFileParser("sample.ppt");
		assertNotNull(parser);
		assertTrue(parser instanceof PowerPointFileParser);

		parser = factory.getFileParser("sample.pptx");
		assertNotNull(parser);
		assertTrue(parser instanceof PowerPointFileParser);

		parser = factory.getFileParser("sample.odp");
		assertNotNull(parser);
		assertTrue(parser instanceof PowerPointFileParser);
	}

	public void testGetInvalidFileParser() {
		FileParserFactory factory = FileParserFactory.getInstance();
		FileParser parser = factory.getFileParser("sample.invalid");
		assertNotNull(parser);
		assertTrue(parser instanceof DefaultFileParser);
	}

	public void testGetTextFileParserMimeType(){
		testFileParserMimeType("TxtDataTest.txt", TextFileParser.class);
	}

	public void testGetHtmFileParserMimeType() {
		testFileParserMimeType("HtmlDataTest.html", TikaHtmlFileParser.class);
	}

	public void testGetPdfFileParserMimeType() {
		testFileParserMimeType("ZimbraCommunity.pdf", PdfFileParser.class);
	}

	public void testGetWordFileParserMimeType() {
		testFileParserMimeType("WordDataTest.doc", WordFileParser.class);

		testFileParserMimeType("ODFDataTest.odt", WordFileParser.class);
	}

	public void testGetExcelFileParserMimeType() {
		testFileParserMimeType("ExcelDataTest.xls", ExcelFileParser.class);
	}

	public void testGetPowerPointFileParserMimeType() {
		testFileParserMimeType("PowerPointTestData.ppt", PowerPointFileParser.class);
	}

	public void testGetInvalidFileParserMimeType() {
		//invalid stream
		byte[] bytes = new byte[10];
		InputStream is = new ByteArrayInputStream(bytes);

		FileParserFactory factory = FileParserFactory.getInstance();
		FileParser parser = factory.getFileParser(is);
		assertNotNull(parser);
		assertEquals(TikaFileParser.class, parser.getClass());
	}

	private void testFileParserMimeType(String fileName, Class<?> parserClazz){
		FileParserFactory factory = FileParserFactory.getInstance();
		String dataFolder = System.getProperty("testdata.dir", "../file-parser/testdata");
		File f = new File(dataFolder + "/" + fileName);
		FileInputStream is;
		try {
			is = new FileInputStream(f);
			FileParser parser = factory.getFileParser(is);
			assertNotNull(parser);
			assertEquals(parserClazz, parser.getClass());
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		}
	}

}
