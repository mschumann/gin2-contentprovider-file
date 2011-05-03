package net.sf.iqser.plugin.file.mock.cmis;

import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.DocumentType;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Tree;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.ContentStreamAllowed;

public class MockDocumentBaseType implements DocumentType{

	public BaseTypeId getBaseTypeId() {
		return BaseTypeId.CMIS_DOCUMENT;
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLocalName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLocalNamespace() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getParentTypeId() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, PropertyDefinition<?>> getPropertyDefinitions() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getQueryName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean isControllableAcl() {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean isControllablePolicy() {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean isCreatable() {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean isFileable() {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean isFulltextIndexed() {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean isIncludedInSupertypeQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean isQueryable() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<CmisExtensionElement> getExtensions() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setExtensions(List<CmisExtensionElement> arg0) {
		// TODO Auto-generated method stub		
	}

	public boolean isBaseType() {
		return true;
	}

	public ObjectType getBaseType() {
		// TODO Auto-generated method stub
		return null;
	}

	public ObjectType getParentType() {
		// TODO Auto-generated method stub
		return null;
	}

	public ItemIterable<ObjectType> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Tree<ObjectType>> getDescendants(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean isVersionable() {
		// TODO Auto-generated method stub
		return null;
	}

	public ContentStreamAllowed getContentStreamAllowed() {
		// TODO Auto-generated method stub
		return null;
	}

}
