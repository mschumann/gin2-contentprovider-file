package net.sf.iqser.plugin.file.parser;

import java.io.InputStream;

import com.iqser.core.model.Content;

/**
 * Common FileParser Interface.
 * 
 * @author Christian Magnus
 */
public interface FileParser {

	public Content getContent(String fileName, InputStream inputStream)
			throws FileParserException;

}
