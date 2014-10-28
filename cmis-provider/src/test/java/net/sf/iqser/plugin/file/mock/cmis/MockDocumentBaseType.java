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

	private static final long serialVersionUID = 799571770945224871L;

	public BaseTypeId getBaseTypeId() {
		return BaseTypeId.CMIS_DOCUMENT;
	}

	public String getDescription() {
		return null;
	}

	public String getDisplayName() {
		return null;
	}

	public String getId() {
		return null;
	}

	public String getLocalName() {
		return null;
	}

	public String getLocalNamespace() {
		
		return null;
	}

	public String getParentTypeId() {
		
		return null;
	}

	public Map<String, PropertyDefinition<?>> getPropertyDefinitions() {
		
		return null;
	}

	public String getQueryName() {
		
		return null;
	}

	public Boolean isControllableAcl() {
		
		return null;
	}

	public Boolean isControllablePolicy() {
		
		return null;
	}

	public Boolean isCreatable() {
		
		return null;
	}

	public Boolean isFileable() {
		
		return null;
	}

	public Boolean isFulltextIndexed() {
		
		return null;
	}

	public Boolean isIncludedInSupertypeQuery() {
		
		return null;
	}

	public Boolean isQueryable() {
		
		return null;
	}

	public List<CmisExtensionElement> getExtensions() {
		
		return null;
	}

	public void setExtensions(List<CmisExtensionElement> arg0) {
				
	}

	public boolean isBaseType() {
		return true;
	}

	public ObjectType getBaseType() {
		
		return null;
	}

	public ObjectType getParentType() {
		
		return null;
	}

	public ItemIterable<ObjectType> getChildren() {
		
		return null;
	}

	public List<Tree<ObjectType>> getDescendants(int i) {
		
		return null;
	}

	public Boolean isVersionable() {
		
		return null;
	}

	public ContentStreamAllowed getContentStreamAllowed() {
		
		return null;
	}

}
