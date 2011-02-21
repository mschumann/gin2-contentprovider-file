package net.sf.iqser.plugin.file.parser.tika;

import java.io.IOException;
import java.io.InputStream;

import net.sf.iqser.plugin.file.parser.FileParser;
import net.sf.iqser.plugin.file.parser.FileParserException;
import net.sf.iqser.plugin.file.parser.FileParserUtils;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;

import com.iqser.core.model.Attribute;
import com.iqser.core.model.Content;

public abstract class TikaFileParser implements FileParser {

	public Content getContent(String fileName, InputStream inputStream)
			throws FileParserException {

		Content content = new Content();
		
		// Set the file name attribute. This Attribute is no key.
		content.addAttribute(new Attribute("FILENAME", fileName,
			Attribute.ATTRIBUTE_TYPE_TEXT, false));

		// Set the title attribute. This Attribute is a key.
		content.addAttribute(new Attribute("TITLE", FileParserUtils
			.getFileTitle(fileName), Attribute.ATTRIBUTE_TYPE_TEXT, true));

		// Set the content type for a unknown format (file extention +
		// 'Document')

		Tika tika = new Tika();
		String result = "";
		Metadata metadata = new Metadata();
		try {
			try {
				result = tika.parseToString(inputStream, metadata);
			} catch (TikaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			inputStream.close();
			content.setFulltext(result);
			for (String name : metadata.names()) {
				Attribute attribute = new Attribute();
				attribute.setKey(true);
				if (content.getAttributeByName(name) == null) {
					attribute.setName(name);
					attribute.setValue(metadata.get(name));
					content.addAttribute(attribute);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return content;

	}

}
