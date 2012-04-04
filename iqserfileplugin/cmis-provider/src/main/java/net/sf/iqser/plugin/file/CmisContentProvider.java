package net.sf.iqser.plugin.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.iqser.plugin.file.parser.FileParser;
import net.sf.iqser.plugin.file.parser.FileParserException;
import net.sf.iqser.plugin.file.parser.FileParserFactory;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.bindings.CmisBindingFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.PropertyType;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.iqser.core.exception.IQserException;
import com.iqser.core.exception.IQserRuntimeException;
import com.iqser.core.model.Attribute;
import com.iqser.core.model.Content;
import com.iqser.core.model.Parameter;
import com.iqser.core.plugin.provider.AbstractContentProvider;

/**
 * 
 * This module contains a ContentProvider implementation to connect CMIS ECM to
 * the iQser GIN Platform.
 * 
 * The content URL for a CMIS object is: http://cmis/repositoryName/basicType#ID
 * 
 * where - repositoryName is the name of the CMIS repository - basicType is one
 * of cmis:folder or cmis:document - ID - is the CMIS objectID For example:
 * http://cmis/repositoryName/cmis:document#14-512,
 * http://cmis/repositoryName/cmis:folder#10
 * 
 * @author robert.baban
 * @modified sebastian.danninger
 * 
 */
public class CmisContentProvider extends AbstractContentProvider {

	/**
	 * CMIS Document type.
	 */
	public static final String CMIS_DOCUMENT_TYPE = "CMIS_DOCUMENT";
	/**
	 * CMIS Folder type.
	 */
	public static final String CMIS_FOLDER_TYPE = "CMIS_FOLDER";

	/**
	 * delete action.
	 */
	public static final String ACTION_DELETE = "Delete";
	/**
	 * update action.
	 */
	public static final String ACTION_UPDATE = "Update";
	/**
	 * checkout action.
	 */
	public static final String ACTION_CHECK_IN = "CheckOut";
	/**
	 * checkin action.
	 */
	public static final String ACTION_CHECK_OUT = "CheckIn";

	private static Logger logger = Logger.getLogger(CmisContentProvider.class);

	/**
	 * a collection of CMIS repositories.
	 */
	private Collection<Repository> repositories = new ArrayList<Repository>();

	/**
	 * map for content type---custom name for content type.
	 */
	private Map<String, String> contentTypeMappings = new HashMap<String, String>();

	/**
	 * map for new attribute---for replacing the name of the attributes.
	 */
	private Map<String, String> attributeMappings = new HashMap<String, String>();

	/**
	 * a collection of new key attributes.
	 */
	private Collection<String> keyAttributeNames = new ArrayList<String>();

	/**
	 * last synchronization time.
	 */
	private long lastSynchTime = 0;

	/**
	 * Initialization method.
	 */
	@Override
	public void init() {

		repositories = new ArrayList<Repository>();

		Properties initParams = getInitParams();

		// key attributes
		String keyAttrInitParam = initParams.getProperty("KEY-ATTRIBUTES");
		keyAttributeNames = CmisUtils.parseInitParam(keyAttrInitParam);
		// attribute-mapping
		String attrMappingsInitParam = initParams.getProperty("ATTRIBUTE-MAPPINGS");
		attributeMappings = CmisUtils.parseAttributesMappings(attrMappingsInitParam);
		// content-type-mappings
		String attrContentTypeMappings = initParams.getProperty("CONTENT-TYPE-MAPPINGS");
		contentTypeMappings = CmisUtils.parseAttributesMappings(attrContentTypeMappings);

		// Default factory implementation of client runtime.
		SessionFactory sessionFactory = SessionFactoryImpl.newInstance();

		Map<String, String> cmisParameters = new HashMap<String, String>();

		// User credentials
		cmisParameters.put(SessionParameter.USER, initParams.getProperty("USERNAME"));
		cmisParameters.put(SessionParameter.PASSWORD, initParams.getProperty("PASSWORD"));
		
		// bind to Atompub
		cmisParameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());

		
		// CMIS Atompub Url
		cmisParameters.put(SessionParameter.ATOMPUB_URL, initParams.getProperty("ATOMPUB"));
		

