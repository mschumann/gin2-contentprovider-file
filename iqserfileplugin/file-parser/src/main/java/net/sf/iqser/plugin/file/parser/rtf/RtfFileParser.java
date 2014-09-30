/**
 * 
 */
package net.sf.iqser.plugin.file.parser.rtf;

import java.io.InputStream;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;

import net.sf.iqser.plugin.file.parser.FileParser;
import net.sf.iqser.plugin.file.parser.FileParserException;
import net.sf.iqser.plugin.file.parser.FileParserUtils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.iqser.core.model.Attribute;
import com.iqser.core.model.Content;

/**
 * {@link FileParser} implementation for Rich Text Format documents.
 * 
 * @author Christian Magnus
 * 
 */
public class RtfFileParser implements FileParser {

	/** Constant from the default content type for text documents. */
	private static final String RTF_FILE_CONTENT_TYPE = "RTF Document";

	/**
	 * Default Logger for this class.
	 */
	private static Logger logger = Logger.getLogger(RtfFileParser.class);

	/**
	 * Parse RTF document and create a {@link Content} object.
	 * 
	 * @see net.sf.iqser.plugin.file.parser.FileParser#getContent(java.lang.String,
	 *      java.io.InputStream)
	 * @param fileName
	 *              the name of the file for which the content is created
	 * @param inputStream
	 *              the input stream of the file for which the content is created
	 * @return content
	 *              the content object  
	 * @throws FileParserException exception
	 */
	@Override
	public Content getContent(String fileName, InputStream inputStream)
			throws FileParserException {
		logger.info("Parsing file " + fileName);

		// Create a new Content
		Content content = new Content();

		// Set the content type for a unknown format (file extention +
		// 'Document')
		content.setType(RTF_FILE_CONTENT_TYPE);

		// Set the file name attribute. This Attribute is no key.
		content.addAttribute(new Attribute("FILENAME", FilenameUtils.getName(fileName),
				Attribute.ATTRIBUTE_TYPE_TEXT, false));

		// Set the title attribute. This Attribute is a key.
		content.addAttribute(new Attribute("TITLE", FileParserUtils
				.getFileTitle(fileName), Attribute.ATTRIBUTE_TYPE_TEXT, true));

		try {
			parseDocument(inputStream, content);
		} catch (Exception e) {
			logger.error("Failed to read stream for file " + fileName, e);
			throw new FileParserException("Failed to read stream for file "
					+ fileName, e);
		}

		Attribute keywordsAttribute = content.getAttributeByName("KEYWORDS");
		FileParserUtils.transformIntoMultiValue(keywordsAttribute);

		return content;
	}

	/**
	 * document parser.
	 * @param inputStream
	 * @param content
	 * @throws Exception
	 */
	private void parseDocument(InputStream inputStream, Content content)
			throws Exception {
		DefaultStyledDocument document = new DefaultStyledDocument();
		RTFEditorKit kit = new RTFEditorKit();
		kit.read(inputStream, document, 0);
		String fulltext = document.getText(0, document.getLength());

		if (!StringUtils.isEmpty(fulltext)) {
			content.setFulltext(fulltext);
		}
		// close input stream
		inputStream.close();
	}

}
