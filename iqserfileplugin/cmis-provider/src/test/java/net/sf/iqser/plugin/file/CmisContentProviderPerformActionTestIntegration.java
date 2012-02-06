package net.sf.iqser.plugin.file;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;
import net.sf.iqser.plugin.file.mock.MockAnalyzerTaskStarter;
import net.sf.iqser.plugin.file.mock.MockContentProviderFacade;
import net.sf.iqser.plugin.file.mock.MockRepository;
import net.sf.iqser.plugin.file.mock.TestServiceLocator;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.ObjectIdImpl;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.log4j.PropertyConfigurator;

import com.iqser.core.config.Configuration;
import com.iqser.core.exception.IQserException;
import com.iqser.core.model.Attribute;
import com.iqser.core.model.Content;

@SuppressWarnings("unchecked")
public class CmisContentProviderPerformActionTestIntegration extends TestCase {
				
	CmisContentProvider ccp;
	
	private Folder testFolder;
	private String CmisTestFolderId;
	
	private Session cmisSession;
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
		
		MockContentProviderFacade cpFacade = new MockContentProviderFacade();
		cpFacade.setRepo(repo);
		sl.setFacade(cpFacade);
				
		cmisSession = createSession();
		Map<String, String> prop = new HashMap<String, String>();
		prop.put("cmis:name", "TestFolder");
		prop.put("cmis:objectTypeId", "cmis:folder");
		
