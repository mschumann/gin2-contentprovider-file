package net.sf.iqser.plugin.filesystem;

import java.io.File;
import java.util.Collection;
import java.util.Properties;

import junit.framework.TestCase;
import net.sf.iqser.plugin.filesystem.test.MockAnalyzerTaskStarter;
import net.sf.iqser.plugin.filesystem.test.MockRepository;
import net.sf.iqser.plugin.filesystem.test.TestServiceLocator;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.PropertyConfigurator;

import com.iqser.core.config.Configuration;
import com.iqser.core.model.Attribute;
import com.iqser.core.model.Content;

public class FileSystemContentProviderFilenameTest extends TestCase {

		FilesystemContentProvider fscp;
		String testDataDir;

		@Override
		protected void setUp() throws Exception {

			/*testDataDir = System.getProperty("testdata.dir", "testdata");
			// testDataDir = System.getProperty("testdata.dir", "D:/testdata");

			PropertyConfigurator.configure("src/test/resources/log4j.properties");

			super.setUp();

			Properties initParams = new Properties();
			initParams.setProperty("folder", "[" + testDataDir + "/testSynch]");			
			initParams.setProperty("filter-folder-include", "[" + testDataDir
					+ "/testSynch][" + testDataDir + "/testAttributes/]");
			initParams.setProperty("filter-folder-exclude", "[" + testDataDir
					+ "/testSynch/exclude/]");
			initParams
					.setProperty(
							"attribute.mappings",
							"{AUTHOR:Autor, Last-Author:Autor, Author:Autor, title:Bezeichnung,TITLE:Bezeichnung}");
			initParams.setProperty("key-attributes", "[Autor][Bezeichnung]");
			fscp = new FilesystemContentProvider();
			fscp.setInitParams(initParams);
			fscp.setType("HTML Page");
			fscp.setId("com.iqser.plugin.web.base");
			fscp.setInitParams(initParams);
			fscp.init();

			Configuration
					.configure(new File("src/test/resources/iqser-config.xml"));

			// fscp =
			// FileSystemContentProviderCreator.getFilesystemContentProvider(testDataDir);
			TestServiceLocator sl = (TestServiceLocator) Configuration
					.getConfiguration().getServiceLocator();
			MockRepository rep = new MockRepository();
			rep.init();

			sl.setRepository(rep);
			sl.setAnalyzerTaskStarter(new MockAnalyzerTaskStarter());

			// delete testdata/output
			File file = new File(testDataDir + "/output");
			if (file.exists()) {
				file.delete();
			}*/
		}

		@Override
		protected void tearDown() throws Exception {
			/*fscp.destroy();
			// delete testdata/output
			File file = new File(testDataDir + "/output");
			if (file.exists()) {
				file.delete();
			}
			super.tearDown();*/
		}
		
		public void testGetContentString() {

			/*File root = new File(testDataDir);

			for (File file : root.listFiles()) {

				if (file.isFile()) {
					String absolutePath = file.getAbsolutePath();
					Content content = fscp.getContent(absolutePath);
					assertNotNull(content);

					Collection<Attribute> attributes = content.getAttributes();
					String type = content.getType();
					assertNotNull(type);

					String fileName = content.getAttributeByName("FILENAME").getValue();
										
					assertEquals(FilenameUtils.getName(absolutePath), fileName);				
					
					assertEquals(absolutePath, content.getContentUrl());
				}
			}*/
		}

}
