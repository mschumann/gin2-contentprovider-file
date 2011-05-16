package net.sf.iqser.plugin.file.parser.msoffice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.iqser.core.model.Attribute;
import com.iqser.core.model.Content;

import net.sf.iqser.plugin.file.parser.FileParser;
import net.sf.iqser.plugin.file.parser.FileParserException;
import net.sf.iqser.plugin.file.parser.tika.msoffice.ExcelFileParser;
import junit.framework.TestCase;

public class ExcelFileParserTest extends TestCase{

	private static Logger log = Logger.getLogger(ExcelFileParserTest.class);

	public void testExcelFileParser() throws FileParserException, FileNotFoundException{
		
		FileParser parser = new ExcelFileParser();
		FileInputStream is = new FileInputStream(new File(System.getProperty(
				"testdata.dir", "../file-parser/testdata")
				+ "/ExcelDataTest.xls"));
		Content content = parser.getContent("ExcelDataTest.xls", is);
		
		Collection<Attribute> attributes = content.getAttributes();
		
		for (Attribute attribute : attributes) {
			String name = attribute.getName();
			String value = attribute.getValue();
			assertNotNull(value);
			assertTrue(value.trim().length() > 0);
			log.debug("Attribute name="+name + " value="+value);
		}
	
	}
}
