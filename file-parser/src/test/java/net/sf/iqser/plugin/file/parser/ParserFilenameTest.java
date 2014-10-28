package net.sf.iqser.plugin.file.parser;

import java.io.File;
import java.io.FileInputStream;

import junit.framework.TestCase;

import org.apache.commons.io.FilenameUtils;

import com.iqser.core.model.Content;

public class ParserFilenameTest extends TestCase{

	public void testFileName() throws Exception {
		String testDataDir = System.getProperty("testdata.dir", "../file-parser/testdata");
				
		File root = new File(testDataDir);

		for (File file : root.listFiles()) {

			if (file.isFile()) {
				String absolutePath = file.getAbsolutePath();
				FileParser parser = FileParserFactory.getInstance().getFileParser(absolutePath);				
								
				Content content = parser.getContent(absolutePath, new FileInputStream(file));
				assertNotNull(content);

				String type = content.getType();
				assertNotNull(type);

				String fileName = content.getAttributeByName("FILENAME").getValue();
									
				assertEquals(FilenameUtils.getName(absolutePath), fileName);				
			
			}
		}
	}
}
