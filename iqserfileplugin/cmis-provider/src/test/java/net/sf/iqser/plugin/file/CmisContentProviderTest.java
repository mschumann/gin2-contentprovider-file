package net.sf.iqser.plugin.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;
import net.sf.iqser.plugin.file.mock.MockAnalyzerTaskStarter;
import net.sf.iqser.plugin.file.mock.MockRepository;
import net.sf.iqser.plugin.file.mock.TestServiceLocator;
import net.sf.iqser.plugin.file.mock.cmis.MockDocument;
import net.sf.iqser.plugin.file.mock.cmis.MockFolder;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.PropertyImpl;
import org.apache.chemistry.opencmis.commons.enums.PropertyType;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyStringDefinitionImpl;
import org.apache.log4j.PropertyConfigurator;
import org.easymock.EasyMock;

import com.iqser.core.config.Configuration;
import com.iqser.core.exception.IQserException;
import com.iqser.core.model.Attribute;
import com.iqser.core.model.Content;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class CmisContentProviderTest extends TestCase {
				
	CmisContentProvider ccp;
	MockRepository repo;
		
	// create mock objects		
	Repository mockCmisRepo;		
	Session mockCmisSession;	
		
	@Override
	protected void setUp() throws Exception {
		
		PropertyConfigurator.configure("src/test/resources/log4j.properties");

		mockCmisRepo = EasyMock.createMock(Repository.class);		
		mockCmisSession = EasyMock.createMock(Session.class);	
				
		ccp = new CmisContentProvider();			
			
		ccp.setType("CMISObject");
		ccp.setId("net.sf.iqser.plugin.file");
		ccp.getRepositories().add(mockCmisRepo);
		
		Configuration.configure(new File("src/test/resources/iqser-config.xml"));
		
		TestServiceLocator sl = (TestServiceLocator) Configuration.getConfiguration().getServiceLocator();
		repo = new MockRepository();
		repo.init();

		sl.setRepository(repo);
		sl.setAnalyzerTaskStarter(new MockAnalyzerTaskStarter());
		
	}

	@Override
	protected void tearDown() throws Exception {						
		super.tearDown();
	}

	public void testDoSynchronization() throws IQserException {

		Collection<Content> contentList = (Collection<Content>)repo.getContentByProvider(ccp.getId(), true);
		assertTrue(contentList.size()==0);
		
		MockFolder dummyRootFolder = new MockFolder();
		dummyRootFolder.getProperties().add(helperCreateProperty("cmis:objectId","-1"));
		dummyRootFolder.getProperties().add(helperCreateProperty("cmis:name","RootFolder"));
		
		MockDocument expectedDoc1_v1 = new MockDocument();
		expectedDoc1_v1.getProperties().add(helperCreateProperty("cmis:objectId","1-512"));
		expectedDoc1_v1.getProperties().add(helperCreateProperty("cmis:name","Doc1_v1"));
		expectedDoc1_v1.setContentStream(helperCreateContentStream(), true);		
		expectedDoc1_v1.dummyAddParent(dummyRootFolder);
		
		MockDocument expectedDoc1_v2 = new MockDocument();
		expectedDoc1_v2.getProperties().add(helperCreateProperty("cmis:objectId","1-1024"));
		expectedDoc1_v2.getProperties().add(helperCreateProperty("cmis:name","Doc1_v2"));
		expectedDoc1_v2.setContentStream(helperCreateContentStream(), true);
		expectedDoc1_v2.dummyAddVersion(expectedDoc1_v1);
		expectedDoc1_v2.dummyAddParent(dummyRootFolder);
		dummyRootFolder.dummyAddCmisChild(expectedDoc1_v2);

		MockFolder dummyFolder = new MockFolder();
		dummyFolder.getProperties().add(helperCreateProperty("cmis:objectId","10"));
		dummyFolder.getProperties().add(helperCreateProperty("cmis:name","Folder"));
		dummyFolder.dummyAddParent(dummyRootFolder);
		dummyRootFolder.dummyAddCmisChild(dummyFolder);
		
		MockDocument expectedDoc3 = new MockDocument();
		expectedDoc3.getProperties().add(helperCreateProperty("cmis:objectId","3-512"));
		expectedDoc3.getProperties().add(helperCreateProperty("cmis:name","Doc3"));
		expectedDoc3.setContentStream(helperCreateContentStream(), true);
		expectedDoc3.dummyAddParent(dummyFolder);
		dummyFolder.dummyAddCmisChild(expectedDoc3);
		
		//expected behavior		
		EasyMock.expect(mockCmisRepo.getName()).andReturn("Shared Documents").anyTimes();
		EasyMock.expect(mockCmisRepo.createSession()).andReturn(mockCmisSession);			
		EasyMock.expect(mockCmisSession.getRootFolder()).andReturn(dummyRootFolder);
		
		//register behavior
		EasyMock.replay(mockCmisRepo, mockCmisSession);
		
		ccp.doSynchonization();
		
		//verify
		EasyMock.verify();
		
		//after test the
		contentList = (Collection<Content>)repo.getContentByProvider(ccp.getId(), true);
		assertTrue(contentList.size()>0);
						
		for (Content content : contentList) {
			assertNotNull(content.getAttributeByName("cmis:objectId").getValue());
			assertNotNull(content.getAttributeByName("cmis:name").getValue());
			if (CmisContentProvider.CMIS_DOCUMENT_TYPE.equalsIgnoreCase(content.getType())){
				//	file parser properties
				assertNotNull(content.getAttributeByName("FILENAME").getValue());				
				assertNotNull(content.getAttributeByName("TITLE").getValue());
			}
		}
		
	}
	
	public void testDoSynchronizationWithUpdate1() throws IQserException {

		String contentUrl = "http://cmis/Shared Documents/cmis:document#1-512";
		Content existingContent = helperCreateDummyDocumentContentFromUrl(contentUrl);
		existingContent.addAttribute(new Attribute("cmis:objectId","1-512",Attribute.ATTRIBUTE_TYPE_TEXT));
		existingContent.addAttribute(new Attribute("cmis:name","origName",Attribute.ATTRIBUTE_TYPE_TEXT));
		existingContent.addAttribute(new Attribute("FILENAME","origName.txt",Attribute.ATTRIBUTE_TYPE_TEXT));
		existingContent.addAttribute(new Attribute("TITLE","origTitle",Attribute.ATTRIBUTE_TYPE_TEXT));
		repo.addContent(existingContent);	
		
		Collection<Content> contentList = (Collection<Content>)repo.getContentByProvider(ccp.getId(), true);
		assertTrue(contentList.size()==1);
		
		MockFolder dummyRootFolder = new MockFolder();
		dummyRootFolder.getProperties().add(helperCreateProperty("cmis:objectId","-1"));
		dummyRootFolder.getProperties().add(helperCreateProperty("cmis:name","RootFolder"));
		
		MockDocument expectedDoc1_v1 = new MockDocument();
		expectedDoc1_v1.getProperties().add(helperCreateProperty("cmis:objectId","1-512"));
		expectedDoc1_v1.getProperties().add(helperCreateProperty("cmis:name","Doc1_v1"));
		expectedDoc1_v1.setContentStream(helperCreateContentStream(), true);		
		expectedDoc1_v1.dummyAddParent(dummyRootFolder);
		
		MockDocument expectedDoc1_v2 = new MockDocument();
		expectedDoc1_v2.getProperties().add(helperCreateProperty("cmis:objectId","1-1024"));
		expectedDoc1_v2.getProperties().add(helperCreateProperty("cmis:name","Doc1_v2"));
		expectedDoc1_v2.setContentStream(helperCreateContentStream(), true);
		expectedDoc1_v2.dummyAddVersion(expectedDoc1_v1);
		expectedDoc1_v2.dummyAddParent(dummyRootFolder);
		dummyRootFolder.dummyAddCmisChild(expectedDoc1_v2);		
		
		//expected behavior		
		EasyMock.expect(mockCmisRepo.getName()).andReturn("Shared Documents").anyTimes();
		EasyMock.expect(mockCmisRepo.createSession()).andReturn(mockCmisSession);			
		EasyMock.expect(mockCmisSession.getRootFolder()).andReturn(dummyRootFolder);
		
		//register behavior
		EasyMock.replay(mockCmisRepo, mockCmisSession);
		
		ccp.doSynchonization();
		
		//verify
		EasyMock.verify();
		
		//after test the
		contentList = (Collection<Content>)repo.getContentByProvider(ccp.getId(), true);
		assertTrue(contentList.size()>0);
						
		for (Content content : contentList) {
			assertNotNull(content.getAttributeByName("cmis:objectId").getValue());
			assertNotNull(content.getAttributeByName("cmis:name").getValue());
			System.out.println(content.getAttributeByName("cmis:name").getValue());
			if (CmisContentProvider.CMIS_DOCUMENT_TYPE.equalsIgnoreCase(content.getType())){
				//	file parser properties
				assertNotNull(content.getAttributeByName("FILENAME").getValue());				
				assertNotNull(content.getAttributeByName("TITLE").getValue());
			}
		}
		for (Content content : contentList) {
			if (existingContent.getContentUrl().equals(content.getContentUrl())){
				assertEquals("Doc1_v1",content.getAttributeByName("cmis:name").getValue());
			}
		}
		
	}
	
	public void testDoSynchronizationWithUpdate2() throws IQserException {

		String contentUrl = "http://cmis/Shared Documents/cmis:document#1-512";
		Content existingContent = helperCreateDummyDocumentContentFromUrl(contentUrl);
		existingContent.addAttribute(new Attribute("cmis:objectId","1-512",Attribute.ATTRIBUTE_TYPE_TEXT));
		existingContent.addAttribute(new Attribute("cmis:name","origName",Attribute.ATTRIBUTE_TYPE_TEXT));
		existingContent.addAttribute(new Attribute("FILENAME","origName.txt",Attribute.ATTRIBUTE_TYPE_TEXT));
		existingContent.addAttribute(new Attribute("TITLE","origTitle",Attribute.ATTRIBUTE_TYPE_TEXT));
		repo.addContent(existingContent);	

		
		Collection<Content> contentList = (Collection<Content>)repo.getContentByProvider(ccp.getId(), true);
		assertTrue(contentList.size()==1);
		
		MockFolder dummyRootFolder = new MockFolder();
		dummyRootFolder.getProperties().add(helperCreateProperty("cmis:objectId","-1"));
		dummyRootFolder.getProperties().add(helperCreateProperty("cmis:name","RootFolder"));
		
		MockDocument expectedDoc1_v1 = new MockDocument();
		expectedDoc1_v1.getProperties().add(helperCreateProperty("cmis:objectId","1-512"));
		expectedDoc1_v1.getProperties().add(helperCreateProperty("cmis:name","Doc1_v1"));
		expectedDoc1_v1.setContentStream(helperCreateContentStream(), true);		
		expectedDoc1_v1.dummyAddParent(dummyRootFolder);		
		dummyRootFolder.dummyAddCmisChild(expectedDoc1_v1);		
		
		//expected behavior		
		EasyMock.expect(mockCmisRepo.getName()).andReturn("Shared Documents").anyTimes();
		EasyMock.expect(mockCmisRepo.createSession()).andReturn(mockCmisSession);			
		EasyMock.expect(mockCmisSession.getRootFolder()).andReturn(dummyRootFolder);
		
		//register behavior
		EasyMock.replay(mockCmisRepo, mockCmisSession);
		
		ccp.doSynchonization();
		
		//verify
		EasyMock.verify();
		
		//after test the
		contentList = (Collection<Content>)repo.getContentByProvider(ccp.getId(), true);
		assertTrue(contentList.size()>0);
						
		for (Content content : contentList) {
			assertNotNull(content.getAttributeByName("cmis:objectId").getValue());
			assertNotNull(content.getAttributeByName("cmis:name").getValue());
			System.out.println(content.getAttributeByName("cmis:name").getValue());
			if (CmisContentProvider.CMIS_DOCUMENT_TYPE.equalsIgnoreCase(content.getType())){
				//	file parser properties
				assertNotNull(content.getAttributeByName("FILENAME").getValue());				
				assertNotNull(content.getAttributeByName("TITLE").getValue());
			}
		}
		for (Content content : contentList) {
			if (existingContent.getContentUrl().equals(content.getContentUrl())){
				assertEquals("Doc1_v1",content.getAttributeByName("cmis:name").getValue());
			}
		}
		
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
				
		//expected behavior		
		EasyMock.expect(mockCmisRepo.getName()).andReturn("Shared Documents").anyTimes();
		EasyMock.expect(mockCmisRepo.createSession()).andReturn(mockCmisSession);
		EasyMock.expect(mockCmisSession.getObject("0-1024")).andReturn(null);
		EasyMock.expect(mockCmisRepo.createSession()).andReturn(mockCmisSession);
		EasyMock.expect(mockCmisSession.getObject("1-1024")).andReturn(new MockDocument());
		
		//register behavior
		EasyMock.replay(mockCmisRepo, mockCmisSession);
		
		ccp.doHousekeeping();
		
		//verify
		EasyMock.verify();
		
		//after test the - dummy content should be deleted, existingContent should not be deleted
		contentList = (Collection<Content>)repo.getContentByProvider(ccp.getId(), true);
		assertTrue(contentList.size()==1);		
		
		Content c = contentList.iterator().next();
		assertEquals(existingContent.getContentUrl(), c.getContentUrl());		
	}

	public void testGetBinaryData() {
				
		//parameter
		Content content = new Content();
		content.setType("CMIS_DOCUMENT");
		content.setContentUrl("http://cmis/Shared Documents/cmis:document#1-1024");
		content.getAttributes().add(new Attribute("hasContentStream", "true", Attribute.ATTRIBUTE_TYPE_BOOLEAN));
		content.getAttributes().add(new Attribute("objectId", "1-1024", Attribute.ATTRIBUTE_TYPE_TEXT));
		
		//expected doc				
		Document expectedDoc = new MockDocument(); 
		ContentStreamImpl contentStream = helperCreateContentStream();
		expectedDoc.setContentStream(contentStream, true);					
		
		//expected behavior		
		EasyMock.expect(mockCmisRepo.getName()).andReturn("Shared Documents").anyTimes();
		EasyMock.expect(mockCmisRepo.createSession()).andReturn(mockCmisSession);
		EasyMock.expect(mockCmisSession.getObject("1-1024")).andReturn(expectedDoc);		
		
		//register behavior
		EasyMock.replay(mockCmisRepo, mockCmisSession);
		
		byte[] binaryData = ccp.getBinaryData(content);
		
		assertNotNull(binaryData);
		assertTrue(binaryData.length > 100);
		
		//verify
		EasyMock.verify();
		
	}
	
	public void testGetBinaryDataDocumentNoStream() {
		Content content = new Content();
		content.setType("CMIS_DOCUMENT");
		content.setContentUrl("http://cmis/Shared Documents/cmis:document#1-1024");
		content.getAttributes().add(new Attribute("hasContentStream", "false", Attribute.ATTRIBUTE_TYPE_BOOLEAN));
		content.getAttributes().add(new Attribute("objectId", "1-1024", Attribute.ATTRIBUTE_TYPE_TEXT));
		
		byte[] binaryData = ccp.getBinaryData(content);
		
		assertNull(binaryData);
	}
	
	public void testGetBinaryDataFolder() {
		Content content = new Content();
		content.setType(CmisContentProvider.CMIS_FOLDER_TYPE);
		
		byte[] binaryData = ccp.getBinaryData(content);
		
		assertNull(binaryData);
	}
	
	public void testGetBinaryDataInputStream() {
		InputStream in = null;
		try{
			ccp.getContent(in);
			fail();
		}catch(RuntimeException re){
			//success
		}
	}
	

	@SuppressWarnings("deprecation")
	public void testGetContentUrls() {
		Collection urls = ccp.getContentUrls();
		assertTrue(urls.isEmpty());
	}

	
	@SuppressWarnings("deprecation")
	public void testOnChangeEvent() {
		ccp.onChangeEvent(null);
	}

	public void testContentExistsOnSource() {
		Content content = null;
		boolean contentExistsOnSource = false;
		
		content = new Content();
		content.setType("CMIS_DOCUMENT");
		content.setContentUrl("http://cmis/Shared Documents/cmis:document#1-1024");
		
		//expected doc				
		Document expectedDoc = new MockDocument(); 
		ContentStreamImpl contentStream = helperCreateContentStream();
		expectedDoc.setContentStream(contentStream, true);					
		
		//expected behavior		
		EasyMock.expect(mockCmisRepo.getName()).andReturn("Shared Documents").anyTimes();
		EasyMock.expect(mockCmisRepo.createSession()).andReturn(mockCmisSession);
		EasyMock.expect(mockCmisSession.getObject("1-1024")).andReturn(expectedDoc);	
		//register behavior
		EasyMock.replay(mockCmisRepo, mockCmisSession);
		
		contentExistsOnSource = ccp.contentExistsOnSource(content);		
		assertTrue(contentExistsOnSource);
		
		EasyMock.verify();
	}
	
	public void testContentNotExistsOnSource() {		

		Content content = new Content();
		content.setType("CMIS_DOCUMENT");
		content.setContentUrl("http://cmis/Shared Documents/cmis:document#1-5");
		
		//expected doc				
		Document expectedDoc = null; 
		
		//expected behavior		
		EasyMock.expect(mockCmisRepo.getName()).andReturn("Shared Documents").anyTimes();
		EasyMock.expect(mockCmisRepo.createSession()).andReturn(mockCmisSession);
		EasyMock.expect(mockCmisSession.getObject("1-5")).andReturn(expectedDoc);	
		//register behavior
		EasyMock.replay(mockCmisRepo, mockCmisSession);
		
		boolean contentExistsOnSource = ccp.contentExistsOnSource(content);		
		assertFalse(contentExistsOnSource);
		
		EasyMock.verify();		
				
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
				
		String contentUrl = "http://cmis/Shared Documents/cmis:document#3-1024";

		//expected doc				
		MockDocument expectedDoc = new MockDocument();				
		expectedDoc.getProperties().add(helperCreateProperty("cmis:objectId","3-1024"));
		expectedDoc.getProperties().add(helperCreateProperty("cmis:name","MyFile.txt"));
		expectedDoc.setContentStream(helperCreateContentStream(), true);		
		
		//expected behavior		
		EasyMock.expect(mockCmisRepo.getName()).andReturn("Shared Documents").anyTimes();
		EasyMock.expect(mockCmisRepo.createSession()).andReturn(mockCmisSession);
		EasyMock.expect(mockCmisSession.getObject("3-1024")).andReturn(expectedDoc);		
		
		//register behavior
		EasyMock.replay(mockCmisRepo, mockCmisSession);

		Content content = ccp.getContent(contentUrl);
		
		//verify
		EasyMock.verify();
						
		assertNotNull(content);
		assertNotNull(content.getModificationDate());
		assertEquals("CMIS_DOCUMENT", content.getType());	
		assertEquals(ccp.getId(), content.getProvider());
		
		assertTrue(content.getAttributes().size() > 0);
		
		assertEquals("true",content.getAttributeByName("hasContentStream").getValue());
		
		assertNotNull(content.getAttributeByName("cmis:objectId").getValue());
		assertNotNull(content.getAttributeByName("cmis:name").getValue());
		//file parser properties
		assertNotNull(content.getAttributeByName("FILENAME").getValue());				
		assertNotNull(content.getAttributeByName("TITLE").getValue());
	}	
	
	public void testGetContentFolder() {
		
		String contentUrl = "http://cmis/Shared Documents/cmis:folder#100";

		//expected doc				
		MockFolder expectedFolder = new MockFolder();				
		expectedFolder.getProperties().add(helperCreateProperty("cmis:objectId","100"));
		expectedFolder.getProperties().add(helperCreateProperty("cmis:name","MyFolder"));
		expectedFolder.getProperties().add(helperCreateProperty("boolProp","true",PropertyType.BOOLEAN));
		expectedFolder.getProperties().add(helperCreateProperty("dateProp","11/11/2011",PropertyType.DATETIME));
		expectedFolder.getProperties().add(helperCreateProperty("intProp","11",PropertyType.INTEGER));
		expectedFolder.getProperties().add(helperCreateProperty("decimalProp","11.0",PropertyType.DECIMAL));
		
		//expected behavior		
		EasyMock.expect(mockCmisRepo.getName()).andReturn("Shared Documents").anyTimes();
		EasyMock.expect(mockCmisRepo.createSession()).andReturn(mockCmisSession);
		EasyMock.expect(mockCmisSession.getObject("100")).andReturn(expectedFolder);		
		
		//register behavior
		EasyMock.replay(mockCmisRepo, mockCmisSession);

		Content content = ccp.getContent(contentUrl);
		
		//verify
		EasyMock.verify();
						
		assertNotNull(content);
		assertNotNull(content.getModificationDate());
		assertEquals(CmisContentProvider.CMIS_FOLDER_TYPE, content.getType());	
		assertEquals(ccp.getId(), content.getProvider());
		
		assertTrue(content.getAttributes().size() > 0);
				
		assertNotNull(content.getAttributeByName("cmis:objectId").getValue());
		assertNotNull(content.getAttributeByName("cmis:name").getValue());
		assertNotNull(content.getAttributeByName("boolProp").getValue());
		assertNotNull(content.getAttributeByName("dateProp").getValue());
		assertNotNull(content.getAttributeByName("intProp").getValue());
		assertNotNull(content.getAttributeByName("decimalProp").getValue());
		
	}
	
	public void testGetContentWithCustomProperties() {
		
		String contentUrl = "http://cmis/Shared Documents/cmis:document#3-1024";

		//expected doc				
		MockDocument expectedDoc = new MockDocument();				
		expectedDoc.getProperties().add(helperCreateProperty("cmis:objectId","3-1024"));
		expectedDoc.getProperties().add(helperCreateProperty("cmis:name","MyFile.txt"));
		expectedDoc.getProperties().add(helperCreateProperty("Title","The title"));
		expectedDoc.getProperties().add(helperCreateProperty("MyData","data value"));
		expectedDoc.getProperties().add(helperCreateProperty("MyData2","data value 2"));
		expectedDoc.setContentStream(helperCreateContentStream(), true);		
		
		//expected behavior		
		EasyMock.expect(mockCmisRepo.getName()).andReturn("Shared Documents").anyTimes();
		EasyMock.expect(mockCmisRepo.createSession()).andReturn(mockCmisSession);
		EasyMock.expect(mockCmisSession.getObject("3-1024")).andReturn(expectedDoc);		
		
		//register behavior
		EasyMock.replay(mockCmisRepo, mockCmisSession);

		Content content = ccp.getContent(contentUrl);
		
		//verify
		EasyMock.verify();
		
		assertNotNull(content);
		assertNotNull(content.getModificationDate());
		assertEquals("CMIS_DOCUMENT", content.getType());	
		assertEquals(ccp.getId(), content.getProvider());
		
		assertTrue(content.getAttributes().size() > 0);
				
		assertEquals("true",content.getAttributeByName("hasContentStream").getValue());
		
		assertNotNull(content.getAttributeByName("cmis:objectId").getValue());
		assertNotNull(content.getAttributeByName("cmis:name").getValue());
		//file parser properties
		assertNotNull(content.getAttributeByName("FILENAME").getValue());				
		assertNotNull(content.getAttributeByName("TITLE").getValue());
		//custom CMIS properties
		assertNotNull(content.getAttributeByName("Title").getValue());				
		assertNotNull(content.getAttributeByName("MyData").getValue());
		assertNotNull(content.getAttributeByName("MyData2").getValue());

	}
	
	public void testInit(){
		Properties initParams = new Properties();
		initParams.put("USERNAME","username");
		initParams.put("PASSWORD","password");			
		String sharepointWS = "http://mySharepointServer/_vti_bin/cmissoapwsdl.aspx?wsdl";		
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

		initParams.put("AUTHENTICATION_PROVIDER_CLASS","BASIC");

		initParams.put("ATTRIBUTE-MAPPINGS","[cmis:name=CmisName][Title=CmisTitle]");
		initParams.put("KEY-ATTRIBUTES","[CmisName][CmisTitle]");
		
		ccp.setInitParams(initParams);
		try{
			ccp.init();
			fail();
		}catch(CmisBaseException cbe){
			
		}					
		
		//authentication - Standard or NTLM
		initParams.put("AUTHENTICATION_PROVIDER_CLASS","NTLM");
		//repository
		initParams.put("REPOSITORY","[RootRepo]");
		ccp.setInitParams(initParams);
		try{
			ccp.init();
			fail();
		}catch(CmisBaseException cbe){
			
		}		
	}
	
	
	public void testDestroy(){
		ccp.destroy();
	}
	
	public void testGetActions(){
				
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
		

		content.setType("NONE");
		
		actions = ccp.getActions(content);
		
		assertTrue(actions.size() == 0);

	}
	
	public void testKeyAttributes(){

		Properties prop = new Properties();
		prop.setProperty("KEY-ATTRIBUTES", "[myProp]");
		ccp.setInitParams(prop);
		try{
			ccp.init();
			fail();
		}catch(CmisBaseException cbe){			
		}
		ccp.getRepositories().add(mockCmisRepo);
		
		String contentUrl = "http://cmis/Shared Documents/cmis:document#3-1024";

		//expected doc				
		MockDocument expectedDoc = new MockDocument();				
		expectedDoc.getProperties().add(helperCreateProperty("cmis:objectId","3-1024"));
		expectedDoc.getProperties().add(helperCreateProperty("cmis:name","MyFile.txt"));
		expectedDoc.getProperties().add(helperCreateProperty("myProp","myPropValue"));		
		expectedDoc.setContentStream(helperCreateContentStream(), true);		
		
		//expected behavior		
		EasyMock.expect(mockCmisRepo.getName()).andReturn("Shared Documents").anyTimes();
		EasyMock.expect(mockCmisRepo.createSession()).andReturn(mockCmisSession);
		EasyMock.expect(mockCmisSession.getObject("3-1024")).andReturn(expectedDoc);		
		
		//register behavior
		EasyMock.replay(mockCmisRepo, mockCmisSession);

		Content content = ccp.getContent(contentUrl);
		
		//verify
		EasyMock.verify();
						
		assertNotNull(content);
		assertNotNull(content.getModificationDate());
		assertEquals("CMIS_DOCUMENT", content.getType());	
		assertEquals(ccp.getId(), content.getProvider());
		
		assertTrue(content.getAttributes().size() > 0);
		
		assertTrue(content.getAttributeByName("myProp").isKey());		
		
	}
	
	public void testAttributesMappings(){

		Properties prop = new Properties();
		prop.setProperty("ATTRIBUTE-MAPPINGS", "[cmis:name=Cmis_Name]");
		ccp.setInitParams(prop);
		try{
			ccp.init();
			fail();
		}catch(CmisBaseException cbe){			
		}
		ccp.getRepositories().add(mockCmisRepo);
		
		String contentUrl = "http://cmis/Shared Documents/cmis:document#3-1024";

		//expected doc				
		MockDocument expectedDoc = new MockDocument();				
		expectedDoc.getProperties().add(helperCreateProperty("cmis:objectId","3-1024"));
		expectedDoc.getProperties().add(helperCreateProperty("cmis:name","MyFile.txt"));
		expectedDoc.getProperties().add(helperCreateProperty("myProp","myPropValue"));		
		expectedDoc.setContentStream(helperCreateContentStream(), true);		
		
		//expected behavior		
		EasyMock.expect(mockCmisRepo.getName()).andReturn("Shared Documents").anyTimes();
		EasyMock.expect(mockCmisRepo.createSession()).andReturn(mockCmisSession);
		EasyMock.expect(mockCmisSession.getObject("3-1024")).andReturn(expectedDoc);		
		
		//register behavior
		EasyMock.replay(mockCmisRepo, mockCmisSession);

		Content content = ccp.getContent(contentUrl);
		
		//verify
		EasyMock.verify();
						
		assertNotNull(content);
		assertNotNull(content.getModificationDate());
		assertEquals("CMIS_DOCUMENT", content.getType());	
		assertEquals(ccp.getId(), content.getProvider());
		
		assertTrue(content.getAttributes().size() > 0);
		
		assertNull(content.getAttributeByName("cmis:name"));
		assertNotNull(content.getAttributeByName("Cmis_Name"));		
		assertEquals("myPropValue", content.getAttributeByName("myProp").getValue());
		assertEquals("MyFile.txt", content.getAttributeByName("Cmis_Name").getValue());	
		
	}
	
	
	//helper methods	
	
	private Content helperCreateDummyDocumentContentFromUrl(String contentUrl){
		Content content = new Content();
		content.setProvider(ccp.getId());
		content.setType("CMIS_DOCUMENT");
		content.setContentUrl(contentUrl);
		
		return content;
	}
	
	private ContentStreamImpl helperCreateContentStream(){
		ContentStreamImpl contents = new ContentStreamImpl();
        contents.setFileName("data.txt");
        contents.setMimeType("text/plain");
        int len = 32 * 1024;
        byte[] b = { 0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6a, 0x6b, 0x6c, 0x6d, 0x6e, 0x0c, 0x0a,
                0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6a, 0x6b, 0x6c, 0x6d, 0x6e, 0x0c, 0x0a }; // 32
        // Bytes
        ByteArrayOutputStream ba = new ByteArrayOutputStream(len);
        try {
            for (int i = 0; i < 1024; i++)
                ba.write(b);
        } catch (IOException e) {
            throw new RuntimeException("Failed to fill content stream with data", e);
        }
        contents.setStream(new ByteArrayInputStream(ba.toByteArray()));
        contents.setLength(BigInteger.valueOf(len));
        
        return contents;
	}
	
	private Property<?> helperCreateProperty(String id, String value){
		PropertyStringDefinitionImpl pd = new PropertyStringDefinitionImpl();
		pd.setId(id);
		pd.setUpdatability(Updatability.READWRITE);
		List<String> values = new ArrayList<String>();
		values.add(value);
		return new PropertyImpl<String>(pd, values);
	}
	
	private Property<?> helperCreateProperty(String id, String value, PropertyType propertyType){
		PropertyStringDefinitionImpl pd = new PropertyStringDefinitionImpl();
		pd.setId(id);
		pd.setUpdatability(Updatability.READWRITE);
		pd.setPropertyType(propertyType);		
		List<String> values = new ArrayList<String>();
		values.add(value);
		return new PropertyImpl<String>(pd, values);
	}
	
}

