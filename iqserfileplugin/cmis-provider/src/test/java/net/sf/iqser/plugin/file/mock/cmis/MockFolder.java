package net.sf.iqser.plugin.file.mock.cmis;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.FileableCmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Policy;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Relationship;
import org.apache.chemistry.opencmis.client.api.Rendition;
import org.apache.chemistry.opencmis.client.api.TransientCmisObject;
import org.apache.chemistry.opencmis.client.api.TransientFolder;
import org.apache.chemistry.opencmis.client.api.Tree;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.AllowableActions;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.AclPropagation;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.ExtensionLevel;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;

public class MockFolder implements Folder, CmisObject {

	List<Property<?>> propertyList = new ArrayList();
	List<CmisObject> childrenList = new ArrayList<CmisObject>();	
	List<Folder> parents = new ArrayList<Folder>();
	
	//dummy methods
	public void dummyAddParent(Folder folder){
		parents.add(folder);
	}	

	public void dummyAddCmisChild(CmisObject child){
		this.childrenList.add(child);
	}	
	
	public void addToFolder(ObjectId objectid, boolean flag) {
		// TODO Auto-generated method stub
	}

	public List<Folder> getParents() {
		return parents;
	}

	public List<String> getPaths() {
		// TODO Auto-generated method stub
		return null;
	}

	public FileableCmisObject move(ObjectId objectid, ObjectId objectid1) {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeFromFolder(ObjectId objectid) {
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

	public void applyPolicy(ObjectId... aobjectid) {
		// TODO Auto-generated method stub

	}

	public void delete(boolean flag) {
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

	public List<CmisExtensionElement> getExtensions(
			ExtensionLevel extensionlevel) {
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

	public void refreshIfOld(long l) {
		// TODO Auto-generated method stub

	}

	public Acl removeAcl(List<Ace> arg0, AclPropagation arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public void removePolicy(ObjectId... aobjectid) {
		// TODO Auto-generated method stub

	}

	public CmisObject updateProperties(Map<String, ?> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public ObjectId updateProperties(Map<String, ?> arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getId() {
		List<String> value = (List<String>)getPropertyValue("cmis:objectId");
		return value.get(0);
	}

	public ObjectType getBaseType() {
		// TODO Auto-generated method stub
		return null;
	}

	public BaseTypeId getBaseTypeId() {		
		return BaseTypeId.CMIS_FOLDER;
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
		// TODO Auto-generated method stub
		return null;
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

	public List<ObjectType> getAllowedChildObjectTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	public Document createDocument(Map<String, ?> arg0, ContentStream arg1,
			VersioningState arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	public Document createDocument(Map<String, ?> arg0, ContentStream arg1,
			VersioningState arg2, List<Policy> arg3, List<Ace> arg4,
			List<Ace> arg5, OperationContext arg6) {
		// TODO Auto-generated method stub
		return null;
	}

	public Document createDocumentFromSource(ObjectId arg0,
			Map<String, ?> arg1, VersioningState arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	public Document createDocumentFromSource(ObjectId arg0,
			Map<String, ?> arg1, VersioningState arg2, List<Policy> arg3,
			List<Ace> arg4, List<Ace> arg5, OperationContext arg6) {
		// TODO Auto-generated method stub
		return null;
	}

	public Folder createFolder(Map<String, ?> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Folder createFolder(Map<String, ?> arg0, List<Policy> arg1,
			List<Ace> arg2, List<Ace> arg3, OperationContext arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	public Policy createPolicy(Map<String, ?> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Policy createPolicy(Map<String, ?> arg0, List<Policy> arg1,
			List<Ace> arg2, List<Ace> arg3, OperationContext arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> deleteTree(boolean flag, UnfileObject unfileobject,
			boolean flag1) {
		// TODO Auto-generated method stub
		return null;
	}

	public ItemIterable<Document> getCheckedOutDocs() {
		// TODO Auto-generated method stub
		return null;
	}

	public ItemIterable<Document> getCheckedOutDocs(
			OperationContext operationcontext) {
		// TODO Auto-generated method stub
		return null;
	}

	public ItemIterable<CmisObject> getChildren() {		
		return new MockItemIterable<CmisObject>(childrenList);
	}

	public ItemIterable<CmisObject> getChildren(
			OperationContext operationcontext) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Tree<FileableCmisObject>> getDescendants(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Tree<FileableCmisObject>> getDescendants(int i,
			OperationContext operationcontext) {
		// TODO Auto-generated method stub
		return null;
	}

	public Folder getFolderParent() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Tree<FileableCmisObject>> getFolderTree(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Tree<FileableCmisObject>> getFolderTree(int i,
			OperationContext operationcontext) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	public TransientFolder getTransientFolder() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isRootFolder() {
		// TODO Auto-generated method stub
		return false;
	}

    public FileableCmisObject move(ObjectId oi, ObjectId oi1, OperationContext oc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Folder> getParents(OperationContext oc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getParentId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
