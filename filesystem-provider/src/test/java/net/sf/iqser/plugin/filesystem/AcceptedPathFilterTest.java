package net.sf.iqser.plugin.filesystem;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class AcceptedPathFilterTest extends TestCase {

	private String[] acceptedPaths = new String[] { "path1", "path2","",null };
	private String[] deniedPaths = new String[] { "path1", "path2","",null };

	AcceptedPathFilter apf = new AcceptedPathFilter();

	public void testAddAccepted() throws SecurityException,
			NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {

		apf.addAccepted(Arrays.asList(acceptedPaths));
		Field field = apf.getClass().getDeclaredField("acceptedPath");
		field.setAccessible(true);
		List<String> acceptedPaths = (List<String>) field.get(apf);
		assertNotNull(acceptedPaths);

		assertEquals(2,acceptedPaths.size());
		assertEquals(acceptedPaths.get(0), "path1");
		assertEquals(acceptedPaths.get(1), "path2");
	}

	public void testAddDenied() throws SecurityException,
			NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {

		apf.addDenied(Arrays.asList(deniedPaths));
		Field field = apf.getClass().getDeclaredField("deniedPath");
		field.setAccessible(true);
		List<String> deniedPaths = (List<String>) field.get(apf);
		assertNotNull(deniedPaths);

		assertEquals(2,deniedPaths.size());
		assertEquals(deniedPaths.get(0), "path1");
		assertEquals(deniedPaths.get(1), "path2");
	}
	
	public void testAcceptAll(){
		AcceptedPathFilter filter = new AcceptedPathFilter();
		String testDataDir = System.getProperty("testdata.dir", "testdata");
		
		File file = new File(testDataDir+File.separator+"testSynch");
		assertTrue(filter.accept(file));
		
		File includeFolder1 = new File(testDataDir+"/testSynch/include");
		assertTrue(filter.accept(includeFolder1));
		
		File includeFolder2 = new File(testDataDir+"/testAttributes");
		assertTrue(filter.accept(includeFolder2));
		
		File excludeFolder = new File(testDataDir+"/testSynch/exclude");
		assertTrue(filter.accept(excludeFolder));
	}
	
	public void testAcceptExclude(){
		String testDataDir = System.getProperty("testdata.dir", "testdata");
		
		AcceptedPathFilter filter = new AcceptedPathFilter();
		filter.addDeniedPath(testDataDir+"/testSynch/exclude");		
				
		File includeFolder1 = new File(testDataDir+"/testSynch/include");
		assertTrue(filter.accept(includeFolder1));
		
		File includeFolder2 = new File(testDataDir+"/testAttributes");
		assertTrue(filter.accept(includeFolder2));
		
		File excludeFolder = new File(testDataDir+"/testSynch/exclude");
		assertFalse(filter.accept(excludeFolder));
	}
	
	public void testAcceptExcludeEmpty(){
		String testDataDir = System.getProperty("testdata.dir", "testdata");
		
		AcceptedPathFilter filter = new AcceptedPathFilter();
		filter.addDeniedPath("");		
				
		File includeFolder1 = new File(testDataDir+"/testSynch/include");
		assertTrue(filter.accept(includeFolder1));
		
		File includeFolder2 = new File(testDataDir+"/testAttributes");
		assertTrue(filter.accept(includeFolder2));
		
		File excludeFolder = new File(testDataDir+"/testSynch/exclude");
		assertTrue(filter.accept(excludeFolder));
	}
	
	public void testAcceptInclude(){
		String testDataDir = System.getProperty("testdata.dir", "testdata");
		
		AcceptedPathFilter filter = new AcceptedPathFilter();
		filter.addAcceptedPath(testDataDir+"/testSynch/include");	
				
		File includeFolder1 = new File(testDataDir+"/testSynch/include");
		assertTrue(filter.accept(includeFolder1));
		
		File includeFolder2 = new File(testDataDir+"/testAttributes");
		assertFalse(filter.accept(includeFolder2));
		
		File excludeFolder = new File(testDataDir+"/testSynch/exclude");
		assertFalse(filter.accept(excludeFolder));
	}
	
	public void testAcceptIncludeEmpty(){
		String testDataDir = System.getProperty("testdata.dir", "testdata");
		
		AcceptedPathFilter filter = new AcceptedPathFilter();
		filter.addAcceptedPath("");	
				
		File includeFolder1 = new File(testDataDir+"/testSynch/include");
		assertTrue(filter.accept(includeFolder1));
		
		File includeFolder2 = new File(testDataDir+"/testAttributes");
		assertTrue(filter.accept(includeFolder2));
		
		File excludeFolder = new File(testDataDir+"/testSynch/exclude");
		assertTrue(filter.accept(excludeFolder));
	}
	
	public void testAcceptIncludeAndExclude(){
		String testDataDir = System.getProperty("testdata.dir", "testdata");
		
		AcceptedPathFilter filter = new AcceptedPathFilter();
		filter.addAcceptedPath(testDataDir+"/testSynch/include");
		filter.addDeniedPath(testDataDir+"/testSynch/exclude");		
				
		File includeFolder = new File(testDataDir+"/testSynch/include");
		assertTrue(filter.accept(includeFolder));
		
		File f2 = new File(testDataDir+"/testAttributes");
		assertFalse(filter.accept(f2));
		
		File excludeFolder = new File(testDataDir+"/testSynch/exclude");
		assertFalse(filter.accept(excludeFolder));
	}
}
