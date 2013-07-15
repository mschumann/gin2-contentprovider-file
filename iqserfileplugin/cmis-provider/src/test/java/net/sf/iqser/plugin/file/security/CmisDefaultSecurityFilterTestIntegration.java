package net.sf.iqser.plugin.file.security;

import junit.framework.TestCase;

import com.iqser.core.model.Content;
import com.iqser.core.plugin.security.IQserSecurityException;

public class CmisDefaultSecurityFilterTestIntegration extends TestCase {

	private CmisDefaultSecurityFilter filter;

	private Content content;
	private final String user = "robert.baban";
	private final String password = "#EDCXSW@1qaz";

	@Override
	public void setUp() {
		filter = new CmisDefaultSecurityFilter();
		content = new Content();
		content.setContentUrl("http://cmis/Shared Documents/cmis:document#3-5120");
	}

	public void testCanRead() throws IQserSecurityException {
		boolean canRead = filter.canRead(user, password, content.getContentId());
		assertTrue(canRead);
	}

}