		testFolder = cmisSession.getRootFolder().createFolder(prop);
		CmisTestFolderId = testFolder.getId();
	}

	@Override
	protected void tearDown() throws Exception {
		try{			
			for (CmisObject cmisobj : testFolder.getChildren()) {
				cmisobj.delete(true);
			}
			testFolder.delete(true);
		}catch(CmisRuntimeException cre){
			//ignoreit
		}
				
		cmisSession.clear();		
		cmisSession= null;
		repo = null;		
		super.tearDown();
	}
	
	public void testPerformAction() throws IQserException {
		//add document in CMIS repository
		ContentStream contentStream = new ContentStreamImpl("testUpdate.txt","text/plain","This is the content");
		Map<String, String> props =new HashMap<String, String>();
		props.put("cmis:name", "testUpdate");
		props.put("Title", "TestFile001");
		props.put("cmis:objectTypeId", "cmis:document");
		ObjectId oid = cmisSession.createDocument(props, new ObjectIdImpl(CmisTestFolderId), contentStream, VersioningState.MAJOR );		
		//create content and add it to object graph 
		String contentUrl = "http://cmis/Shared Documents/cmis:document#"+oid.getId();
		Content content = ccp.getContent(contentUrl);
		repo.addContent(content);		
		
		//check if documents exists in object graph
		System.out.println(repo.getContentByProvider(ccp.getId(), true).size());
		assertTrue(repo.getContentByProvider(ccp.getId(), true).size() == 1);		
		
		ccp.performAction(CmisContentProvider.ACTION_CHECK_OUT, content);
		
		//check if documents exists in object graph
		assertTrue(repo.getContentByProvider(ccp.getId(), true).size() == 2);
		
		//query repo and obtain pwc
		String newId = null;
		Content newContent = null;
		for (Content item : (Collection<Content>)repo.getContentByProvider(ccp.getId(),true)) {						
			String isLV = item.getAttributeByName("cmis:isLatestMajorVersion").getValue();
			String isVCO = item.getAttributeByName("cmis:isVersionSeriesCheckedOut").getValue();
			if ("true".equals(isVCO)&& "true".equals(isLV)){
				newId = item.getAttributeByName("cmis:objectId").getValue();
				newContent = item;
				break;
			}
		}		
		
		System.out.println(oid.getId()+ " " + newId);
		
		
		//update properties
		newContent.getAttributeByName("Title").setValue("NewTestFile001");
		newContent.getAttributeByName("cmis:name").setValue("NewNameTest");
		
		ccp.performAction(CmisContentProvider.ACTION_UPDATE, newContent);
				
		ccp.performAction(CmisContentProvider.ACTION_CHECK_IN, newContent);
		
		//POSTCONDITIONS 
		//check if documents exists in object graph
		assertTrue(repo.getContentByProvider(ccp.getId(), true).size() == 2);		
		//object properties have been changed
		cmisSession.clear();
		Document doc = (Document)cmisSession.getObject(newId);	
		assertEquals("NewTestFile001", doc.getPropertyValue("Title"));
		assertEquals("NewNameTest.txt", doc.getPropertyValue("cmis:name"));	
		
	}

	public void testPerformActionUpdate() throws IQserException {	
		
		//add document in CMIS repository
		ContentStream contentStream = new ContentStreamImpl("testUpdate.txt","text/plain","This is the content");
		Map<String, String> props =new HashMap<String, String>();
		props.put("cmis:name", "testUpdate");
		props.put("Title", "TestFile001");
		props.put("cmis:objectTypeId", "cmis:document");
		ObjectId oid = cmisSession.createDocument(props, new ObjectIdImpl(CmisTestFolderId), contentStream, VersioningState.CHECKEDOUT );		
		//create content and add it to object graph 
		String contentUrl = "http://cmis/Shared Documents/cmis:document#"+oid.getId();
		Content content = createDummyContentFromUrl(contentUrl);
		content.addAttribute(new Attribute("Title","TestFile001",Attribute.ATTRIBUTE_TYPE_TEXT));
		content.addAttribute(new Attribute("cmis:name","testUpdate",Attribute.ATTRIBUTE_TYPE_TEXT));
		repo.addContent(content);
		
		//PRECONDITIONS
		//check if document exists in CMIS repository and checked out 	
		try{
			Document doc = (Document)cmisSession.getObject(oid);
			assertNotNull(doc);		
			assertEquals("TestFile001", doc.getPropertyValue("Title"));
			assertEquals("testUpdate.txt", doc.getPropertyValue("cmis:name"));
		}catch(CmisObjectNotFoundException e){
			fail("Object not created");
		}						
		//check if documents exists in object graph
		assertTrue(repo.getContentByProvider(ccp.getId(), true).size() == 1);
		
		//update properties
		content.getAttributeByName("Title").setValue("NewTestFile001");
		content.getAttributeByName("cmis:name").setValue("NewNameTest");		
		
		ccp.performAction(CmisContentProvider.ACTION_UPDATE, content);
				
		//POSTCONDITIONS 
		//check if documents exists in object graph
		assertTrue(repo.getContentByProvider(ccp.getId(), true).size() == 1);		
		//object properties have been changed
		cmisSession.clear();
		Document doc = (Document)cmisSession.getObject(oid);
		assertEquals("NewTestFile001", doc.getPropertyValue("Title"));
		assertEquals("NewNameTest.txt", doc.getPropertyValue("cmis:name"));		
		
	}

	public void testPerformActionDelete() throws IQserException{
		
		//add document in CMIS repository
		ContentStream contentStream = new ContentStreamImpl("test.txt","text/plain","This is the content");
		Map<String, String> props =new HashMap<String, String>();
		props.put("cmis:name", "test");
		props.put("Title", "TestFile001");
		props.put("cmis:objectTypeId", "cmis:document");
		ObjectId oid = cmisSession.createDocument(props, new ObjectIdImpl(CmisTestFolderId), contentStream, VersioningState.MAJOR );						
		//create content and add it to object graph 
		String contentUrl = "http://cmis/Shared Documents/cmis:document#"+oid.getId();
		Content content = createDummyContentFromUrl(contentUrl);		
		repo.addContent(content);
		
		//PRECONDITIONS
		//check if document exists in CMIS repository		
		try{
			CmisObject cmisObj = cmisSession.getObject(oid);
			assertNotNull(cmisObj);			
		}catch(CmisObjectNotFoundException e){
			fail("Object not created");
		}						
		//check if documents exists in object graph
		assertTrue(repo.getContentByProvider(ccp.getId(), true).size() == 1);
		
		//perform operation
		ccp.performAction(CmisContentProvider.ACTION_DELETE, content);		
				
		//POSTCONDITIONS
		//document deleted from object graph		
		assertTrue(repo.getContentByProvider(ccp.getId(), true).isEmpty());
		//document deleted from CMIS repository
		try{
			cmisSession.clear();
			cmisSession.getObject(oid);			
			fail("Object not deleted");
		}catch(CmisObjectNotFoundException e){		
		}
	}

	public void testPerformActionCheckOut() throws IQserException {
		
		//initialization
		//add document in CMIS repository
		ContentStream contentStream = new ContentStreamImpl("test.txt","text/plain","This is the content");
		Map<String, String> props =new HashMap<String, String>();
		props.put("cmis:name", "test");
		props.put("Title", "TestFile001");
		props.put("cmis:objectTypeId", "cmis:document");
		ObjectId oid = cmisSession.createDocument(props, new ObjectIdImpl(CmisTestFolderId), contentStream, VersioningState.MAJOR );						
		//create content and add it to object graph 
		String contentUrl = "http://cmis/Shared Documents/cmis:document#"+oid.getId();
		Content content = createDummyContentFromUrl(contentUrl);		
		repo.addContent(content);
		
		//PRECONDITIONS
		//check if document exists in CMIS repository		
		try{
			Document doc = (Document)cmisSession.getObject(oid);
			assertNotNull(doc);
			//not checked out
			assertFalse(doc.isVersionSeriesCheckedOut());						
		}catch(CmisObjectNotFoundException e){
			fail("Object not created");
		}						
		//check if documents exists in object graph
		assertTrue(repo.getContentByProvider(ccp.getId(), true).size() == 1);

		ccp.performAction(CmisContentProvider.ACTION_CHECK_OUT, content);
		
		//POSTCONDITIONS - object is check out
		cmisSession.clear();
		Document doc = (Document)cmisSession.getObject(oid);
		assertTrue(doc.isVersionSeriesCheckedOut());		
		
	}

	public void testPerformActionCheckIn() throws IQserException {
						
		//initialization
		//add document in CMIS repository
		ContentStream contentStream = new ContentStreamImpl("test.txt","text/plain","This is the content");
		Map<String, String> props =new HashMap<String, String>();
		props.put("cmis:name", "test");
		props.put("Title", "TestFile001");
		props.put("cmis:objectTypeId", "cmis:document");
		ObjectId oid = cmisSession.createDocument(props, new ObjectIdImpl(CmisTestFolderId), contentStream, VersioningState.MAJOR );						
		//create content and add it to object graph 
		String contentUrl = "http://cmis/Shared Documents/cmis:document#"+oid.getId();
		Content content = createDummyContentFromUrl(contentUrl);
		content.addAttribute(new Attribute("Title","TestFile001",Attribute.ATTRIBUTE_TYPE_TEXT));
		content.addAttribute(new Attribute("cmis:name","test",Attribute.ATTRIBUTE_TYPE_TEXT));		
		repo.addContent(content);
		
		ccp.performAction(CmisContentProvider.ACTION_CHECK_OUT, content);
		
		
		//PRECONDITIONS
		//check if document exists in CMIS repository		
		try{
			Document doc = (Document)cmisSession.getObject(oid);
			assertNotNull(doc);
			//checked out
			assertTrue(doc.isVersionSeriesCheckedOut());
		}catch(CmisObjectNotFoundException e){
			fail("Object not created");
		}						
		//check if documents exists in object graph
		assertTrue(repo.getContentByProvider(ccp.getId(), true).size() == 2);

		ccp.performAction(CmisContentProvider.ACTION_CHECK_IN, content);
										
		//POSTCONDITIONS - object is check out
		cmisSession.clear();
		Document doc = (Document)cmisSession.getObject(oid);
		assertFalse(doc.isVersionSeriesCheckedOut());		
		
	}	
	
	private Session createSession(){
		Collection<Repository> repoList = ccp.getRepositories();
		for (Repository repository : repoList) {			
			return repository.createSession();
		}
		return null;
	}
	
	private Content createDummyContentFromUrl(String contentUrl){
		Content content = new Content();
		content.setProvider(ccp.getId());
		content.setType("CMIS_DOCUMENT");
		content.setContentUrl(contentUrl);
		
		return content;
	}
		

}

