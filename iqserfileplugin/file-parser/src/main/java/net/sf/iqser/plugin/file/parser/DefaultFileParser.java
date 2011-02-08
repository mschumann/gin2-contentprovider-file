package net.sf.iqser.plugin.file.parser;

import java.io.InputStream;

import org.apache.log4j.Logger;

import com.iqser.core.model.Attribute;
import com.iqser.core.model.Content;

/**
 * {@link FileParser} implementation to read at least basic file information.
 * This class is always used if there is no concrete {@link FileParser}
 * available.
 * 
 * @author Christian Magnus
 * 
 */
public class DefaultFileParser implements FileParser {

	/**
	 * Default Logger for this class.
	 */
	private static Logger logger = Logger.getLogger(DefaultFileParser.class);

	/**
	 * Method implementation from {@link FileParser} interface.<br>
	 * The InputStream param can be null.
	 * 
	 * @see net.sf.iqser.plugin.file.parser.FileParser#getContent(String,
	 *      InputStream)
	 */
	public Content getContent(String fileName, InputStream inputStream)
			throws FileParserException {
		logger.info("Parsing file " + fileName);

		// Create a new Content
		Content content = new Content();

		// Set the content type for a unknown format (file extention +
		// 'Document')
		content.setType(FileParserUtils.getContentType(fileName));

		// Set the file name attribute. This Attribute is no key.
		content.addAttribute(new Attribute("FILENAME", fileName,
				Attribute.ATTRIBUTE_TYPE_TEXT, false));

		// Set the title attribute. This Attribute is a key.
		content.addAttribute(new Attribute("TITLE", FileParserUtils
				.getFileTitle(fileName), Attribute.ATTRIBUTE_TYPE_TEXT, true));

		return content;
	}
}
