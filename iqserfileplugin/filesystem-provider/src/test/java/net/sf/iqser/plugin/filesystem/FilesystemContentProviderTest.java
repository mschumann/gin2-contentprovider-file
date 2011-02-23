package net.sf.iqser.plugin.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;
import net.sf.iqser.plugin.filesystem.test.MockAnalyzerTaskStarter;
import net.sf.iqser.plugin.filesystem.test.MockRepository;
import net.sf.iqser.plugin.filesystem.test.TestServiceLocator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.PropertyConfigurator;

import com.iqser.core.config.Configuration;
import com.iqser.core.exception.IQserTechnicalException;
import com.iqser.core.model.Attribute;
import com.iqser.core.model.Content;
import com.iqser.core.repository.Repository;

public class FilesystemContentProviderTest extends TestCase {

	FilesystemContentProvider fscp;
	String testDataDir;

	@Override
	protected void setUp() throws Exception {

		testDataDir = System.getProperty("testdata.dir", "testdata");
		//testDataDir = System.getProperty("testdata.dir", "D:/testdata");

		PropertyConfigurator.configure("src/test/resources/log4j.properties");

		super.setUp();

		Properties initParams = new Properties();
		initParams.setProperty("folder", testDataDir + "/testSynch");
		initParams.setProperty("filter-pattern", "txt");
		initParams.setProperty("filter-folder-include", testDataDir
				+ "/testSynch");
		initParams.setProperty("filter-folder-exclude", testDataDir
				+ "/testSynch/exclude/");
		initParams
				.setProperty("attribute.mappings",
						"{AUTHOR:Autor, Last-Author:Autor, Author:Autor, title:Bezeichnung}");

		fscp = new FilesystemContentProvider();
		fscp.setInitParams(initParams);
		fscp.setType("HTML Page");
		fscp.setId("com.iqser.plugin.web.base");
		fscp.setInitParams(initParams);
		fscp.init();

		Configuration
				.configure(new File("src/test/resources/iqser-config.xml"));

		TestServiceLocator sl = (TestServiceLocator) Configuration
				.getConfiguration().getServiceLocator();
		MockRepository rep = new MockRepository();
		rep.init();

		sl.setRepository(rep);
		sl.setAnalyzerTaskStarter(new MockAnalyzerTaskStarter());
		
		//delete testdata/output
		File file = new File(testDataDir + "/output");
		if (file.exists()){
			file.delete();
		}
	}

	@Override
	protected void tearDown() throws Exception {
		fscp.destroy();
		//delete testdata/output
		File file = new File(testDataDir + "/output");
		if (file.exists()){
			file.delete();
		}
		super.tearDown();
	}

	public void testDestroy() {
		fscp.destroy(); // nothing to do
	}