		// authentication - Standard or NTLM
		String auth = initParams.getProperty("AUTHENTICATION_PROVIDER_CLASS");
		if ("NTLM".equalsIgnoreCase(auth)) {
			cmisParameters.put(SessionParameter.AUTHENTICATION_PROVIDER_CLASS,
					CmisBindingFactory.NTLM_AUTHENTICATION_PROVIDER);
		} else {
			cmisParameters.put(SessionParameter.AUTHENTICATION_PROVIDER_CLASS,
					CmisBindingFactory.STANDARD_AUTHENTICATION_PROVIDER);
		}

		// decide which repositories to synch, default value is the first
		// repository
		String repositoryInitParam = initParams.getProperty("REPOSITORY");
		List<String> repositoryNames = CmisUtils.parseInitParam(repositoryInitParam);

		if (repositoryNames.isEmpty()) {
			// default the first repository is considered
			List<Repository> repoList = sessionFactory.getRepositories(cmisParameters);
			if (!repoList.isEmpty()) {
				this.repositories.add(repoList.get(0));
			}
		} else {
			// obtain all repositories and filter the ones that are present in
			// configuration file
			List<Repository> repoList = sessionFactory.getRepositories(cmisParameters);
			for (Repository repository : repoList) {
				if (repositoryNames.contains(repository.getName())) {
					this.repositories.add(repository);
				}
			}
		}

