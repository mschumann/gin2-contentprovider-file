package net.sf.iqser.plugin.file;

import java.io.File;
import java.util.Collection;
import java.util.Properties;

import junit.framework.TestCase;
import net.sf.iqser.plugin.file.mock.MockAnalyzerTaskStarter;
import net.sf.iqser.plugin.file.mock.MockRepository;
import net.sf.iqser.plugin.file.mock.TestServiceLocator;

import org.apache.log4j.PropertyConfigurator;

import com.iqser.core.config.Configuration;
import com.iqser.core.exception.IQserException;
import com.iqser.core.model.Attribute;
import com.iqser.core.model.Content;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class CmisContentProviderTestIntegration extends TestCase {
				
	CmisContentProvider ccp;
	

	private com.iqser.core.repository.Repository repo;
		
	@Override
	protected void setUp() throws Exception {
		
		PropertyConfigurator.configure("src/test/resources/log4j.properties");

		ccp = new CmisContentProvider();
		
		Properties initParams = new Properties();
	
		// User credentials
		initParams.put("USERNAME","robert.baban");
		initParams.put("PASSWORD","#EDCXSW@1qaz");			
		String sharepointWS = "http://win2008:8778/_vti_bin/cmissoapwsdl.aspx?wsdl";
		
		// CMIS WebService Urls
		initParams.put("WEBSERVICES_REPOSITORY_SERVICE",sharepointWS); 
		initParams.put("WEBSERVICES_ACL_SERVICE",sharepointWS);
		initParams.put("WEBSERVICES_DISCOVERY_SERVICE",sharepointWS);
		initParams.put("WEBSERVICES_MULTIFILING_SERVICE",sharepointWS);
		initParams.put("WEBSERVICES_NAVIGATION_SERVICE",sharepointWS);
		initParams.put("WEBSERVICES_OBJECT_SERVICE",sharepointWS);
		initParams.put("WEBSERVICES_POLICY_SERVICE",sharepointWS);
		initParams.put("WEBSERVICES_RELATIONSHIP_SERVICE",sharepointWS);
		initParams.put("WEBSERVICES_REPOSITORY_SERVICE",sharepointWS);
		initParams.put("WEBSERVICES_VERSIONING_SERVICE",sharepointWS);
		//authentication - Standard or NTLM
		initParams.put("AUTHENTICATION_PROVIDER_CLASS","NTLM");
		//repository
		initParams.put("REPOSITORY","[Shared Documents]");
		
		ccp.setInitParams(initParams);
		ccp.setType("CMISObject");
		ccp.setId("net.sf.iqser.plugin.file");
		ccp.setInitParams(initParams);
		ccp.init();	
		
		Configuration.configure(new File("src/test/resources/iqser-config.xml"));
		
		TestServiceLocator sl = (TestServiceLocator) Configuration.getConfiguration().getServiceLocator();
		repo = new MockRepository();
		repo.init();

		sl.setRepository(repo);
		sl.setAnalyzerTaskStarter(new MockAnalyzerTaskStarter());
		
	}

	@Override
	protected void tearDown() throws Exception {				
		repo = null;		
		
		super.tearDown();
	}

	public void testDoSynchronization() throws IQserException {

		Collection<Content> contentList = (Collection<Content>)repo.getContentByProvider(ccp.getId(), true);
		assertTrue(contentList.size()==0);
		
		ccp.doSynchonization();
		
		//after test the
		contentList = (Collection<Content>)repo.getContentByProvider(ccp.getId(), true);
		assertTrue(contentList.size()>0);
				
		System.out.println("NR OF CONTENTS="+contentList.size());
		
	}

	public void testDoHousekeeping() throws IQserException {
		//add some content that does not exists in server
		Content dummyContent = new Content();
		dummyContent.setType(CmisContentProvider.CMIS_DOCUMENT_TYPE);
		dummyContent.setProvider(ccp.getId());
		dummyContent.setContentUrl("http://cmis/Shared Documents/cmis:document#0-1024");
		dummyContent.getAttributes().add(new Attribute("hasContentStream", "true", Attribute.ATTRIBUTE_TYPE_BOOLEAN));
		dummyContent.getAttributes().add(new Attribute("objectId", "0-1024", Attribute.ATTRIBUTE_TYPE_TEXT));
		repo.addContent(dummyContent);
		
		Content existingContent = new Content();
		existingContent.setType(CmisContentProvider.CMIS_DOCUMENT_TYPE);
		existingContent.setProvider(ccp.getId());
		existingContent.setContentUrl("http://cmis/Shared Documents/cmis:document#1-1024");
		existingContent.getAttributes().add(new Attribute("objectId", "1-1024", Attribute.ATTRIBUTE_TYPE_TEXT));
		repo.addContent(existingContent);
		
		Collection<Content> contentList = (Collection<Content>)repo.getContentByProvider(ccp.getId(), true);
		assertTrue(contentList.size()>0);
				
		ccp.doHousekeeping();
		
		//after test the - dummy content should be deleted
		contentList = (Collection<Content>)repo.getContentByProvider(ccp.getId(), true);
		assertTrue(contentList.size()==1);		
		
		Content c = contentList.iterator().next();
		assertEquals(existingContent.getContentUrl(), c.getContentUrl());		
	}

	public void testGetBinaryData() {
		String contentUrl = "http://cmis/Shared Documents/cmis:document#1-1024";
		Content content = new Content();
		content.setType("CMIS_DOCUMENT");
		content.setContentUrl(contentUrl);
		content.getAttributes().add(new Attribute("hasContentStream", "true", Attribute.ATTRIBUTE_TYPE_BOOLEAN));
		content.getAttributes().add(new Attribute("objectId", "1-1024", Attribute.ATTRIBUTE_TYPE_TEXT));
		
		byte[] binaryData = ccp.getBinaryData(content);
		assertNotNull(binaryData);
		assertTrue(binaryData.length > 100);
		
	}

	public void testContentExistsOnSource() {
		Content content = null;
		boolean contentExistsOnSource = false;
		
		content = new Content();
		content.setType("CMIS_DOCUMENT");
		content.setContentUrl("http://cmis/Shared Documents/cmis:document#1-1024");
		
		contentExistsOnSource = ccp.contentExistsOnSource(content);		
		assertTrue(contentExistsOnSource);
		
		//not exists
		content.setContentUrl("http://cmis/Shared Documents/cmis:document#1-5");
		contentExistsOnSource = ccp.contentExistsOnSource(content);		
		assertFalse(contentExistsOnSource);		
	}

	public void testGetActionsContent() {		
		
		Collection actions = null;
		
		Content content = new Content();
		
		actions = ccp.getActions(content);
		assertTrue(actions.size() == 0);
		
		content.setType(CmisContentProvider.CMIS_DOCUMENT_TYPE);
		actions = ccp.getActions(content);
		assertTrue(actions.size() == 4);
		
		assertTrue(actions.contains(CmisContentProvider.ACTION_CHECK_OUT));
		assertTrue(actions.contains(CmisContentProvider.ACTION_CHECK_IN));
		assertTrue(actions.contains(CmisContentProvider.ACTION_DELETE));
		assertTrue(actions.contains(CmisContentProvider.ACTION_UPDATE));
		
		content.setType(CmisContentProvider.CMIS_FOLDER_TYPE);
		actions = ccp.getActions(content);
		assertTrue(actions.size() == 2);
		
		assertTrue(actions.contains(CmisContentProvider.ACTION_DELETE));
		assertTrue(actions.contains(CmisContentProvider.ACTION_UPDATE));

	}

	public void testGetContent() {
		
		//TODO not working for PDF
		//String contentUrl = "http://cmis/Shared Documents/cmis:document#5-512";
		//TEXT document
		String contentUrl = "http://cmis/Shared Documents/cmis:document#3-1024";
		Content content = ccp.getContent(contentUrl);
		
		assertNotNull(content);
		assertNotNull(content.getModificationDate());
		assertEquals("CMIS_DOCUMENT", content.getType());	
		assertEquals(ccp.getId(), content.getProvider());
		
		assertTrue(content.getAttributes().size() > 0);
		
		assertEquals("true",content.getAttributeByName("hasContentStream").getValue());
		
		assertNotNull(content.getAttributeByName("cmis:objectId").getValue());
		assertNotNull(content.getAttributeByName("cmis:name").getValue());
		assertNotNull(content.getAttributeByName("cmis:objectId").getValue());
		//file parser properties
		assertNotNull(content.getAttributeByName("FILENAME").getValue());				
		assertNotNull(content.getAttributeByName("TITLE").getValue());
	}	
	
	public void testGetContentPDF() {
		
		//TODO not working for PDF
		String contentUrl = "http://cmis/Shared Documents/cmis:document#5-512";
		Content content = ccp.getContent(contentUrl);
		
		assertNotNull(content);
		assertNotNull(content.getModificationDate());
		assertEquals("CMIS_DOCUMENT", content.getType());	
		assertEquals(ccp.getId(), content.getProvider());
		
		assertTrue(content.getAttributes().size() > 0);
		
		assertEquals("true",content.getAttributeByName("hasContentStream").getValue());
		
		assertNotNull(content.getAttributeByName("cmis:objectId").getValue());
		assertNotNull(content.getAttributeByName("cmis:name").getValue());
		assertNotNull(content.getAttributeByName("cmis:objectId").getValue());
		//file parser properties
		assertNotNull(content.getAttributeByName("FILENAME").getValue());				
		assertNotNull(content.getAttributeByName("TITLE").getValue());
	}	
	
	public void testGetContentWithCustomProperties() {
		
		//TEXT document
		String contentUrl = "http://cmis/Shared Documents/cmis:document#3-5120";
		Content content = ccp.getContent(contentUrl);
		
		assertNotNull(content);
		assertNotNull(content.getModificationDate());
		assertEquals("CMIS_DOCUMENT", content.getType());	
		assertEquals(ccp.getId(), content.getProvider());
		
		assertTrue(content.getAttributes().size() > 0);
				
		assertEquals("true",content.getAttributeByName("hasContentStream").getValue());
		
		assertNotNull(content.getAttributeByName("cmis:objectId").getValue());
		assertNotNull(content.getAttributeByName("cmis:name").getValue());
		assertNotNull(content.getAttributeByName("cmis:objectId").getValue());
		//file parser properties
		assertNotNull(content.getAttributeByName("FILENAME").getValue());				
		assertNotNull(content.getAttributeByName("TITLE").getValue());
		//custom CMIS properties
		assertNotNull(content.getAttributeByName("Title").getValue());				
		assertNotNull(content.getAttributeByName("MyData").getValue());
		assertNotNull(content.getAttributeByName("MyData2").getValue());

	}		
	
}
