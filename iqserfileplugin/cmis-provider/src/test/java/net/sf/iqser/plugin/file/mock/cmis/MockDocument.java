package net.sf.iqser.plugin.file.mock.cmis;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.FileableCmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Policy;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Relationship;
import org.apache.chemistry.opencmis.client.api.Rendition;
import org.apache.chemistry.opencmis.client.api.TransientCmisObject;
import org.apache.chemistry.opencmis.client.api.TransientDocument;
import org.apache.chemistry.opencmis.client.runtime.ObjectIdImpl;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.AllowableActions;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.AclPropagation;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.ExtensionLevel;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;

@SuppressWarnings("unchecked")
public class MockDocument implements Document, CmisObject {

	ContentStream contentStream;
	List<Property<?>> propertyList = new ArrayList<Property<?>>();
	List<Folder> parents = new ArrayList<Folder>();
	List<Document> versions = new ArrayList<Document>();

	String checkOutId;

	public MockDocument(){
		versions.add(this);
	}

	//dummy methods
	public void dummyAddParent(Folder folder){
		parents.add(folder);
	}
	//dummy methods
	public void dummyAddVersion(Document doc){
		versions.add(doc);
	}

	public void dummySetCheckOutId(String checkOutId){
		this.checkOutId = checkOutId;
	}


	@Override
	public void addToFolder(ObjectId arg0, boolean arg1) {
		
	}

	@Override
	public List<Folder> getParents() {
		return parents;
	}

	@Override
	public List<String> getPaths() {
		
		return null;
	}

	@Override
	public FileableCmisObject move(ObjectId arg0, ObjectId arg1) {
		
		return null;
	}

	@Override
	public void removeFromFolder(ObjectId arg0) {
		

	}

	@Override
	public Acl addAcl(List<Ace> arg0, AclPropagation arg1) {
		
		return null;
	}

	@Override
	public Acl applyAcl(List<Ace> arg0, List<Ace> arg1, AclPropagation arg2) {
		
		return null;
	}

	@Override
	public void applyPolicy(ObjectId... arg0) {
		

	}

	@Override
	public void delete(boolean arg0) {
		

	}

	@Override
	public Acl getAcl() {
		
		return null;
	}

	@Override
	public <T> T getAdapter(Class<T> arg0) {
		
		return null;
	}

	@Override
	public AllowableActions getAllowableActions() {
		
		return null;
	}

	@Override
	public List<CmisExtensionElement> getExtensions(ExtensionLevel arg0) {
		
		return null;
	}

	@Override
	public List<Policy> getPolicies() {
		
		return null;
	}

	@Override
	public long getRefreshTimestamp() {
		
		return 0;
	}

	@Override
	public List<Relationship> getRelationships() {
		
		return null;
	}

	@Override
	public List<Rendition> getRenditions() {
		
		return null;
	}

	@Override
	public TransientCmisObject getTransientObject() {
		
		return null;
	}

	@Override
	public void refresh() {
		

	}

	@Override
	public void refreshIfOld(long arg0) {
		

	}

	@Override
	public Acl removeAcl(List<Ace> arg0, AclPropagation arg1) {
		
		return null;
	}

	@Override
	public void removePolicy(ObjectId... arg0) {
		

	}

	@Override
	public CmisObject updateProperties(Map<String, ?> arg0) {
		
		return this;
	}

	@Override
	public ObjectId updateProperties(Map<String, ?> arg0, boolean arg1) {
		return null;
	}

	@Override
	public String getId() {
		List<String> value = (List<String>)getPropertyValue("cmisobjectId");
		return value.get(0);
	}

	@Override
	public ObjectType getBaseType() {
		return new MockDocumentBaseType();
	}

	@Override
	public BaseTypeId getBaseTypeId() {
		return BaseTypeId.CMIS_DOCUMENT;
	}

	@Override
	public String getChangeToken() {
		
		return null;
	}

	@Override
	public String getCreatedBy() {
		
		return null;
	}

	@Override
	public GregorianCalendar getCreationDate() {
		
		return null;
	}

	@Override
	public GregorianCalendar getLastModificationDate() {
		return new GregorianCalendar();
	}

