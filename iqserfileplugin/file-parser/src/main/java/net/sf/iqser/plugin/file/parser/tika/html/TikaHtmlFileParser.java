package net.sf.iqser.plugin.file.parser.tika.html;

import java.io.InputStream;

import com.iqser.core.model.Content;

import net.sf.iqser.plugin.file.parser.FileParserException;
import net.sf.iqser.plugin.file.parser.tika.TikaFileParser;

public class TikaHtmlFileParser extends TikaFileParser {

	public Content getContent(String fileName, InputStream inputStream)
			throws FileParserException {

		Content content = super.getContent(fileName, inputStream);

		content.setType("HTML Document");

		return content;
	}

}
