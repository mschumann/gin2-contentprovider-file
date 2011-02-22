package net.sf.iqser.plugin.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import junit.framework.TestCase;
import net.sf.iqser.plugin.filesystem.test.MockAnalyzerTaskStarter;
import net.sf.iqser.plugin.filesystem.test.MockRepository;
import net.sf.iqser.plugin.filesystem.test.TestServiceLocator;

import org.apache.log4j.PropertyConfigurator;

import com.iqser.core.config.Configuration;
import com.iqser.core.exception.IQserTechnicalException;
import com.iqser.core.model.Content;
import com.iqser.core.repository.Repository;

public class FilesystemContentProviderTest extends TestCase {
	
	FilesystemContentProvider fscp;
	String testDataDir;

    @Override
	protected void setUp() throws Exception {
    	
    	testDataDir = System.getProperty("testdata.dir", "testdata");
    	
    	PropertyConfigurator.configure("src/test/resources/log4j.properties");
			
		super.setUp();
		
		Properties initParams = new Properties();		
		initParams.setProperty("folder", testDataDir + "/testSynch");
		initParams.setProperty("filter-pattern", "txt");
		initParams.setProperty("filter-folder-include", testDataDir + "/testSynch");
		initParams.setProperty("filter-folder-exclude", testDataDir + "/testSynch/exclude/");
		
		fscp = new FilesystemContentProvider();
		fscp.setInitParams(initParams);
		fscp.setType("HTML Page");
		fscp.setId("com.iqser.plugin.web.base");
		fscp.setInitParams(initParams);
		fscp.init();		
				
		Configuration.configure(new File("src/test/resources/iqser-config.xml"));
		
		TestServiceLocator sl = (TestServiceLocator)Configuration.getConfiguration().getServiceLocator();
		MockRepository rep = new MockRepository();
		rep.init();
		
		sl.setRepository(rep);
		sl.setAnalyzerTaskStarter(new MockAnalyzerTaskStarter());				
	}

	@Override
	protected void tearDown() throws Exception {
		fscp.destroy();
		super.tearDown();
	}

	public void testDestroy() {
		fscp.destroy(); //nothing to do
    }

    public void testInit() {
    	fscp.init(); //nothing to do
    }

    public void testDoSynchronization() throws IQserTechnicalException {	    	
    	
    	Repository repository = Configuration.getConfiguration().getServiceLocator().getRepository();
    	
    	//add dummy contents 
    	File f = new File(testDataDir + "/testSynch/file2.txt");
    	Content c = fscp.getContent(f.getAbsolutePath());
    	c.setProvider(fscp.getId());
    	repository.addContent(c);
    	
    	Content dummyContent = new Content();
    	dummyContent.setContentUrl("deletedContent");
    	dummyContent.setProvider(fscp.getId());
    	repository.addContent(dummyContent);
    	
    	Collection contents = repository.getAllContentItem(-1);
    	assertTrue(contents.size() == 2);
    	
    	fscp.doSynchonization();    	
    	    	    	    	 
    	contents = repository.getContentByProvider(fscp.getId());    	
    	assertTrue(contents.size() == 3);
    	    	
    }       

    
    public void testDoHousekeeping() {
    	fscp.doHousekeeping(); //nothing to do
    }

    public void testGetBinaryData() {
	fail("Not yet implemented");
    }

    public void testGetFile() {
	fail("Not yet implemented");	
    }

    public void testGetActionsContent() {
    	
    	Collection expectedActions = Arrays.asList(new String[] { "delete", "save" });
    	
    	Collection actions = fscp.getActions(null);
    	
    	assertEquals(expectedActions, actions);
    }

    public void testGetContentString() {
    	
    	File root = new File(testDataDir);
    	for (File file : root.listFiles()) {
    		if (file.isFile()){
	    		Content content = fscp.getContent(file.getAbsolutePath());
	    		assertNotNull(content);
	    		String fulltext = content.getFulltext();
	    		assertNotNull(fulltext);
    		}
		}		
    }

    public void testGetContentInputStream() throws FileNotFoundException {
    	
    	File root = new File(testDataDir);
    	for (File file : root.listFiles()) {
    		if (file.isFile()){
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
		for (Object url : urls) {
			System.out.println(url);
		}
    }

    public void testOnChangeEventEvent() {
    	fail("Not yet implemented");
    }

    public void testPerformActionStringContent() throws IOException {
    	
    	String contentURL, newContentURL;
    	
    	File f = new File(testDataDir + "/output");
    	f.mkdirs();
    	
    	//txt
    	contentURL = testDataDir + "/TxtDataTest.txt";
    	newContentURL = testDataDir + "/output/testtxt.txt";
    	performSaveAction(contentURL, newContentURL);
    	
    	//doc
    	contentURL = testDataDir + "/WordDataTest.doc";
    	newContentURL = testDataDir + "/output/testdoc.doc";
    	performSaveAction(contentURL, newContentURL);
				
		//xls
    	contentURL = testDataDir + "/ExcelDataTest.xls";
    	newContentURL = testDataDir + "/output/testxls.xls"; 
    	performSaveAction(contentURL, newContentURL);
		
		//odt		
    	contentURL = testDataDir + "/ODFDataTest.odt";
    	newContentURL = testDataDir + "/output/testodt.odt";
    	performSaveAction(contentURL, newContentURL);
		
		//ppt		
    	contentURL = testDataDir + "/PowerPointTestData.ppt";
    	newContentURL = testDataDir + "/output/testppt.ppt";
    	performSaveAction(contentURL, newContentURL);
		
		//rtf		
    	contentURL = testDataDir + "/TestDocument.rtf";
    	newContentURL = testDataDir + "/output/testrtf.rtf";
    	performSaveAction(contentURL, newContentURL);
		
		
    	
    }
    
    public void testPerformActionStringContentPDF() throws IOException {

    	String contentURL, newContentURL;
    	
    	File f = new File(testDataDir + "/output");
    	f.mkdirs();
    	
    	contentURL = testDataDir + "/ZimbraCommunity.pdf";
    	newContentURL = testDataDir + "/output/testpdf.pdf";
    	performSaveAction(contentURL, newContentURL);
    	    	
    }
    
    private void performSaveAction(String contentURL, String newContentURL) throws IOException{
    	
		Content content = fscp.getContent(contentURL);
		assertNotNull(content);
		String fulltext = content.getFulltext();
		assertNotNull(fulltext);
		
		//save it in other location
		content.setContentUrl(newContentURL);
		fscp.performAction("save", content);
    	
		//read new content    			
		Content newContent = fscp.getContent(newContentURL);
		String newFulltext = newContent.getFulltext();
		
		assertEquals(fulltext, newFulltext);
    }        

}
