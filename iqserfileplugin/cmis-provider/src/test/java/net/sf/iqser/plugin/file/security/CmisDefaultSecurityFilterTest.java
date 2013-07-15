package net.sf.iqser.plugin.file.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import junit.framework.TestCase;
import net.sf.iqser.plugin.file.mock.cmis.MockAllowableActions;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.commons.enums.Action;
import org.easymock.EasyMock;

import com.iqser.core.model.Content;
import com.iqser.core.plugin.security.IQserSecurityException;

@SuppressWarnings({"unused", "unchecked"})
public class CmisDefaultSecurityFilterTest extends TestCase {

	private CmisDefaultSecurityFilter filter;
	private String user;
	private String password;

	// mock objects
	private SessionFactory mockFactory;
	private Session mockCmisSession;

	protected void setUp() throws Exception {

		mockFactory = EasyMock.createMock(SessionFactory.class);
		mockCmisSession = EasyMock.createMock(Session.class);

		Map<String, String> repositoryNameToIdMap = new HashMap<String, String>();
		repositoryNameToIdMap.put("Shared Documents", "id1");

		Properties initProp = new Properties();
		filter = new CmisDefaultSecurityFilter(mockFactory, initProp, repositoryNameToIdMap);

		user = "superadmin";
		password = "secret";
	}

	public void testCanRead() throws IQserSecurityException {
		// "http://cmis/repositoryName/basicType#cmisObjectId"
		Content content = new Content();
		content.setContentUrl("cmis/Shared Documents/basicType#100-2");

		// expected behavior
		EasyMock.expect(mockFactory.createSession(EasyMock.anyObject(HashMap.class))).andReturn(mockCmisSession);
		CmisObject cmisObject = EasyMock.createMock(CmisObject.class);
		EasyMock.expect(mockCmisSession.getObject("100-2")).andReturn(cmisObject);

		// register behavior
		EasyMock.replay(mockFactory, mockCmisSession);

		// Todo: mock or implement TestServiceLocator that returns a (mocked)
		// RepositoryReader
		// boolean canRead = filter.canRead(user, password,
		// content.getContentId());

		// verify
		// EasyMock.verify();

		// assertTrue(canRead);
	}

	public void testCanEdit() throws IQserSecurityException {
		// "http://cmis/repositoryName/basicType#cmisObjectId"
		Content content = new Content();
		content.setContentUrl("cmis/Shared Documents/basicType#100-2");

		// expected behavior
		EasyMock.expect(mockFactory.createSession(EasyMock.anyObject(HashMap.class))).andReturn(mockCmisSession);
		CmisObject mockCmisObject = EasyMock.createMock(CmisObject.class);
		EasyMock.expect(mockCmisSession.getObject("100-2")).andReturn(mockCmisObject);

		Set<Action> actions = new HashSet<Action>();
		actions.add(Action.CAN_UPDATE_PROPERTIES);
		MockAllowableActions myActions = new MockAllowableActions(actions);
		EasyMock.expect(mockCmisObject.getAllowableActions()).andReturn(myActions);

		// register behavior
		EasyMock.replay(mockFactory, mockCmisSession, mockCmisObject);

		// Todo: mock or implement TestServiceLocator that returns a (mocked)
		// RepositoryReader
		// boolean canEdit = filter.canEdit(user, password,
		// content.getContentId());

		// verify
		// EasyMock.verify();

		// assertTrue(canEdit);
	}

	public void testCanEditFalse() throws IQserSecurityException {
		// "http://cmis/repositoryName/basicType#cmisObjectId"
		Content content = new Content();
		content.setContentUrl("cmis/Shared Documents/basicType#100-2");

		// expected behavior
		EasyMock.expect(mockFactory.createSession(EasyMock.anyObject(HashMap.class))).andReturn(mockCmisSession);
		CmisObject mockCmisObject = EasyMock.createMock(CmisObject.class);
		EasyMock.expect(mockCmisSession.getObject("100-2")).andReturn(mockCmisObject);

		Set<Action> actions = new HashSet<Action>();
		actions.add(Action.CAN_ADD_OBJECT_TO_FOLDER);
		MockAllowableActions myActions = new MockAllowableActions(actions);
		EasyMock.expect(mockCmisObject.getAllowableActions()).andReturn(myActions);

		// register behavior
		EasyMock.replay(mockFactory, mockCmisSession, mockCmisObject);

		// Todo: mock or implement TestServiceLocator that returns a (mocked)
		// RepositoryReader
		// boolean canEdit = filter.canEdit(user, password,
		// content.getContentId());

		// verify
		// EasyMock.verify();

		// assertFalse(canEdit);
	}

	private void canExecuteAction(String action, boolean expected, Set<Action> allowableActions)
			throws IQserSecurityException {
		// "http://cmis/repositoryName/basicType#cmisObjectId"
		Content content = new Content();
		content.setContentUrl("cmis/Shared Documents/basicType#100-2");

		// expected behavior
		EasyMock.expect(mockFactory.createSession(EasyMock.anyObject(HashMap.class))).andReturn(mockCmisSession);
		CmisObject mockCmisObject = EasyMock.createMock(CmisObject.class);
		EasyMock.expect(mockCmisSession.getRootFolder()).andReturn(null).anyTimes();
		EasyMock.expect(mockCmisSession.getObject("100-2")).andReturn(mockCmisObject);

		MockAllowableActions myActions = new MockAllowableActions(allowableActions);
		EasyMock.expect(mockCmisObject.getAllowableActions()).andReturn(myActions);

		// register behavior
		EasyMock.replay(mockFactory, mockCmisSession, mockCmisObject);

		// Todo: mock or implement TestServiceLocator that returns a (mocked)
		// RepositoryReader
		// boolean canExecuteAction = filter.canExecuteAction(user, password,
		// action, content.getContentId());

		// verify
		// EasyMock.verify();

		// assertEquals(expected, canExecuteAction);
	}

}