	@Override
	public String getLastModifiedBy() {
		
		return null;
	}

	@Override
	public String getName() {
		List<String> value = (List<String>)getPropertyValue("cmisname");
		if (value!=null && !value.isEmpty()){
			return value.get(0);
		}else{
			return null;
		}
	}

	@Override
	public List<Property<?>> getProperties() {
		return propertyList;
	}

	@Override
	public Property<?> getProperty(String propId) {
		for (Property<?> prop : propertyList) {
			if (propId.equalsIgnoreCase(prop.getId())){
				return prop;
			}
		}
		return null;
	}

	@Override
	public Object getPropertyValue(String propId) {
		for (Property<?> prop : propertyList) {
			if (propId.equalsIgnoreCase(prop.getId())){
				return prop.getValue();
			}
		}
		return null;
	}

	@Override
	public ObjectType getType() {
		
		return null;
	}

	@Override
	public String getCheckinComment() {
		
		return null;
	}

	@Override
	public String getContentStreamFileName() {
		
		return null;
	}

	@Override
	public String getContentStreamId() {
		
		return null;
	}

	@Override
	public long getContentStreamLength() {
		
		return 0;
	}

	@Override
	public String getContentStreamMimeType() {
		
		return null;
	}

	@Override
	public String getVersionLabel() {
		
		return null;
	}

	@Override
	public String getVersionSeriesCheckedOutBy() {
		
		return null;
	}

	@Override
	public String getVersionSeriesCheckedOutId() {
		
		return null;
	}

	@Override
	public String getVersionSeriesId() {
		
		return null;
	}

	@Override
	public Boolean isImmutable() {
		
		return null;
	}

	@Override
	public Boolean isLatestMajorVersion() {
		
		return null;
	}

	@Override
	public Boolean isLatestVersion() {
		
		return null;
	}

	@Override
	public Boolean isMajorVersion() {
		
		return null;
	}

	@Override
	public Boolean isVersionSeriesCheckedOut() {
		
		return null;
	}

	@Override
	public void cancelCheckOut() {
		

	}

	@Override
	public ObjectId checkIn(boolean arg0, Map<String, ?> arg1,
			ContentStream arg2, String arg3) {
		
		return null;
	}

	@Override
	public ObjectId checkIn(boolean arg0, Map<String, ?> arg1,
			ContentStream arg2, String arg3, List<Policy> arg4, List<Ace> arg5,
			List<Ace> arg6) {
		
		return null;
	}

	@Override
	public ObjectId checkOut() {
		return new ObjectIdImpl(checkOutId);
	}

	@Override
	public Document copy(ObjectId arg0) {
		
		return null;
	}

	@Override
	public Document copy(ObjectId arg0, Map<String, ?> arg1,
			VersioningState arg2, List<Policy> arg3, List<Ace> arg4,
			List<Ace> arg5, OperationContext arg6) {
		
		return null;
	}

	@Override
	public void deleteAllVersions() {
		

	}

	@Override
	public Document deleteContentStream() {
		
		return null;
	}

	@Override
	public ObjectId deleteContentStream(boolean arg0) {
		
		return null;
	}

	@Override
	public List<Document> getAllVersions() {
		return versions;
	}

	@Override
	public List<Document> getAllVersions(OperationContext arg0) {
		
		return null;
	}

	@Override
	public ContentStream getContentStream() {
		return contentStream;
	}

	@Override
	public ContentStream getContentStream(String arg0) {
		return contentStream;
	}

	@Override
	public Document getObjectOfLatestVersion(boolean arg0) {
		
		return null;
	}

	@Override
	public Document getObjectOfLatestVersion(boolean arg0, OperationContext arg1) {
		
		return null;
	}

	@Override
	public TransientDocument getTransientDocument() {
		
		return null;
	}

	@Override
	public Document setContentStream(ContentStream cs, boolean arg1) {
		contentStream = cs;
		return this;
	}

	@Override
	public ObjectId setContentStream(ContentStream arg0, boolean arg1,	boolean arg2) {
		return null;
	}

	public FileableCmisObject move(ObjectId oi, ObjectId oi1, OperationContext oc) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public List<Folder> getParents(OperationContext oc) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
