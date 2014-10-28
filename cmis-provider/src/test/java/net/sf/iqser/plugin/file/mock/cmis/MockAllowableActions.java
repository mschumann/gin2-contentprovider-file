package net.sf.iqser.plugin.file.mock.cmis;

import java.util.List;
import java.util.Set;

import org.apache.chemistry.opencmis.commons.data.AllowableActions;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.enums.Action;

public class MockAllowableActions implements AllowableActions{

		private Set<Action> actions;
		
		public MockAllowableActions(Set<Action> actions){
			this.actions =actions;
		}
		
		public List<CmisExtensionElement> getExtensions() {
			return null;
		}

		public void setExtensions(List<CmisExtensionElement> arg0) {
		}

		public Set<Action> getAllowableActions() {
			return actions;
		}
		
}
