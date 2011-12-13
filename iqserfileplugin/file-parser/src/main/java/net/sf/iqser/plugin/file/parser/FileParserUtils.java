package net.sf.iqser.plugin.file.parser;

import org.apache.commons.lang.StringUtils;
import org.apache.xerces.util.XMLChar;

/**
 * Util methods for all {@link FileParser}.
 * 
 * @author Christian Magnus
 * 
 */
public class FileParserUtils {

	/**
	 * Constant for the separator that splits the file name from the extention.
	 */
	public static final String FILE_EXTENTION_SEPARATOR = ".";

	/**
	 * Constant for the suffix of the file type.
	 */
	public static final String FILE_TYPE_SUFFIX = " Document";

	/**
	 * Constant for the unknown file type.
	 */
	public static final String UNKNOWN_FILE_TYPE = "Unknown" + FILE_TYPE_SUFFIX;

	/**
	 * Extract the title from the file name.The title of the file is the file
	 * name without the extention.
	 * 
	 * @param fileName
	 *            The file name
	 * @return The title or null
	 */
	public static String getFileTitle(String fileName) {
		String[] nameElements = getNameElements(fileName);
		if (nameElements != null) {
			return StringUtils.substringBefore(fileName, "." + nameElements[nameElements.length - 1]);
		}
		return null;
	}

	/**
	 * Split file name to it's elements.
	 * 
	 * @param fileName
	 *            The file name
	 * @return A String array or null
	 */
	public static String[] getNameElements(String fileName) {
		if (!StringUtils.isEmpty(fileName)) {
			String[] elements = StringUtils.split(fileName, FILE_EXTENTION_SEPARATOR);
			return elements;
		}
		return null;
	}

	/**
	 * Returns a default name for the content type.
	 * 
	 * @param fileName
	 *            The file name
	 * @return The file extention + 'Document' (e.q. 'XZY Document' if the file
	 *         name is 'sample.xzy') or 'Unknown Document' if there is no
	 *         extention.
	 */
	public static String getContentType(String fileName) {
		String[] nameElements = getNameElements(fileName);
		if (nameElements != null) {
			return StringUtils.upperCase(nameElements[nameElements.length - 1]) + FILE_TYPE_SUFFIX;
		}
		return UNKNOWN_FILE_TYPE;
	}

	/**
	 * Cleans up text and removes invalid characters that are not allowed in
	 * XML. Removing invalid characters is necessary as clients for the iQser
	 * platform use the SOAP WebService for communication.
	 * 
	 * @param text
	 *            to be cleaned
	 * 
	 * @return cleaned text that contains only characters which are allowed in
	 *         an XML document.
	 */
	public static String cleanUpText(String text) {
		StringBuffer validTextBuffer = new StringBuffer();
		for (char c : text.toCharArray()) {
			if (XMLChar.isValid(c)) {
				validTextBuffer.append(c);
			}
		}
		return validTextBuffer.toString();
	}
}
