package net.sf.iqser.plugin.file.parser.tika.html;

import java.io.InputStream;

import com.iqser.core.model.Attribute;
import com.iqser.core.model.Content;

import net.sf.iqser.plugin.file.parser.FileParserException;
import net.sf.iqser.plugin.file.parser.tika.TikaFileParser;

/**
 * tika parser for html files.
 * @author Alexandru Galos
 *
 */
public class TikaHtmlFileParser extends TikaFileParser {

	/**
	 * creates content for html files.
	 *  @see net.sf.iqser.plugin.file.parser.FileParser#getContent(java.lang.String,
	 *      java.io.InputStream).
	 * @param fileName
	 *           the url of the file.
	 * @param inputStream
	 *           the input stream of the file.
	 * @return content the new content that is created
	 * @throws FileParserException exception
	 */
	public Content getContent(String fileName, InputStream inputStream)
			throws FileParserException {

		Content content = super.getContent(fileName, inputStream);
		Attribute a = content.getAttributeByName("KEYWORDS");
		
		if (a != null) {
			String value = a.getValue().replaceAll(", ", Attribute.MULTIVALUE_SEPARATOR);
			a.setValue(value);
			a.setMultiValue(true);
		}
		
		content.setType("HTML Document");

		return content;
	}

}