	public void testInit() {
		// fscp.init(); //nothing to do
		Field field = null;
		try {
			field = fscp.getClass().getDeclaredField("attributeMappings");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		field.setAccessible(true);
		Map<String, String> attributeMappings = null;
		try {
			attributeMappings = (Map<String, String>) field.get(fscp);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull(attributeMappings);
		assertTrue(attributeMappings.size() > 0);
		String value = attributeMappings.get("AUTHOR");
		assertEquals("Autor", value);
		value = attributeMappings.get("title");
		assertEquals("Bezeichnung", value);
		value = attributeMappings.get("Last-Author");
		assertEquals("Autor", value);
		value = attributeMappings.get("Author");
		assertEquals("Autor", value);
	}

	public void testDoSynchronization() throws IQserTechnicalException,
			IOException {

		Repository repository = Configuration.getConfiguration()
				.getServiceLocator().getRepository();

		// add dummy contents
		File f = new File(testDataDir + "/testSynch/file2.txt");
		Content c = fscp.getContent(f.getAbsolutePath());
		c.setProvider(fscp.getId());
		repository.addContent(c);
		assertTrue(c.getFulltext().trim()
				.equalsIgnoreCase("testing synchronization initial"));

		Content dummyContent = new Content();
		dummyContent.setContentUrl("deletedContent");
		dummyContent.setProvider(fscp.getId());
		repository.addContent(dummyContent);

		Collection contents = repository.getAllContentItem(-1);
		assertTrue(contents.size() == 2);

		FileWriter fw = new FileWriter(f);
		fw.write("testing synchronization");
		fw.close();

		fscp.doSynchonization();

		contents = repository.getContentByProvider(fscp.getId());
		for (Content content : (Collection<Content>)contents) {
			if (content.getContentUrl().equalsIgnoreCase(c.getContentUrl())){
				assertTrue(content.getFulltext().trim().equals("testing synchronization"));
			}
		}
		assertTrue(contents.size() == 3);

		

		fw = new FileWriter(f);
		fw.write("testing synchronization initial");
		fw.close();
	}

	public void testDoHousekeeping() {
		fscp.doHousekeeping(); // nothing to do
	}

	public void testGetBinaryData() throws IOException {
		String contentUrl;
		contentUrl = testDataDir + "/TxtDataTest.txt";
		testGetBinaryData(contentUrl);

		contentUrl = testDataDir + "/WordDataTest.doc";
		testGetBinaryData(contentUrl);		
	}

	private void testGetBinaryData(String contentUrl) throws IOException,
			FileNotFoundException {
		Content content = fscp.getContent(contentUrl);
		byte[] binaryContent = fscp.getBinaryData(content);
		
		assertNotNull(binaryContent);	
		assertTrue(binaryContent.length > 0);
		
		byte[] expectedByteContent = IOUtils.toByteArray(new FileInputStream(contentUrl));
		
		assertEquals(expectedByteContent.length, binaryContent.length);
		
		for (int i = 0; i < expectedByteContent.length; i++) {
			assertEquals(expectedByteContent[i], binaryContent[i]);
		}
	}	

	public void testGetActionsContent() {

		Collection expectedActions = Arrays.asList(new String[] { "delete",
				"save" });

		Collection actions = fscp.getActions(null);

		assertEquals(expectedActions, actions);
	}

	public void testGetContentString() {

		try {
			testPerformActionStringContent();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		File root = new File(testDataDir);
		StringBuffer sb = new StringBuffer();
		for (File file : root.listFiles()) {
			if (file.isFile()) {

				Content content = fscp.getContent(file.getAbsolutePath());
				assertNotNull(content);
				Collection<Attribute> attributes = content.getAttributes();
				String type = content.getType();
				sb.append(type).append("\r\n").append("\r\n");
				for (Attribute attribute : attributes) {
					String name = attribute.getName();
					String value = attribute.getValue();

					sb.append(name).append("\r\n");
				}
				String fulltext = content.getFulltext();
				sb.append("\r\n").append("\r\n").append("\r\n");
				assertNotNull(fulltext);
			}
		}
		try {
			FileUtils.writeStringToFile(new File(testDataDir
					+ "/testAttributes/attributes.out"), sb.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void testGetContentInputStream() throws FileNotFoundException {

		File root = new File(testDataDir);
		for (File file : root.listFiles()) {
			if (file.isFile()) {
				InputStream inputStream = new FileInputStream(file);
				Content content = fscp.getContent(inputStream);
				assertNotNull(content);
				String fulltext = content.getFulltext();
				assertNotNull(fulltext);
			}
		}

	}

	public void testGetContentUrls() {
		Collection urls = fscp.getContentUrls();
		
		assertTrue(urls.size() == 3);
				
		File file = new File(testDataDir + "/testSynch/file1.txt");
		String contentUrl = file.getAbsolutePath();
		
		assertTrue( urls.contains(contentUrl));
	}

	public void testOnChangeEventEvent() {
		//TODO implement me
	}

	public void testPerformActionStringContent() throws IOException {

		String contentURL, newContentURL;

		File f = new File(testDataDir + "/output");
		f.mkdirs();

		// txt
		contentURL = testDataDir + "/TxtDataTest.txt";
		newContentURL = testDataDir + "/output/testtxt.txt";
		performSaveAction(contentURL, newContentURL);

		// doc
		contentURL = testDataDir + "/WordDataTest.doc";
		newContentURL = testDataDir + "/output/testdoc.doc";
		performSaveAction(contentURL, newContentURL);

		// xls
		contentURL = testDataDir + "/ExcelDataTest.xls";
		newContentURL = testDataDir + "/output/testxls.xls";
		performSaveAction(contentURL, newContentURL);

		// odt
		contentURL = testDataDir + "/ODFDataTest.odt";
		newContentURL = testDataDir + "/output/testodt.odt";
		performSaveAction(contentURL, newContentURL);

		// ppt
		contentURL = testDataDir + "/PowerPointTestData.ppt";
		newContentURL = testDataDir + "/output/testppt.ppt";
		performSaveAction(contentURL, newContentURL);

		// rtf
		contentURL = testDataDir + "/TestDocument.rtf";
		newContentURL = testDataDir + "/output/testrtf.rtf";
		performSaveAction(contentURL, newContentURL);
		

		Repository repository = Configuration.getConfiguration()
				.getServiceLocator().getRepository();
		
		Collection contents = null;
		try {
			contents = repository.getAllContentItem(-1);
		} catch (IQserTechnicalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertTrue(contents.size()==6);
		
		for (Content content : (Collection<Content>)contents) {
			performDeleteAction(content);
		}
		try {
			contents = repository.getAllContentItem(-1);
		} catch (IQserTechnicalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertTrue(contents.size()==0);
		
		//check if folder is empty
		File outputFolder = new File(testDataDir + "/output");
		assertTrue(outputFolder.list().length == 0);

	}

	public void testPerformActionStringContentPDF() throws IOException, IQserTechnicalException {

		String contentURL, newContentURL;

		File f = new File(testDataDir + "/output");
		f.mkdirs();

		contentURL = testDataDir + "/ZimbraCommunity.pdf";
		newContentURL = testDataDir + "/output/testpdf.pdf";
		performSaveAction(contentURL, newContentURL);
						
		Repository repository = Configuration.getConfiguration().getServiceLocator().getRepository();

		Collection contents = repository.getAllContentItem(-1);		
		assertTrue(contents.size()==1);
		
		for (Content content : (Collection<Content>)contents) {
			performDeleteAction(content);
		}
		contents = repository.getAllContentItem(-1);		
		assertTrue(contents.size()==0);

	}

	private void performSaveAction(String contentURL, String newContentURL)
			throws IOException {

		Content content = fscp.getContent(contentURL);
		assertNotNull(content);
		String fulltext = content.getFulltext();
		assertNotNull(fulltext);

		// save it in other location
		content.setContentUrl(newContentURL);
		fscp.performAction("save", content);

		// read new content
		Content newContent = fscp.getContent(newContentURL);
		String newFulltext = newContent.getFulltext();

		assertEquals(fulltext, newFulltext);
	}

	private void performDeleteAction(Content content){
		fscp.performAction("delete", content);
	}
	
}
