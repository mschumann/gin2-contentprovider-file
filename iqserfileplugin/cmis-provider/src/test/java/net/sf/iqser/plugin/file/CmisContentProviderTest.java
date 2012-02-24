package net.sf.iqser.plugin.file;

import com.iqser.core.exception.IQserException;
import com.iqser.core.model.Attribute;
import com.iqser.core.model.Content;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import junit.framework.TestCase;
import net.sf.iqser.plugin.file.mock.MockRepository;
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
		
		
	}

	@Override
	protected void tearDown() throws Exception {						
		super.tearDown();
	}

	public void testDoSynchronization() throws IQserException {

		
		
	}
	
	public void testDoSynchronizationWithUpdate1() throws IQserException {

		
		
	}
	
	public void testDoSynchronizationWithUpdate2() throws IQserException {

		
		
	}

	public void testDoHousekeeping() throws IQserException {
			
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
		String atompubUrl = "http://alfresco/alfresco/service/cmis";		
		// CMIS Atompub Url
		initParams.put("ATOMPUB",atompubUrl); 

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

		/*Properties prop = new Properties();
		prop.setProperty("KEY-ATTRIBUTES", "[myProp]");*/
                
                Properties initParams = new Properties();
		initParams.put("USERNAME","username");
		initParams.put("PASSWORD","password");
		String atompubUrl = "http://alfresco/alfresco/service/cmis";		
		// CMIS Atompub Url
		initParams.put("ATOMPUB",atompubUrl); 

		initParams.put("AUTHENTICATION_PROVIDER_CLASS","BASIC");

		initParams.put("ATTRIBUTE-MAPPINGS","[cmis:name=CmisName][Title=CmisTitle]");
		initParams.put("KEY-ATTRIBUTES", "[myProp]");
		
                ccp.setInitParams(initParams);
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

		/*Properties prop = new Properties();
		prop.setProperty("ATTRIBUTE-MAPPINGS", "[cmis:name=Cmis_Name]");*/
            
                Properties initParams = new Properties();
		initParams.put("USERNAME","username");
		initParams.put("PASSWORD","password");
		String atompubUrl = "http://alfresco/alfresco/service/cmis";		
		// CMIS Atompub Url
		initParams.put("ATOMPUB",atompubUrl); 

		initParams.put("AUTHENTICATION_PROVIDER_CLASS","BASIC");
                initParams.put("ATTRIBUTE-MAPPINGS", "[cmis:name=Cmis_Name]");
		ccp.setInitParams(initParams);
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
	
	public void testContentTypeMappings(){

		/*Properties prop = new Properties();
		prop.setProperty("CONTENT-TYPE-MAPPINGS", "[CMIS_DOCUMENT=IQserDocument]");*/
            
                Properties initParams = new Properties();
		initParams.put("USERNAME","username");
		initParams.put("PASSWORD","password");
		String atompubUrl = "http://alfresco/alfresco/service/cmis";		
		// CMIS Atompub Url
		initParams.put("ATOMPUB",atompubUrl); 

		initParams.put("AUTHENTICATION_PROVIDER_CLASS","BASIC");
                initParams.put("CONTENT-TYPE-MAPPINGS", "[CMIS_DOCUMENT=IQserDocument]");
		ccp.setInitParams(initParams);
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
		assertEquals("IQserDocument", content.getType());	
		assertEquals(ccp.getId(), content.getProvider());
		
		assertTrue(content.getAttributes().size() > 0);
				
		assertEquals("myPropValue", content.getAttributeByName("myProp").getValue());
		assertEquals("MyFile.txt", content.getAttributeByName("cmis:name").getValue());	
		
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

