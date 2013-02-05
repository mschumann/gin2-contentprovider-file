package net.sf.iqser.plugin.file;

import java.util.Collection;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.log4j.PropertyConfigurator;

import com.iqser.core.exception.IQserException;
import com.iqser.core.model.Content;

@SuppressWarnings("unchecked")
public class CmisContentProviderPerformActionTestIntegration extends TestCase {

	CmisContentProvider ccp;

	private Folder testFolder;
	private String CmisTestFolderId;

	private Session cmisSession;


	@Override
	protected void setUp() throws Exception {

		PropertyConfigurator.configure("src/test/resources/log4j.properties");

		ccp = new CmisContentProvider();

		Properties initParams = new Properties();

		// User credentials
		initParams.put("USERNAME","robert.baban");
		initParams.put("PASSWORD","#EDCXSW@1qaz");
		String atompubUrl = "http://alfresco/alfresco/service/cmis";
		// CMIS Atompub Url
		initParams.put("ATOMPUB",atompubUrl);
		//authentication - Standard or NTLM
		initParams.put("AUTHENTICATION_PROVIDER_CLASS","NTLM");
		//repository
		initParams.put("REPOSITORY","[Shared Documents]");

		ccp.setInitParams(initParams);
		ccp.setName("net.sf.iqser.plugin.file");
		ccp.setInitParams(initParams);
		ccp.init();

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

	private Session createSession(){
		Collection<Repository> repoList = ccp.getRepositories();
		for (Repository repository : repoList) {
			return repository.createSession();
		}
		return null;
	}

	private Content createDummyContentFromUrl(String contentUrl){
		Content content = new Content();
		content.setProvider(ccp.getName());
		content.setType("CMIS_DOCUMENT");
		content.setContentUrl(contentUrl);

		return content;
	}


}

