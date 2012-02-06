package net.sf.iqser.plugin.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;
import net.sf.iqser.plugin.file.mock.MockAnalyzerTaskStarter;
import net.sf.iqser.plugin.file.mock.MockContentProviderFacade;
import net.sf.iqser.plugin.file.mock.MockRepository;
import net.sf.iqser.plugin.file.mock.TestServiceLocator;
import net.sf.iqser.plugin.file.mock.cmis.MockDocument;

import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.PropertyImpl;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyStringDefinitionImpl;
import org.apache.log4j.PropertyConfigurator;
import org.easymock.EasyMock;

import com.iqser.core.config.Configuration;
import com.iqser.core.exception.IQserException;
import com.iqser.core.exception.IQserRuntimeException;
import com.iqser.core.model.Attribute;
import com.iqser.core.model.Content;

@SuppressWarnings("unchecked")
public class CmisContentProviderPerformActionTest extends TestCase {
				
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
		
		MockContentProviderFacade cpFacade = new MockContentProviderFacade();
		cpFacade.setRepo(repo);
		sl.setFacade(cpFacade);
	}

	@Override
	protected void tearDown() throws Exception {						
		super.tearDown();
	}	
	

	public void testPerformAction() throws IQserException {
		//create content and add it to object graph 
		String contentUrl = "http://cmis/Shared Documents/cmis:document#1-512";
		Content content = createDummyContentFromUrl(contentUrl);
		content.addAttribute(new Attribute("Title","TestFile.txt",Attribute.ATTRIBUTE_TYPE_TEXT));
		content.addAttribute(new Attribute("cmis:name","testUpdate",Attribute.ATTRIBUTE_TYPE_TEXT));
		content.addAttribute(new Attribute("cmis:isLatestMajorVersion","true",Attribute.ATTRIBUTE_TYPE_BOOLEAN));
		content.addAttribute(new Attribute("cmis:isVersionSeriesCheckedOut","false",Attribute.ATTRIBUTE_TYPE_BOOLEAN));
		repo.addContent(content);		
		
		//check if documents exists in object graph
		assertTrue(repo.getContentByProvider(ccp.getId(), true).size() == 1);	
		
		//expected behavior
		//expected behavior
		MockDocument dummyDoc = new MockDocument();
		dummyDoc.dummySetCheckOutId("1-1024");
		dummyDoc.getProperties().add(helperCreateProperty("cmis:objectId","1-512"));
		dummyDoc.getProperties().add(helperCreateProperty("cmis:name","testUpdate"));
		dummyDoc.getProperties().add(helperCreateProperty("Title","TestFile.txt"));
		dummyDoc.getProperties().add(helperCreateProperty("cmis:isLatestMajorVersion","true"));
		dummyDoc.getProperties().add(helperCreateProperty("cmis:isVersionSeriesCheckedOut","false"));		
		
		MockDocument dummyDoc1 = new MockDocument();
		dummyDoc1.dummySetCheckOutId("1-1536");
		dummyDoc1.getProperties().add(helperCreateProperty("cmis:objectId","1-1024"));
		dummyDoc1.getProperties().add(helperCreateProperty("cmis:name","testUpdate"));
		dummyDoc1.getProperties().add(helperCreateProperty("Title","TestFile.txt"));
		dummyDoc1.getProperties().add(helperCreateProperty("cmis:isLatestMajorVersion","true"));
		dummyDoc1.getProperties().add(helperCreateProperty("cmis:isVersionSeriesCheckedOut","true"));
				
		//checkout
		EasyMock.expect(mockCmisRepo.getName()).andReturn("Shared Documents").anyTimes();
		EasyMock.expect(mockCmisRepo.createSession()).andReturn(mockCmisSession);
		EasyMock.expect(mockCmisSession.getObject("1-512")).andReturn(dummyDoc);	
		EasyMock.expect(mockCmisSession.getObject("1-1024")).andReturn(dummyDoc1);
		//update
		EasyMock.expect(mockCmisRepo.createSession()).andReturn(mockCmisSession);
		EasyMock.expect(mockCmisSession.getObject("1-1024")).andReturn(dummyDoc1);
		//checkin
		EasyMock.expect(mockCmisRepo.createSession()).andReturn(mockCmisSession);
		EasyMock.expect(mockCmisSession.getObject("1-1024")).andReturn(dummyDoc1);
		
		//register behavior
		EasyMock.replay(mockCmisRepo, mockCmisSession);

		//perform operation
		ccp.performAction(CmisContentProvider.ACTION_CHECK_OUT, content);
		
		//check if documents exists in object graph
		assertTrue(repo.getContentByProvider(ccp.getId(), true).size() == 2);
		
		//query repo and obtain pwc
		Content newContent = null;
		for (Content item : (Collection<Content>)repo.getContentByProvider(ccp.getId(),true)) {						
			String isLV = item.getAttributeByName("cmis:isLatestMajorVersion").getValue();
			String isVCO = item.getAttributeByName("cmis:isVersionSeriesCheckedOut").getValue();
			if ("true".equals(isVCO)&& "true".equals(isLV)){
				//String newId = item.getAttributeByName("cmis:objectId").getValue();
				newContent = item;
				break;
			}
		}		
		
		//update properties
		newContent.getAttributeByName("Title").setValue("NewTestFile001");
		newContent.getAttributeByName("cmis:name").setValue("NewNameTest");
		
		ccp.performAction(CmisContentProvider.ACTION_UPDATE, newContent);
				
		ccp.performAction(CmisContentProvider.ACTION_CHECK_IN, newContent);
		
		//verify
		EasyMock.verify();
		
		//POSTCONDITIONS 
		//check if documents exists in object graph
		assertTrue(repo.getContentByProvider(ccp.getId(), true).size() == 2);		
		//object properties have been changed
		
	}



	public void testPerformActionUpdate() throws IQserException {	
						
		//create content and add it to object graph 
		String contentUrl = "http://cmis/Shared Documents/cmis:document#1-512";
		Content content = createDummyContentFromUrl(contentUrl);
		content.addAttribute(new Attribute("Title","TestFile.txt",Attribute.ATTRIBUTE_TYPE_TEXT));
		content.addAttribute(new Attribute("cmis:name","testUpdate",Attribute.ATTRIBUTE_TYPE_TEXT));
		repo.addContent(content);
		
		//PRECONDITIONS
		//check if documents exists in object graph
		assertTrue(repo.getContentByProvider(ccp.getId(), true).size() == 1);
		
		//update properties
		content.getAttributeByName("Title").setValue("NewTestFile001");
		content.getAttributeByName("cmis:name").setValue("NewNameTest");		
				
		//expected behavior
		MockDocument mockDoc = new MockDocument();
		mockDoc.getProperties().add(helperCreateProperty("cmis:name","testUpdate"));
		mockDoc.getProperties().add(helperCreateProperty("Title","TestFile.txt"));
		
		EasyMock.expect(mockCmisRepo.getName()).andReturn("Shared Documents").anyTimes();
		EasyMock.expect(mockCmisRepo.createSession()).andReturn(mockCmisSession);
		EasyMock.expect(mockCmisSession.getObject("1-512")).andReturn(mockDoc);		
		
		//register behavior
		EasyMock.replay(mockCmisRepo, mockCmisSession);

		//perform operation
		ccp.performAction(CmisContentProvider.ACTION_UPDATE, content);		
		
		//verify
		EasyMock.verify();
				
		//POSTCONDITIONS 
		//check if documents exists in object graph
		assertTrue(repo.getContentByProvider(ccp.getId(), true).size() == 1);		
		
	}

	public void testPerformActionDelete() throws IQserException{
		
		//add document in CMIS repository
		String contentUrl = "http://cmis/Shared Documents/cmis:document#1-512";
		Content content = createDummyContentFromUrl(contentUrl);		
		repo.addContent(content);
		
		//check if documents exists in object graph
		assertTrue(repo.getContentByProvider(ccp.getId(), true).size() == 1);
				
		//expected behavior
		MockDocument mockDoc = new MockDocument();
		EasyMock.expect(mockCmisRepo.getName()).andReturn("Shared Documents").anyTimes();
		EasyMock.expect(mockCmisRepo.createSession()).andReturn(mockCmisSession);
		EasyMock.expect(mockCmisSession.getObject("1-512")).andReturn(mockDoc);		
		
		//register behavior
		EasyMock.replay(mockCmisRepo, mockCmisSession);

		//perform operation
		ccp.performAction(CmisContentProvider.ACTION_DELETE, content);		
		
		//verify
		EasyMock.verify();
				
		//POSTCONDITIONS
		//document deleted from object graph		
		assertTrue(repo.getContentByProvider(ccp.getId(), true).isEmpty());
		
	}

	public void testPerformActionCheckOut() throws IQserException {
		
		//initialization								
		//create content and add it to object graph 
		String contentUrl = "http://cmis/Shared Documents/cmis:document#1-512";
		Content content = createDummyContentFromUrl(contentUrl);		
		repo.addContent(content);
		
		//check if documents exists in object graph
		assertTrue(repo.getContentByProvider(ccp.getId(), true).size() == 1);

		//expected behavior
		MockDocument dummyDoc = new MockDocument();
		dummyDoc.dummySetCheckOutId("1-1024");
		dummyDoc.getProperties().add(helperCreateProperty("cmis:objectId","1-512"));

		MockDocument dummyDoc1 = new MockDocument();
		dummyDoc1.dummySetCheckOutId("1-1536");
		dummyDoc1.getProperties().add(helperCreateProperty("cmis:objectId","1-1024"));
		
		EasyMock.expect(mockCmisRepo.getName()).andReturn("Shared Documents").anyTimes();
		EasyMock.expect(mockCmisRepo.createSession()).andReturn(mockCmisSession);
		EasyMock.expect(mockCmisSession.getObject("1-512")).andReturn(dummyDoc);
		EasyMock.expect(mockCmisSession.getObject("1-1024")).andReturn(dummyDoc1);
		
		//register behavior
		EasyMock.replay(mockCmisRepo, mockCmisSession);
		
		ccp.performAction(CmisContentProvider.ACTION_CHECK_OUT, content);
		
		//verify
		EasyMock.verify();
		
		//POSTCONDITIONS - object is check out
		assertTrue(repo.getContentByProvider(ccp.getId(), true).size() == 2);
		
	}

	public void testPerformActionCheckIn() throws IQserException {
						
		//initialization
		//create content and add it to object graph
		String contentUrl = "http://cmis/Shared Documents/cmis:document#1-512";
		Content content = createDummyContentFromUrl(contentUrl);
		content.addAttribute(new Attribute("Title","TestFile001",Attribute.ATTRIBUTE_TYPE_TEXT));
		content.addAttribute(new Attribute("cmis:name","test",Attribute.ATTRIBUTE_TYPE_TEXT));		
		repo.addContent(content);

		contentUrl = "http://cmis/Shared Documents/cmis:document#1-1024";
		content = createDummyContentFromUrl(contentUrl);
		content.addAttribute(new Attribute("Title","TestFile001",Attribute.ATTRIBUTE_TYPE_TEXT));
		content.addAttribute(new Attribute("cmis:name","test",Attribute.ATTRIBUTE_TYPE_TEXT));
		content.addAttribute(new Attribute("cmis:checkinComment","coment",Attribute.ATTRIBUTE_TYPE_TEXT));
		repo.addContent(content);
				
		//PRECONDITIONS
		assertTrue(repo.getContentByProvider(ccp.getId(), true).size() == 2);		
		
		MockDocument dummyDoc1 = new MockDocument();
		dummyDoc1.getProperties().add(helperCreateProperty("cmis:objectId","1-1024"));
		dummyDoc1.getProperties().add(helperCreateProperty("cmis:name","test"));
		dummyDoc1.getProperties().add(helperCreateProperty("Title","TestFile.txt"));

		EasyMock.expect(mockCmisRepo.getName()).andReturn("Shared Documents").anyTimes();
		EasyMock.expect(mockCmisRepo.createSession()).andReturn(mockCmisSession);
		EasyMock.expect(mockCmisSession.getObject("1-1024")).andReturn(dummyDoc1);
		
		//register behavior
		EasyMock.replay(mockCmisRepo, mockCmisSession);
		
		ccp.performAction(CmisContentProvider.ACTION_CHECK_IN, content);
		
		//verify
		EasyMock.verify();
														
		//POSTCONDITIONS - object is not checkout
		Collection<Content> contentList = (Collection<Content>)repo.getContentByProvider(ccp.getId(), true);		
		assertTrue(contentList.size() == 2);
						
	}		
	
	
	public void testPerformUnknownAction() throws IQserException {
		
		String contentUrl = "http://cmis/Shared Documents/cmis:document#1-512";
		Content content = createDummyContentFromUrl(contentUrl);
		try{
			ccp.performAction("UNKNOWN", content);
			fail();
		}catch(IQserRuntimeException ire){			
		}		
	}
	
	private Content createDummyContentFromUrl(String contentUrl){
		Content content = new Content();
		content.setProvider(ccp.getId());
		content.setType("CMIS_DOCUMENT");
		content.setContentUrl(contentUrl);
		
		return content;
	}
	
	private Property<?> helperCreateProperty(String id, String value){
		PropertyStringDefinitionImpl pd = new PropertyStringDefinitionImpl();
		pd.setId(id);
		pd.setUpdatability(Updatability.READWRITE);
		List<String> values = new ArrayList<String>();
		values.add(value);
		return new PropertyImpl<String>(pd, values);
	}		
		

}

