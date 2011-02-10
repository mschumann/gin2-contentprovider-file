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
     * Default constructor.
     * 
     * @param message
     * @param cause
     */
    public FileParserException(String message, Throwable cause) {
	super(message, cause);
    }

    /**
     * Default constructor.
     * 
     * @param message
     */
    public FileParserException(String message) {
	super(message);
    }

    /**
     * Default constructor.
     * 
     * @param cause
     */
    public FileParserException(Throwable cause) {
	super(cause);
    }

    /**
     * UID.
     */
    private static final long serialVersionUID = 1963111488229957217L;

}
