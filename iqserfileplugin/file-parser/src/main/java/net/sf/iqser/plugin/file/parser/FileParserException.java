package net.sf.iqser.plugin.file.parser;

/**
 * Exception for all {@link FileParser} implementations on occurred errors while
 * parsing.
 * 
 * @author Christian Magnus
 * 
 */
public class FileParserException extends Exception {

	/**
	 * Default constructor.
	 */
	public FileParserException() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param message the message of the exception
	 * @param cause the cause of the exception
	 */
	public FileParserException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 *  Constructor.
	 * 
	 * @param message the message of the exception
	 */
	public FileParserException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * 
	 * @param cause the cause of the exception
	 */
	public FileParserException(Throwable cause) {
		super(cause);
	}

	/**
	 * UID.
	 */
	private static final long serialVersionUID = 1963111488229957217L;

}
