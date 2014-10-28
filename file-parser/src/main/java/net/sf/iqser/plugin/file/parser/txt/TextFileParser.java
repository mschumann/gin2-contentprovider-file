package net.sf.iqser.plugin.file.parser.txt;

import java.io.IOException;
import java.io.InputStream;

import net.sf.iqser.plugin.file.parser.FileParser;
import net.sf.iqser.plugin.file.parser.FileParserException;
import net.sf.iqser.plugin.file.parser.FileParserUtils;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.iqser.core.model.Attribute;
import com.iqser.core.model.Content;

/**
 * {@link FileParser} implementation to read text files.
 * 
 * @author Christian Magnus
 * 
 */
public class TextFileParser implements FileParser {

    /** Constant from the default content type for text documents. */
    private static final String TEXT_FILE_CONTENT_TYPE = "Text Document";

    /**
     * Default Logger for this class.
     */
    private static Logger logger = Logger.getLogger(TextFileParser.class);

    /**
     * Method implementation from {@link FileParser} interface.
     * 
     * @see net.sf.iqser.plugin.file.parser.FileParser#getContent(String,
     *      InputStream)
     * @param fileName the url of the file
     * @param inputStream the input stream of the file
     * @return content the content that is created
     * @throws FileParserException exception
     */
    public Content getContent(String fileName, InputStream inputStream)
	    throws FileParserException {
	logger.info("Parsing file " + fileName);

	// Create a new Content
	Content content = new Content();

	// Set the content type for a unknown format (file extention +
	// 'Document')
	content.setType(TEXT_FILE_CONTENT_TYPE);

	// Set the file name attribute. This Attribute is no key.
	content.addAttribute(new Attribute("FILENAME", FilenameUtils.getName(fileName),
		Attribute.ATTRIBUTE_TYPE_TEXT, false));

	// Set the title attribute. This Attribute is a key.
	content.addAttribute(new Attribute("TITLE", FileParserUtils
		.getFileTitle(fileName), Attribute.ATTRIBUTE_TYPE_TEXT, true));

	// Read content from InputStream and add it as fulltext
	try {
	    content.setFulltext(parse(inputStream));
	} catch (IOException e) {
	    logger.error("Failed to read stream for file " + fileName, e);
	    throw new FileParserException("Failed to read stream for file "
		    + fileName, e);
	}

	return content;
    }

    /**
     * Read the text files content.
     * 
     * @param inputStream
     *            The InputStream.
     * 
     * @param inputStream
     * @return A String containing the content.
     */
    private String parse(InputStream inputStream) throws IOException {
	String text = "";

	if (inputStream != null) {
	    byte[] in = new byte[inputStream.available()];
	    inputStream.read(in);
	    text = new String(in);
	}

	return text;
    }
}
