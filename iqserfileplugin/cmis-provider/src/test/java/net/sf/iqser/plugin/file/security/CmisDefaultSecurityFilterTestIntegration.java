package net.sf.iqser.plugin.file.security;

import junit.framework.TestCase;
import net.sf.iqser.plugin.file.CmisContentProvider;
import net.sf.iqser.plugin.file.security.CmisDefaultSecurityFilter;

import com.iqser.core.model.Content;
import com.iqser.core.plugin.security.IQserSecurityException;

public class CmisDefaultSecurityFilterTestIntegration extends TestCase {

	private CmisDefaultSecurityFilter filter;
	
	private Content content;
	private String user = "robert.baban";
	private String password = "#EDCXSW@1qaz";
		
	public void setUp(){
		filter = new CmisDefaultSecurityFilter();
		content = new Content();			
		content.setContentUrl("http://cmis/Shared Documents/cmis:document#3-5120");		
	}

	public void testCanRead() throws IQserSecurityException {
		boolean canRead = filter.canRead(user, password, content);
		
		assertTrue(canRead);
	}

	public void testCanEdit() throws IQserSecurityException {
		boolean canEdit = filter.canEdit(user, password, content);
		
		assertTrue(canEdit);
	}

	public void testCanExecuteAction() throws IQserSecurityException {
		
		String[] actions = new String[]{
				CmisContentProvider.ACTION_DELETE,
				CmisContentProvider.ACTION_UPDATE,				
				CmisContentProvider.ACTION_CHECK_OUT				
		};
		
		for (String action : actions) {
			boolean canExecuteAction = filter.canExecuteAction(user, password, action, content);
			
			assertTrue(canExecuteAction);			
		}
	}

}
