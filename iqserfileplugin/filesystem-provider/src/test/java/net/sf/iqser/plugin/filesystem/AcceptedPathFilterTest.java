package net.sf.iqser.plugin.filesystem;

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
}
