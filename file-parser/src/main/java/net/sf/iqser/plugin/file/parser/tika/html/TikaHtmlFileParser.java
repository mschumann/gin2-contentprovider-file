package net.sf.iqser.plugin.file.parser.tika.html;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.sf.iqser.plugin.file.parser.FileParserException;
import net.sf.iqser.plugin.file.parser.tika.TikaFileParser;

import com.iqser.core.model.Attribute;
import com.iqser.core.model.Content;

/**
 * tika parser for html files.
 * @author Alexandru Galos
 *
 */
public class TikaHtmlFileParser extends TikaFileParser {

	private static final DateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ", Locale.GERMANY);

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
	@Override
	public Content getContent(String fileName, InputStream inputStream)
			throws FileParserException {

		Content content = super.getContent(fileName, inputStream);
		content.setType("HTML Document");

		setLastModifiedDate(content);

		return content;
	}

	protected static void setLastModifiedDate(Content content) {
		Attribute lastModifiedAttribute = content.getAttributeByName("LAST-MODIFIED");
		if (null != lastModifiedAttribute) {
			String lastModifiedValue = lastModifiedAttribute.getValue();
			try {
				Date lastModifiedDate = FORMATTER.parse(lastModifiedValue);
				content.setModificationDate(lastModifiedDate.getTime());
			} catch (ParseException e) {
				// do nothing
			}
		}
	}

}
