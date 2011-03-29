package net.sf.iqser.plugin.file.parser;

import junit.framework.TestCase;

public class FileParserExceptionTest extends TestCase{

	
	public void testFileParserConstructorNoArgs(){
		FileParserException fpe =  new FileParserException();
		assertNotNull(fpe);
	}
	
	public void testFileParserConstructorWithString(){
		FileParserException fpe = new FileParserException("exception");
		assertNotNull(fpe);
		assertEquals("exception", fpe.getMessage());
	}
	
	public void testFileParserConstructorCause(){
		Throwable cause = new Throwable("exception");
		FileParserException fpe = new FileParserException(cause );
		assertNotNull(fpe);
		assertNotNull(fpe.getCause());
		assertEquals("exception", fpe.getCause().getMessage());
	}
}
