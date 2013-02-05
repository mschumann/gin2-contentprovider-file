package net.sf.iqser.plugin.file;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import net.sf.iqser.plugin.file.mock.MockRepository;

import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.PropertyImpl;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyStringDefinitionImpl;
import org.apache.log4j.PropertyConfigurator;
import org.easymock.EasyMock;

import com.iqser.core.exception.IQserException;
import com.iqser.core.exception.IQserRuntimeException;
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

		ccp.setName("net.sf.iqser.plugin.file");
		ccp.getRepositories().add(mockCmisRepo);

	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}


	public void testPerformAction() throws IQserException {


	}



	public void testPerformActionUpdate() throws IQserException {


	}

	public void testPerformActionDelete() throws IQserException{



	}

	public void testPerformActionCheckOut() throws IQserException {



	}

	public void testPerformActionCheckIn() throws IQserException {



	}


	public void testPerformUnknownAction() throws IQserException {

		String contentUrl = "http://cmis/Shared Documents/cmis:document#1-512";
		Content content = createDummyContentFromUrl(contentUrl);
		try{
			ccp.performAction("UNKNOWN",null, content);
			fail();
		}catch(IQserRuntimeException ire){
		}
	}

	private Content createDummyContentFromUrl(String contentUrl){
		Content content = new Content();
		content.setProvider(ccp.getName());
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

