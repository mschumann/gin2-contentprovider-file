package net.sf.iqser.plugin.file.parser.msoffice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;

import org.apache.log4j.Logger;

import junit.framework.TestCase;

import net.sf.iqser.plugin.file.parser.FileParser;
import net.sf.iqser.plugin.file.parser.FileParserException;
import net.sf.iqser.plugin.file.parser.tika.msoffice.ExcelFileParser;

import com.iqser.core.model.Attribute;
import com.iqser.core.model.Content;

public class PowerPointFileParserTest extends TestCase{
	
	private static Logger log = Logger.getLogger(PowerPointFileParserTest.class); 

	public void testPowerPointFileParser() throws FileNotFoundException, FileParserException{
		FileParser parser = new ExcelFileParser();
		FileInputStream is = new FileInputStream(new File(System.getProperty(
				"testdata.dir", "../file-parser/testdata")
				+ "/PowerPointTestData.ppt"));
		Content content = parser.getContent("PowerPointTestData.ppt", is);
		
		Collection<Attribute> attributes = content.getAttributes();
		
		for (Attribute attribute : attributes) {
			String name = attribute.getName();
			String value = attribute.getValue();
			log.debug("Attribute name="+name + " value="+value);
		}
	}
}