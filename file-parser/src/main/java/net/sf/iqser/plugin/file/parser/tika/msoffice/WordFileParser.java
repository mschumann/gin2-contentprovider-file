package net.sf.iqser.plugin.file.parser.tika.msoffice;

import java.io.InputStream;

import net.sf.iqser.plugin.file.parser.FileParserException;
import net.sf.iqser.plugin.file.parser.tika.TikaFileParser;

import com.iqser.core.model.Content;

/**
 * tika parser for word files.
 * @author Alexandru Galos.
 *
 */
public class WordFileParser extends TikaFileParser{

	/**
	 * creates content for word files.
	 *  @see net.sf.iqser.plugin.file.parser.FileParser#getContent(java.lang.String,
	 *      java.io.InputStream)
	 * @param fileName
	 *            the url of the file.
	 * @param inputStream
	 *            the input stream of the file.
	 * @return content the content that is created
	 * @throws FileParserException exception
	 */
	public Content getContent(String fileName, InputStream inputStream)
			throws FileParserException {
		
		Content content = super.getContent(fileName, inputStream);
		
		content.setType("DOC Document");
		
		return content;
	}
	
}
