package net.sf.iqser.plugin.file.parser.tika.msoffice;

import java.io.InputStream;

import net.sf.iqser.plugin.file.parser.FileParserException;
import net.sf.iqser.plugin.file.parser.tika.TikaFileParser;

import com.iqser.core.model.Content;

public class PowerPointFileParser extends TikaFileParser{

	public Content getContent(String fileName, InputStream inputStream)
			throws FileParserException {
		
		Content content = super.getContent(fileName, inputStream);
		
		content.setType("PPT Document");
		
		return content;
	}

}
