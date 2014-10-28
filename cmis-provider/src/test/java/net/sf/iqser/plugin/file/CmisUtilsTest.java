package net.sf.iqser.plugin.file;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class CmisUtilsTest extends TestCase {

	public void testParseInitParam() {
		List<String> params = CmisUtils.parseInitParam("[param1][ param2][param3 ][  param4  ]");
		
		assertEquals("param1", params.get(0));
		assertEquals("param2", params.get(1));
		assertEquals("param3", params.get(2));
		assertEquals("param4", params.get(3));
		
		params = CmisUtils.parseInitParam(null);
		assertTrue(params.isEmpty());
		
		params = CmisUtils.parseInitParam("");
		assertTrue(params.isEmpty());
	}

	public void testParseAttributesMappings() {
		Map<String, String> mappings = CmisUtils.parseAttributesMappings("[attr1=new_attr1][ attr2 = new_attr2]");
		
		assertEquals("new_attr1", mappings.get("attr1"));
		assertEquals("new_attr2", mappings.get("attr2"));
		
		mappings = CmisUtils.parseAttributesMappings(null);
		assertTrue(mappings.isEmpty());
		
		mappings = CmisUtils.parseAttributesMappings("");
		assertTrue(mappings.isEmpty());

	}

	public void testGetRepository() {
		String docContentUrl = "http://cmis/repoName/cmis:folder#123-456";
		assertEquals("repoName",CmisUtils.getRepository(docContentUrl));
		
		docContentUrl = "http://cmis/repoName";		
		assertNull(CmisUtils.getRepository(docContentUrl));
	}

	public void testGetObjectID() {
		String docContentUrl = "http://cmis/repoName/cmis:document#123-456";
		assertEquals("123-456",CmisUtils.getObjectID(docContentUrl));
		
		docContentUrl = "http://cmis/repoName";		
		assertNull(CmisUtils.getObjectID(docContentUrl));

	}

}
