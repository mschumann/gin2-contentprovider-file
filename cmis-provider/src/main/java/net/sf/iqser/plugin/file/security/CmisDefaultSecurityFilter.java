package net.sf.iqser.plugin.file.security;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.sf.iqser.plugin.file.CmisContentProvider;
import net.sf.iqser.plugin.file.CmisUtils;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.bindings.CmisBindingFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.Action;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
import org.apache.log4j.Logger;

import com.iqser.core.exception.IQserException;
import com.iqser.core.model.Content;
import com.iqser.core.model.Result;
import com.iqser.core.plugin.AbstractPlugin;
import com.iqser.core.plugin.security.IQserSecurityException;
import com.iqser.core.plugin.security.SecurityFilter;

/**
 * Default security filter for CmisContentProvider.
 * 
 * Open a CMIS session using given user name and password and see if the user has read, edit rights on the content. If
 * the user has edit rights on the content he can execute actions on the content.
 * 
 * A cache is used to store CMIS sessions.
 * 
 */
public class CmisDefaultSecurityFilter extends AbstractPlugin implements SecurityFilter {

	private static Logger logger = Logger.getLogger(CmisDefaultSecurityFilter.class);

	private SessionFactory sessionFactory;

	private final Map<String, String> repositoryNameToIdMap;

	private final Properties initParams;

	private final LinkedHashMap<String, Session> cache;

	/**
	 * Creates a security filter. Initialization parameters must be set in the configuration file
	 * cmis_security.properties. The configuration file must be placed in package net.sf.iqser.plugin.file.security.
	 * 
	 * It will connect to CMIS server and read repositories name and id.
	 */
	public CmisDefaultSecurityFilter() {
		repositoryNameToIdMap = new HashMap<String, String>();
		initParams = new Properties();

		cache = new LinkedHashMap<String, Session>(100, 0.75f, true);

		init();
	}

	/**
	 * Creates a security filter. It will not connect to the CMIS server. Used mainly for testing purposes.
	 * 
	 * @param sessionFactory
	 *            CMIS session factory
	 * @param initParams
	 *            initialization parameters
	 * @param repositoryNameToIdMap
	 *            map between repository name and repository id
	 */
	public CmisDefaultSecurityFilter(SessionFactory sessionFactory, Properties initParams,
			Map<String, String> repositoryNameToIdMap) {
		this.sessionFactory = sessionFactory;
		this.repositoryNameToIdMap = repositoryNameToIdMap;
		this.initParams = initParams;

		cache = new LinkedHashMap<String, Session>(100, 0.75f, true);

	}

	@Override
	public void init() {
		try {
			// load init params from property file
			InputStream in = getClass().getResourceAsStream("cmis_security.properties");
			if (in != null) {
				initParams.load(in);

				// Default factory implementation of client runtime.
				sessionFactory = SessionFactoryImpl.newInstance();

				// get repository names and ids
				String user = initParams.getProperty("USERNAME");
				String password = initParams.getProperty("PASSWORD");
				HashMap<String, String> cmisParams = createCMISProperties(user, password);

				List<Repository> repoList = sessionFactory.getRepositories(cmisParams);
				for (Repository repository : repoList) {
					String id = repository.getId();
					String name = repository.getName();
					repositoryNameToIdMap.put(name, id);
					logger.info("Found repository name=" + name + " id=" + id);
				}
			} else {
				logger.error("Missing cmis_security.peoperties file");
			}
		} catch (IOException e) {
			logger.error("Cannot read init params from cmis_security.peoperties file", e);
		} catch (CmisBaseException cbe) {
			logger.error("Could not connect to CMIS repository. Init params = " + initParams);
		}
	}

	private HashMap<String, String> createCMISProperties(String user, String password) {
		HashMap<String, String> cmisParams = new HashMap<String, String>();

		// bind to Atompub
		cmisParams.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());

		// CMIS Atompub Url
		cmisParams.put(SessionParameter.ATOMPUB_URL, initParams.getProperty("ATOMPUB"));

		// authentication - Standard or NTLM
		String auth = initParams.getProperty("AUTHENTICATION_PROVIDER_CLASS");
		if ("NTLM".equalsIgnoreCase(auth)) {
			cmisParams.put(SessionParameter.AUTHENTICATION_PROVIDER_CLASS,
					CmisBindingFactory.NTLM_AUTHENTICATION_PROVIDER);
		} else {
			cmisParams.put(SessionParameter.AUTHENTICATION_PROVIDER_CLASS,
					CmisBindingFactory.STANDARD_AUTHENTICATION_PROVIDER);
		}

		// User credentials
		cmisParams.put(SessionParameter.USER, user);
		cmisParams.put(SessionParameter.PASSWORD, password);

