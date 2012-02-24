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


import edu.emory.mathcs.backport.java.util.Collections;

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

	
	public void addToFolder(ObjectId arg0, boolean arg1) {
		// TODO Auto-generated method stub
	}

	public List<Folder> getParents() {		
		return parents;
	}

	public List<String> getPaths() {
		// TODO Auto-generated method stub
		return null;
	}

	public FileableCmisObject move(ObjectId arg0, ObjectId arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeFromFolder(ObjectId arg0) {
		// TODO Auto-generated method stub

	}

	public Acl addAcl(List<Ace> arg0, AclPropagation arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Acl applyAcl(List<Ace> arg0, List<Ace> arg1, AclPropagation arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	public void applyPolicy(ObjectId... arg0) {
		// TODO Auto-generated method stub

	}

	public void delete(boolean arg0) {
		// TODO Auto-generated method stub

	}

	public Acl getAcl() {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T getAdapter(Class<T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public AllowableActions getAllowableActions() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<CmisExtensionElement> getExtensions(ExtensionLevel arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Policy> getPolicies() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getRefreshTimestamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<Relationship> getRelationships() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Rendition> getRenditions() {
		// TODO Auto-generated method stub
		return null;
	}

	public TransientCmisObject getTransientObject() {
		// TODO Auto-generated method stub
		return null;
	}

	public void refresh() {
		// TODO Auto-generated method stub

	}

	public void refreshIfOld(long arg0) {
		// TODO Auto-generated method stub

	}

	public Acl removeAcl(List<Ace> arg0, AclPropagation arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public void removePolicy(ObjectId... arg0) {
		// TODO Auto-generated method stub

	}

	public CmisObject updateProperties(Map<String, ?> arg0) {
		// TODO Auto-generated method stub
		return this;
	}

	public ObjectId updateProperties(Map<String, ?> arg0, boolean arg1) {
		return null;
	}

	public String getId() {
		List<String> value = (List<String>)getPropertyValue("cmis:objectId");
		return value.get(0);
	}

	public ObjectType getBaseType() {
		return new MockDocumentBaseType();
	}

	public BaseTypeId getBaseTypeId() {
		return BaseTypeId.CMIS_DOCUMENT;
	}

	public String getChangeToken() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCreatedBy() {
		// TODO Auto-generated method stub
		return null;
	}

	public GregorianCalendar getCreationDate() {
		// TODO Auto-generated method stub
		return null;
	}

	public GregorianCalendar getLastModificationDate() {		
		return new GregorianCalendar();
	}

	public String getLastModifiedBy() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		List<String> value = (List<String>)getPropertyValue("cmis:name");
		if (value!=null && !value.isEmpty()){
			return value.get(0);
		}else{
			return null;
		}
	}

	public List<Property<?>> getProperties() {
		return propertyList;
	}

	public Property<?> getProperty(String propId) {
		for (Property<?> prop : propertyList) {
			if (propId.equalsIgnoreCase(prop.getId())){
				return prop;
			}
		}
		return null;
	}

	public Object getPropertyValue(String propId) {
		for (Property<?> prop : propertyList) {
			if (propId.equalsIgnoreCase(prop.getId())){
				return prop.getValue();
			}
		}
		return null;
	}

	public ObjectType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCheckinComment() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getContentStreamFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getContentStreamId() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getContentStreamLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getContentStreamMimeType() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getVersionLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getVersionSeriesCheckedOutBy() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getVersionSeriesCheckedOutId() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getVersionSeriesId() {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean isImmutable() {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean isLatestMajorVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean isLatestVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean isMajorVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean isVersionSeriesCheckedOut() {
		// TODO Auto-generated method stub
		return null;
	}

	public void cancelCheckOut() {
		// TODO Auto-generated method stub

	}

	public ObjectId checkIn(boolean arg0, Map<String, ?> arg1,
			ContentStream arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	public ObjectId checkIn(boolean arg0, Map<String, ?> arg1,
			ContentStream arg2, String arg3, List<Policy> arg4, List<Ace> arg5,
			List<Ace> arg6) {
		// TODO Auto-generated method stub
		return null;
	}

	public ObjectId checkOut() {
		return new ObjectIdImpl(checkOutId);		
	}

	public Document copy(ObjectId arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Document copy(ObjectId arg0, Map<String, ?> arg1,
			VersioningState arg2, List<Policy> arg3, List<Ace> arg4,
			List<Ace> arg5, OperationContext arg6) {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteAllVersions() {
		// TODO Auto-generated method stub

	}

	public Document deleteContentStream() {
		// TODO Auto-generated method stub
		return null;
	}

	public ObjectId deleteContentStream(boolean arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Document> getAllVersions() {
		return versions;
	}

	public List<Document> getAllVersions(OperationContext arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public ContentStream getContentStream() {
		return contentStream;
	}

	public ContentStream getContentStream(String arg0) {
		return contentStream;
	}

	public Document getObjectOfLatestVersion(boolean arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Document getObjectOfLatestVersion(boolean arg0, OperationContext arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public TransientDocument getTransientDocument() {
		// TODO Auto-generated method stub
		return null;
	}

	public Document setContentStream(ContentStream cs, boolean arg1) {
		this.contentStream = cs;
		return this;
	}

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
