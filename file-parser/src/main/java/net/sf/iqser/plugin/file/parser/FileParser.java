package net.sf.iqser.plugin.file.parser;

import java.io.InputStream;

import com.iqser.core.model.Content;

/**
 * Common FileParser Interface.
 * 
 * @author Christian Magnus
 */
public interface FileParser {

    /**
     * Parse a inputStream and return a {@link Content} object.
     * 
     * @param fileName
     *            Name of the file
     * @param inputStream
     *            {@link InputStream} to parse
     * @return A {@link Content} object
     * @throws FileParserException
     *             Throw this Exception if the {@link InputStream} cannot be
     *             parsed.
     */
    Content getContent(String fileName, InputStream inputStream)
	    throws FileParserException;
}
