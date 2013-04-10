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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	List<Property<?>> propertyList = new ArrayList();
	List<CmisObject> childrenList = new ArrayList<CmisObject>();
	List<Folder> parents = new ArrayList<Folder>();

	//dummy methods
	public void dummyAddParent(Folder folder){
		parents.add(folder);
	}

	public void dummyAddCmisChild(CmisObject child){
		childrenList.add(child);
	}

	@Override
	public void addToFolder(ObjectId objectid, boolean flag) {
		
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
	public FileableCmisObject move(ObjectId objectid, ObjectId objectid1) {
		
		return null;
	}

	@Override
	public void removeFromFolder(ObjectId objectid) {
		

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
	public void applyPolicy(ObjectId... aobjectid) {
		

	}

	@Override
	public void delete(boolean flag) {
		

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
	public List<CmisExtensionElement> getExtensions(
			ExtensionLevel extensionlevel) {
		
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
	public void refreshIfOld(long l) {
		

	}

	@Override
	public Acl removeAcl(List<Ace> arg0, AclPropagation arg1) {
		
		return null;
	}

	@Override
	public void removePolicy(ObjectId... aobjectid) {
		

	}

	@Override
	public CmisObject updateProperties(Map<String, ?> arg0) {
		
		return null;
	}

	@Override
	public ObjectId updateProperties(Map<String, ?> arg0, boolean arg1) {
		
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getId() {
		List<String> value = (List<String>)getPropertyValue("cmisobjectId");
		return value.get(0);
	}

	@Override
	public ObjectType getBaseType() {
		
		return null;
	}

	@Override
	public BaseTypeId getBaseTypeId() {
		return BaseTypeId.CMIS_FOLDER;
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
		
		return null;
	}

	@Override
	public String getLastModifiedBy() {
		
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getName() {
		List<String> value = (List<String>)getPropertyValue("cmis:name");
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

	@SuppressWarnings("unchecked")
	@Override
	public Property<?> getProperty(String propId) {
		for (Property<?> prop : propertyList) {
			if (propId.equalsIgnoreCase(prop.getId())){
				return prop;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
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
	public List<ObjectType> getAllowedChildObjectTypes() {
		
		return null;
	}

	@Override
	public Document createDocument(Map<String, ?> arg0, ContentStream arg1,
			VersioningState arg2) {
		
		return null;
	}

	@Override
	public Document createDocument(Map<String, ?> arg0, ContentStream arg1,
			VersioningState arg2, List<Policy> arg3, List<Ace> arg4,
			List<Ace> arg5, OperationContext arg6) {
		
		return null;
	}

	@Override
	public Document createDocumentFromSource(ObjectId arg0,
			Map<String, ?> arg1, VersioningState arg2) {
		
		return null;
	}

	@Override
	public Document createDocumentFromSource(ObjectId arg0,
			Map<String, ?> arg1, VersioningState arg2, List<Policy> arg3,
			List<Ace> arg4, List<Ace> arg5, OperationContext arg6) {
		
		return null;
	}

	@Override
	public Folder createFolder(Map<String, ?> arg0) {
		
		return null;
	}

	@Override
	public Folder createFolder(Map<String, ?> arg0, List<Policy> arg1,
			List<Ace> arg2, List<Ace> arg3, OperationContext arg4) {
		
		return null;
	}

	@Override
	public Policy createPolicy(Map<String, ?> arg0) {
		
		return null;
	}

	@Override
	public Policy createPolicy(Map<String, ?> arg0, List<Policy> arg1,
			List<Ace> arg2, List<Ace> arg3, OperationContext arg4) {
		
		return null;
	}

	@Override
	public List<String> deleteTree(boolean flag, UnfileObject unfileobject,
			boolean flag1) {
		
		return null;
	}

	@Override
	public ItemIterable<Document> getCheckedOutDocs() {
		
		return null;
	}

	@Override
	public ItemIterable<Document> getCheckedOutDocs(
			OperationContext operationcontext) {
		
		return null;
	}

	@Override
	public ItemIterable<CmisObject> getChildren() {
		return new MockItemIterable<CmisObject>(childrenList);
	}

	@Override
	public ItemIterable<CmisObject> getChildren(
			OperationContext operationcontext) {
		
		return null;
	}

	@Override
	public List<Tree<FileableCmisObject>> getDescendants(int i) {
		
		return null;
	}

	@Override
	public List<Tree<FileableCmisObject>> getDescendants(int i,
			OperationContext operationcontext) {
		
		return null;
	}

	@Override
	public Folder getFolderParent() {
		
		return null;
	}

	@Override
	public List<Tree<FileableCmisObject>> getFolderTree(int i) {
		
		return null;
	}

	@Override
	public List<Tree<FileableCmisObject>> getFolderTree(int i,
			OperationContext operationcontext) {
		
		return null;
	}

	@Override
	public String getPath() {
		
		return null;
	}

	@Override
	public TransientFolder getTransientFolder() {
		
		return null;
	}

	@Override
	public boolean isRootFolder() {
		
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