		// cmisParameters.put(SessionParameter.REPOSITORY_ID,
		// "db9d2cfc-2a84-4eee-b751-0aac19b50d0b");
		// this.repositories.add(sessionFactory.getRepositories(cmisParameters).get(0));

	}

	/**
	 * Destroy method.
	 */
	@Override
	public void destroy() {
		repositories = null;
	}

	/**
	 * If content type is CMIS_DOCUMENT will return the binary content of the
	 * document, if the document has associated a binary content.
	 * 
	 * Otherwise the method will return null
	 * 
	 * @param content
	 *            the content
	 * @return the binary data for a content.
	 */
	@Override
	public byte[] getBinaryData(Content content) {

		byte[] binaryData = null;

		if (isDocument(content)) {
			// if content type is CMIS_DOCUMENT and has attribute hasStream true
			// stream then return the content stream
			Attribute attr = content.getAttributeByName("hasContentStream");
			if ("true".equalsIgnoreCase(attr.getValue())) {
				// determine repository
				Repository repo = getRepository(content.getContentUrl());
				String objectId = content.getAttributeByName("objectId").getValue();

				try {
					// open session on repository
					logger.info("Repository name=" + repo.getName() + " objectId=" + objectId);
					Session s = repo.createSession();
					logger.info(s);
					Document doc = (Document) s.getObject(objectId);

					ContentStream cs = doc.getContentStream();
					binaryData = IOUtils.toByteArray(cs.getStream());
				} catch (IOException e) {
					throw new IQserRuntimeException(e);
				}
			}
		}

		return binaryData;
	}

	/**
	 * Performs cleaning. Deletes the content objects from the object graph if
	 * the corresponding objects are no longer on the CMS system.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void doHousekeeping() {
		Collection<Content> existingContents;
		try {
			existingContents = getExistingContents();
		} catch (IQserException e) {
			throw new IQserRuntimeException(e);
		}

		for (Content content : existingContents) {
			if (!contentExistsOnSource(content)) {
				try {
					removeContent(content.getContentUrl());
				} catch (IQserException e) {
					logger.error("doHousekeeping() - Could not remove content " + content.getContentUrl(), e);
				}
			}
		}

	}

	/**
	 * Checks if the content is present in the CMS system.
	 * 
	 * @param content
	 *            the content
	 * @return true if content exists on CMS, false otherwise
	 */
	protected boolean contentExistsOnSource(Content content) {
		Repository repo = getRepository(content.getContentUrl());
		String objectID = getObjectID(content.getContentUrl());

		Session session = repo.createSession();
		CmisObject cmisObject = null;
		try {
			cmisObject = session.getObject(objectID);
		} catch (CmisObjectNotFoundException confe) {
			// ignore it
			cmisObject = null;
		}

		return cmisObject != null;
	}

	/**
	 * Performs synchronization.
	 */
	@Override
	public void doSynchronization() {

		long startLastSynchTime = new Date().getTime();

		for (Repository repo : repositories) {
			Session session = repo.createSession();

			// getFolders and getDocuments
			Folder root = session.getRootFolder();

			doSynchFolder(repo, root, null);
		}

		lastSynchTime = startLastSynchTime;
	}

	/**
	 * Synchronize one folder.
	 * 
	 * @param repo
	 *            CMIS repository
	 * @param root
	 *            folder
	 * @param parent
	 *            parent folder
	 */
	protected void doSynchFolder(Repository repo, Folder root, Folder parent) {

		// synch Folder
		Content folderContent = createFolderContent(repo, root, parent);
		boolean isExistingContent;
		try {
			isExistingContent = isExistingContent(folderContent.getContentUrl());
			if (isExistingContent) {
				try {
					updateContent(folderContent);
				} catch (Throwable t) {
					// Make sure to catch everthing to continue
					// with next Content
					logger.error("Could not update content.", t);
				}
			} else {
				try {
					addContent(folderContent);
				} catch (Throwable t) {
					// Make sure to catch everthing to continue
					// with next Content
					logger.error("Could not update content.", t);
				}
			}
		} catch (IQserException e) {
			logger.error("Exception for content " + folderContent.getContentUrl(), e);
		}

		// synch documents in folder
		ItemIterable<CmisObject> children = root.getChildren();
		for (CmisObject o : children) {
			if (o.getBaseTypeId() == BaseTypeId.CMIS_FOLDER) {
				doSynchFolder(repo, (Folder) o, root);
			} else if (o.getBaseTypeId() == BaseTypeId.CMIS_DOCUMENT) {
				// synch Document
				Document doc = (Document) o;
				// synch all versions
				List<Document> versions = doc.getAllVersions();
				boolean found = true;
				for (Document vdoc : versions) {
					if (vdoc.getId().equals(doc.getId())) {
						found = true;
						break;
					}
				}
				if (!found) {
					versions.add(doc);
				}

				for (Document verDoc : versions) {
					try {
						long lastModDate = doc.getLastModificationDate().getTime().getTime();
						if (lastModDate >= lastSynchTime) {
							Content docContent = createDocumentContent(repo, verDoc);
							isExistingContent = isExistingContent(docContent.getContentUrl());
							if (isExistingContent) {
								updateContent(docContent);
							} else {
								addContent(docContent);
							}
						}
					} catch (IQserException e) {
						String url = this.createURL(repo.getName(), "CMIS_DOCUMENT", verDoc.getId());
						logger.error("Exception in doSynch for document " + url, e);
					}
				}
			}
		}
	}

	/**
	 * Returns the available actions for the given content. The content URL has
	 * the following pattern: http://cmis/repositoryName/basicType#ID
	 * 
	 * @param content
	 *            the content
	 * @return a collection of string representing action names
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Collection getActions(Content content) {
		String[] actions = null;
		if (isFolder(content)) {
			actions = new String[] { ACTION_DELETE, ACTION_UPDATE };
		} else if (isDocument(content)) {
			actions = new String[] { ACTION_DELETE, ACTION_UPDATE, ACTION_CHECK_OUT, ACTION_CHECK_IN };
		} else {
			actions = new String[0];
		}
		return Arrays.asList(actions);
	}

	/**
	 * Creates a content object based on a content URL. The content URL has the
	 * following pattern: http://cmis/repositoryName/basicType#ID
	 * 
	 * @param contentUrl
	 *            content URL
	 * @return a content object
	 */
	@Override
	public Content getContent(String contentUrl) {
		Repository repository = getRepository(contentUrl);
		String objectId = getObjectID(contentUrl);

		logger.info("Repository name=" + repository.getName() + " objectId=" + objectId);

		Session session = repository.createSession();

		return getContent(session, contentUrl);
	}

	private Content getContent(Session session, String contentUrl) {

		Repository repository = getRepository(contentUrl);
		String objectId = getObjectID(contentUrl);

		CmisObject object = session.getObject(objectId);

		logger.info("object name=" + object.getName());

		BaseTypeId baseType = object.getBaseTypeId();

		Content content = null;
		if (baseType == BaseTypeId.CMIS_FOLDER) {
			content = createFolderContent(repository, (Folder) object, null);
		} else { // baseType == BaseTypeId.CMIS_DOCUMENT
			content = createDocumentContent(repository, (Document) object);
		}

		content.setContentUrl(contentUrl);
		content.setProvider(this.getId());

		// change attribute name according to mappings
		changeAttributeName(content);

		// set key attributes
		setKeyAttributes(content);

		// change type
		String userType = contentTypeMappings.get(content.getType());
		if (userType != null) {
			content.setType(userType);
		}

		return content;
	}

	private void setKeyAttributes(Content content) {
		for (Attribute attr : content.getAttributes()) {
			if (this.keyAttributeNames.contains(attr.getName())) {
				attr.setKey(true);
			}
		}
	}

	private void changeAttributeName(Content content) {
		for (Attribute attr : content.getAttributes()) {
			String newName = this.attributeMappings.get(attr.getName());
			if (newName != null) {
				attr.setName(newName);
			}
		}
	}

	private String findAttributeName(String name) {
		// if attribute name has been changed using init param find the new name
		String newAttrName = attributeMappings.get(name);
		if (newAttrName == null) {
			newAttrName = name;
		}
		return newAttrName;
	}

	/**
	 * Crates a content object from an InputStream. Not implemented.
	 * 
	 * @param inputStream
	 *            the inputStream
	 * @return a content object
	 */
	@Override
	public Content getContent(InputStream inputStream) {
		throw new RuntimeException("NOT IMPLEMENTED");
	}

	/**
	 * Performs an action of the given content.
	 * 
	 * @param action
	 *            the action
	 * @param content
	 *            the content
	 */
	@Override
	public void performAction(String action, Content content) {
		@SuppressWarnings("rawtypes")
		Collection availableActions = getActions(content);
		if (availableActions.contains(action)) {
			if (ACTION_DELETE.equalsIgnoreCase(action)) {
				performActionDelete(content);
			} else if (ACTION_UPDATE.equalsIgnoreCase(action)) {
				performActionUpdate(content);
			} else if (ACTION_CHECK_OUT.equalsIgnoreCase(action)) {
				performActionCheckOut(content);
			} else { // ACTION_CHECK_IN
				performActionCheckIn(content);
			}
		} else {
			throw new IQserRuntimeException("Action " + action + " not available for content "
					+ content.getContentUrl());
		}

	}

	/**
	 * Perform Update Action.
	 * 
	 * @param content
	 *            the content
	 */
	protected void performActionUpdate(Content content) {
		Repository repo = getRepository(content.getContentUrl());
		String objectID = getObjectID(content.getContentUrl());

		Session session = repo.createSession();

		CmisObject cmisObject = session.getObject(objectID);
		Map<String, String> propMap = determineCMISUpdatableProperties(cmisObject, content);
		cmisObject.updateProperties(propMap);
		try {
			this.updateContent(content);
		} catch (Throwable t) {
			// Make sure to catch everthing to continue
			// with next Content
			logger.error("Could not update content.", t);
		}
	}

	/**
	 * Perform Delete Action.
	 * 
	 * @param content
	 *            the content
	 */
	protected void performActionDelete(Content content) {
		Repository repo = getRepository(content.getContentUrl());
		String objectID = getObjectID(content.getContentUrl());

		Session session = repo.createSession();

		CmisObject cmisObject = session.getObject(objectID);

		// delete current version
		boolean allVersions = false;
		cmisObject.delete(allVersions);

		try {
			this.removeContent(content.getContentUrl());
		} catch (IQserException e) {
			throw new IQserRuntimeException(e);
		}
	}

	/**
	 * Perform CheckOut Action.
	 * 
	 * @param content
	 *            the content
	 */
	protected void performActionCheckOut(Content content) {
		Repository repo = getRepository(content.getContentUrl());
		String objectID = getObjectID(content.getContentUrl());

		Session session = repo.createSession();

		CmisObject cmisObject = session.getObject(objectID);

		if (BaseTypeId.CMIS_DOCUMENT == cmisObject.getBaseType().getBaseTypeId()) {
			Document doc = (Document) cmisObject;
			ObjectId pwcId = doc.checkOut();

			// create new content for pwc - the client will be able to find the
			// pwc by performing a query
			// cmis:isLatestVersion true and cmis:isLatestMajorVersion true
			String newContentUrl = createURL(repo.getName(), CmisContentProvider.CMIS_DOCUMENT_TYPE, pwcId.getId());
			Content newContent = this.getContent(session, newContentUrl);
			try {
				this.addContent(newContent);
			} catch (Throwable t) {
				// Make sure to catch everthing to continue
				// with next Content
				logger.error("Could not add content.", t);
			}
		}
	}

	/**
	 * Perform CheckIn Action.
	 * 
	 * @param content
	 *            the content
	 */
	protected void performActionCheckIn(Content content) {
		Repository repo = getRepository(content.getContentUrl());
		String objectID = getObjectID(content.getContentUrl());

		Session session = repo.createSession();

		CmisObject cmisObject = session.getObject(objectID);

		if (BaseTypeId.CMIS_DOCUMENT == cmisObject.getBaseType().getBaseTypeId()) {
			Document doc = (Document) cmisObject;
			boolean major = true;
			// update properties - content stream is not updated
			Map<String, ?> properties = determineCMISUpdatableProperties(cmisObject, content);
			String comments = "";
			if (content.getAttributeByName("cmis:checkinComment") != null) {
				comments = content.getAttributeByName("cmis:checkinComment").getValue();
			}

			// ObjectId newId = doc.checkIn(major, properties, null, comments);
			doc.checkIn(major, properties, null, comments);
		}
	}

	/**
	 * Returns a list of attributes that CMIS properties.
	 * 
	 * @param cmisObj
	 *            the CMIS object
	 * @param content
	 *            the content
	 * @return a list of properties
	 */
	protected Map<String, String> determineCMISUpdatableProperties(CmisObject cmisObj, Content content) {
		Map<String, String> properties = new HashMap<String, String>();

		properties.put("cmis:name", cmisObj.getProperty("cmis:name").getValueAsString());

		List<Property<?>> cmisPropList = cmisObj.getProperties();
		for (Property<?> property : cmisPropList) {
			Updatability update = property.getDefinition().getUpdatability();
			if (Updatability.READWRITE == update) {
				// find attr name considering also attr mappings
				String attrName = findAttributeName(property.getId());
				Attribute attr = content.getAttributeByName(attrName);
				if (attr != null) {
					properties.put(property.getId(), attr.getValue());
				}
			}
		}

		logger.info("properties=" + properties);

		return properties;
	}

	/**
	 * Creates a Content object for a CMIS Document.
	 * 
	 * @param repository
	 *            CMIS repository
	 * @param doc
	 *            CMIS document
	 * @return a content object
	 */
	protected Content createDocumentContent(Repository repository, Document doc) {
		Content content = new Content();
		content.setContentUrl(createURL(repository.getName(), CMIS_DOCUMENT_TYPE, doc.getId()));
		content.setProvider(this.getId());
		content.setType(CMIS_DOCUMENT_TYPE);

		handleProperties(doc, content);

		content.addAttribute(new Attribute("repository", repository.getName(), Attribute.ATTRIBUTE_TYPE_TEXT, false));

		List<Folder> parents = doc.getParents();
		if (!parents.isEmpty()) {
			Attribute mva = new Attribute();
			mva.setMultiValue(true);
			mva.setName("parent");
			mva.setType(Attribute.ATTRIBUTE_TYPE_TEXT);
			content.addAttribute(mva);

			for (Folder parentFolder : parents) {
				mva.addValue(parentFolder.getName());
			}
		}

		ContentStream cstream = doc.getContentStream();
		if (cstream != null) {
			String fileName = cstream.getFileName();
			FileParser fileParser = FileParserFactory.getInstance().getFileParser(fileName);

			InputStream inputStream = cstream.getStream();

			// using this line gets an IOException, stream already closed
			// Content fileContent = fileParser.getContent(fileName,
			// inputStream));
			// workaround - copy stream content
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				IOUtils.copy(inputStream, baos);
				Content fileContent = fileParser.getContent(fileName, new ByteArrayInputStream(baos.toByteArray()));
				content.getAttributes().addAll(fileContent.getAttributes());

				// fulltext
				if (!StringUtils.isEmpty(fileContent.getFulltext())) {
					content.setFulltext(fileContent.getFulltext());
				}

			} catch (FileParserException e) {
				logger.error("Error while parsing file content for document" + doc.getName(), e);
			} catch (IOException e) {
				logger.error("Error while parsing file content for document" + doc.getName(), e);
			}
			content.addAttribute(new Attribute("hasContentStream", "true", Attribute.ATTRIBUTE_TYPE_BOOLEAN, true));
		}

		content.setModificationDate(doc.getLastModificationDate().getTimeInMillis());

		return content;
	}

	private void handleProperties(CmisObject doc, Content content) {
		for (Property<?> prop : doc.getProperties()) {
			String name = prop.getId();
			String value = prop.getValueAsString();
			// check for non empty attributes
			if (!StringUtils.isEmpty(value)) {
				int type = Attribute.ATTRIBUTE_TYPE_TEXT;
				if (PropertyType.BOOLEAN == prop.getType()) {
					type = Attribute.ATTRIBUTE_TYPE_BOOLEAN;
				} else if (PropertyType.DATETIME == prop.getType()) {
					type = Attribute.ATTRIBUTE_TYPE_DATE;
				} else if (PropertyType.INTEGER == prop.getType() || PropertyType.DECIMAL == prop.getType()) {
					type = Attribute.ATTRIBUTE_TYPE_NUMBER;
				} else {
					type = Attribute.ATTRIBUTE_TYPE_TEXT;
				}

				content.addAttribute(new Attribute(name, value, type, true));
			}
		}
	}

	/**
	 * Creates a Content object for a CMIS Folder.
	 * 
	 * @param repository
	 *            CMIS repository
	 * @param folder
	 *            CMIS folder
	 * @param parentFolder
	 *            CMIS parent folder
	 * @return a content object
	 */
	protected Content createFolderContent(Repository repository, Folder folder, Folder parentFolder) {
		Content content = new Content();
		content.setContentUrl(createURL(repository.getName(), CMIS_FOLDER_TYPE, folder.getId()));
		content.setProvider(this.getId());
		content.setType(CMIS_FOLDER_TYPE);

		handleProperties(folder, content);

		content.addAttribute(new Attribute("repository", repository.getName(), Attribute.ATTRIBUTE_TYPE_TEXT, false));

		if (parentFolder != null) {
			content.addAttribute(new Attribute("parent", parentFolder.getName(), Attribute.ATTRIBUTE_TYPE_TEXT, false));
		}

		return content;
	}

	private Repository getRepository(String contentUrl) {
		String repoName = CmisUtils.getRepository(contentUrl);
		if (repoName != null) {
			for (Repository repo : repositories) {
				if (repoName.equalsIgnoreCase(repo.getName())) {
					return repo;
				}
			}
		}
		return null;
	}

	private String getObjectID(String contentUrl) {
		return CmisUtils.getObjectID(contentUrl);
	}

	private String createURL(String repoName, String cmisBaseType, String oid) {
		// http://cmis/repositoryName/basicType#ID
		String v = CMIS_DOCUMENT_TYPE.equalsIgnoreCase(cmisBaseType) ? "cmis:document" : "cmis:folder";
		return "http://cmis/" + repoName + "/" + v + "#" + oid;
	}

	/**
	 * Getter method for repositories.
	 * 
	 * @return a collection of repositories
	 */
	public Collection<Repository> getRepositories() {
		return repositories;
	}

	private boolean isFolder(Content content) {
		String type = content.getType();
		return type.equals(CMIS_FOLDER_TYPE) || type.equals(contentTypeMappings.get(CMIS_FOLDER_TYPE));
	}

	private boolean isDocument(Content content) {
		String type = content.getType();
		return type.equals(CMIS_DOCUMENT_TYPE) || type.equals(contentTypeMappings.get(CMIS_DOCUMENT_TYPE));
	}

	@Override
	public void performAction(String arg0, Collection<Parameter> arg1, Content arg2) {
		// TODO Auto-generated method stub

	}

}
