package net.sf.iqser.plugin.file.parser.tika;

import java.io.IOException;
import java.io.InputStream;

import net.sf.iqser.plugin.file.parser.FileParser;
import net.sf.iqser.plugin.file.parser.FileParserException;
import net.sf.iqser.plugin.file.parser.FileParserUtils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;

import com.iqser.core.model.Attribute;
import com.iqser.core.model.Content;

/**
 * parser for different tika formats.
 * 
 * @author Alexandru Galos
 * 
 */
public class TikaFileParser implements FileParser {

	/**
	 * creates content for different tika formats.
	 * 
	 * @see net.sf.iqser.plugin.file.parser.FileParser#getContent(java.lang.String,
	 *      java.io.InputStream).
	 * @param fileName
	 *            the url of the file for which the content is created.
	 * @param inputStream
	 *            the input stream of the file for which the content is created.
	 * @return content the content that is created
	 * @throws FileParserException exception
	 */
	public Content getContent(String fileName, InputStream inputStream)
			throws FileParserException {

		Content content = new Content();

		// Set the file name attribute. This Attribute is no key.
		content.addAttribute(new Attribute("FILENAME", FilenameUtils.getName(fileName),
				Attribute.ATTRIBUTE_TYPE_TEXT, false));

		// Set the title attribute. This Attribute is a key.
		content.addAttribute(new Attribute("TITLE", FileParserUtils
				.getFileTitle(fileName), Attribute.ATTRIBUTE_TYPE_TEXT, true));

		Tika tika = new Tika();
		String result = "";
		Metadata metadata = new Metadata();
		try {
			result = tika.parseToString(inputStream, metadata);
			content.setFulltext(result);
			for (String name : metadata.names()) {
				Attribute attribute = new Attribute();
				attribute.setKey(true);
				if (content.getAttributeByName(name) == null && !StringUtils.isEmpty(metadata.get(name))) {
					attribute.setName(name);
					attribute.setValue(metadata.get(name));
					attribute.setType(Attribute.ATTRIBUTE_TYPE_TEXT);
					content.addAttribute(attribute);
				}
			}
		} catch (IOException e) {
			throw new FileParserException(e);
		} catch (TikaException e) {
			throw new FileParserException(e);
		}

		return content;

	}

}
