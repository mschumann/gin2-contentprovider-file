package net.sf.iqser.plugin.file.parser;

import org.apache.commons.lang.StringUtils;

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

	public static final String FILE_TYPE_SUFFIX = " Document";

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
			return StringUtils.substringBefore(fileName, "."
					+ nameElements[nameElements.length - 1]);
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
			String[] elements = StringUtils.split(fileName,
					FILE_EXTENTION_SEPARATOR);
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
			return StringUtils.upperCase(nameElements[nameElements.length - 1])
					+ FILE_TYPE_SUFFIX;
		}
		return UNKNOWN_FILE_TYPE;
	}
}