		return cmisParams;
	}

	private Session createSession(String user, String password, String repositoryId) {
		Map<String, String> cmisProp = createCMISProperties(user, password);
		cmisProp.put(SessionParameter.REPOSITORY_ID, repositoryId);

		return sessionFactory.createSession(cmisProp);
	}

	/**
	 * Opens a session. If the session is in cache it will reuse it, otherwise it will create a new session
	 * 
	 * @param user
	 *            the user
	 * @param password
	 *            the password
	 * @param repositoryId
	 *            CMIS repository id
	 * @return a Session object for the given user and repository
	 */
	private Session openSession(String user, String password, String repositoryId) {
		// check the session is in the cache
		String key = "session:" + user + repositoryId;
		Session cmisSession = cache.get(key);
		if (cmisSession != null) {
			// if the session is not alive open a new session
			if (!isAlive(cmisSession)) {
				cmisSession = createSession(user, password, repositoryId);
				cache.put(key, cmisSession);
				logger.debug("Creating a new session user=" + user + " repositoryId=" + repositoryId);
			} else {
				logger.debug("Using cached session user=" + user + " repositoryId=" + repositoryId);
			}
		} else {
			// open a new session and place it in cache
			cmisSession = createSession(user, password, repositoryId);
			cache.put(key, cmisSession);
			logger.debug("Creating a new session user=" + user + " repositoryId=" + repositoryId);
		}
		return cmisSession;
	}

	/**
	 * Test if the session is alive, and has not been closed.
	 * 
	 * @param cmisSession
	 *            cmisSession
	 * @return true if the session is alive, false otherwise
	 */
	private boolean isAlive(Session cmisSession) {
		boolean isAlive = true;
		try {
			cmisSession.getRootFolder();
		} catch (CmisBaseException cbe) {
			isAlive = false;
		}
		return isAlive;
	}

	/**
	 * Method that checks read permission.
	 * 
	 * @param user
	 *            the user.
	 * @param password
	 *            the password.
	 * @param content
	 *            the content.
	 * @return true if the user is allowed to read the content, false otherwise.
	 * @throws IQserSecurityException
	 * @see SecurityFilter
	 */
	@Override
	public boolean canRead(String user, String password, long contentId) throws IQserSecurityException {
		return null != getCmisObjectByContentId(user, password, contentId);

	}

	private CmisObject getCmisObjectByContentId(String user, String password, long contentId)
			throws IQserSecurityException {
		Content content = null;
		try {
			content = getContent(contentId);
		} catch (IQserException e) {
			e.printStackTrace();
		}

		if (null == content) {
			return null;
		}

		String cmisObjectId = CmisUtils.getObjectID(content.getContentUrl());
		String repositoryName = CmisUtils.getRepository(content.getContentUrl());
		String repositoryId = repositoryNameToIdMap.get(repositoryName);

		if (repositoryId == null) {
			logger.warn("Could not find repository " + repositoryName);
			return null;
		}

		try {
			Session session = openSession(user, password, repositoryId);
			return session.getObject(cmisObjectId);
		} catch (CmisBaseException cbe) {
			throw new IQserSecurityException(cbe.getMessage());
		}
	}

	/**
	 * Method that checks execute action permission.
	 * 
	 * @param user
	 *            the user.
	 * @param password
	 *            the password.
	 * @param action
	 *            name of the action.
	 * @param content
	 *            the content.
	 * @return true if the user is allowed to execute the action on the given content, false otherwise.
	 * @throws IQserSecurityException
	 * @see SecurityFilter
	 */
	@Override
	public boolean canExecuteAction(String user, String password, String action, long contentId)
			throws IQserSecurityException {

		CmisObject cmisObj = getCmisObjectByContentId(user, password, contentId);
		if (null == cmisObj) {
			return false;
		}

		Set<Action> actions = cmisObj.getAllowableActions().getAllowableActions();

		if (CmisContentProvider.ACTION_UPDATE.equalsIgnoreCase(action)) {
			return actions.contains(Action.CAN_UPDATE_PROPERTIES);
		} else if (CmisContentProvider.ACTION_DELETE.equalsIgnoreCase(action)) {
			return actions.contains(Action.CAN_DELETE_OBJECT);
		} else if (CmisContentProvider.ACTION_CHECK_IN.equalsIgnoreCase(action)) {
			return actions.contains(Action.CAN_CHECK_IN);
		} else if (CmisContentProvider.ACTION_CHECK_OUT.equalsIgnoreCase(action)) {
			return actions.contains(Action.CAN_CHECK_OUT);
		}
		return false;

	}

	@Override
	public Collection<Content> filterReadableContent(String userId, String password, Collection<Content> contentObjects)
			throws IQserSecurityException {

		Iterator<Content> iter = contentObjects.iterator();

		while(iter.hasNext()){
			if (!canRead(userId, password, iter.next().getContentId())){
				iter.remove();
			}
		}

		return contentObjects;
	}

	@Override
	public Collection<Result> filterReadableResult(String userId, String password, Collection<Result> resultObjects)
			throws IQserSecurityException {


		Iterator<Result> iter = resultObjects.iterator();

		while(iter.hasNext()){
			if (!canRead(userId, password, iter.next().getContentId())){
				iter.remove();
			}
		}

		return resultObjects;
	}

}
