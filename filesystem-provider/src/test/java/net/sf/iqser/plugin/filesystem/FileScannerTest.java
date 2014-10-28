package net.sf.iqser.plugin.filesystem;

import java.io.FileFilter;
import java.lang.reflect.Field;
import java.util.List;

import junit.framework.TestCase;

public class FileScannerTest extends TestCase{

	
	public void testFileScanner() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
	{
		FileScanner fs = new FileScanner();
		Field declaredField = fs.getClass().getDeclaredField("folders");
		declaredField.setAccessible(true);
		List folders = (List)declaredField.get(fs);
		assertNotNull(folders);
		assertEquals(0, folders.size());
		folders.add("path1");
		folders.add("path2");
		fs.setFolders(folders);
		
		assertEquals(2, folders.size());
		
		FileFilter pathFilter = new AcceptedPathFilter();
		fs.setPathFilter(pathFilter);
		
		assertNotNull(pathFilter);
		
		
		
	}
}
