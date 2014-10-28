package net.sf.iqser.plugin.file.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import net.sf.iqser.plugin.file.parser.tika.TikaFileParser;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;

/**
 * Factory implementation to get a concrete {@link FileParser} implementation.
 * 
 * @author Christian Magnus
 * 
 */
public class FileParserFactory {

	/**
	 * Default Logger for this class.
	 */
	private static Logger logger = Logger.getLogger(FileParserFactory.class);

	/**
	 * Constant for the property file to load.
	 */
	private static final String PARSER_MAPPINGS_PROPERTIES = "net/sf/iqser/plugin/file/parser/parser-mappings.properties";

	/**
	 * Constant for mapping prefix.
	 */
	private static final String FILE_TYPE_PREFIX = "filetype.";
	private static final String MIME_TYPE_PREFIX = "mimetype.";

	/**
	 * The FileParserFactory instance.
	 */
	private static FileParserFactory instance;

	/**
	 * Return the instance of this class.
	 * 
	 * @return the FileParserFactory instance
	 */
	public static FileParserFactory getInstance() {
		if (instance == null) {
			instance = new FileParserFactory();
		}

		return instance;
	}

	/**
	 * Properties containing the mappings.
	 */
	private Properties mappings;

	/**
	 * A private contructor.
	 */
	private FileParserFactory() {
		loadMappings();
	}

	/**
	 * Load mappings from classpath.
	 */
	private void loadMappings() {
		try {
			logger.info("Loading FileParser mappings");
			mappings = new Properties();
			mappings.load(this.getClass().getClassLoader().getResourceAsStream(PARSER_MAPPINGS_PROPERTIES));
		} catch (IOException e) {
			logger.fatal("Unable to load FileParser mappings", e);
			// If this happens, there is no sense to continue
			throw new RuntimeException("Unable to load FileParser mappings", e);
		}
	}
	
	/**
	 * Return a concrete {@link FileParser} implementation. If there is no {@link FileParser} defined in the mappings,
	 * the {@link DefaultFileParser} is returned. You'll never get a null from this method.
	 * 
	 * @param fileName
	 *            The name of the file including extension (e.q. sample.txt).
	 * @return A {@link FileParser} instance
	 */
	public FileParser getFileParser(String fileName) {
		return getFileParser(fileName, null);
	}

	/**
	 * Return a concrete {@link FileParser} implementation. If there is no {@link FileParser} defined in the mappings,
	 * the {@link DefaultFileParser} is returned. You'll never get a null from this method.
	 * 
	 * @param fileName
	 *            The name of the file including extension (e.q. sample.txt).
	 * @param properties
	 *            Additional properties, the parser might use.
	 * @return A {@link FileParser} instance
	 */
	public FileParser getFileParser(String fileName, Properties properties) {
		FileParser parser = null;

		// Get FileParser from mappings
		String mappingClassname = mappings.getProperty(getMappingName(fileName));

		// If there is no FileParser defined in mapping use the
		// DefaultFileParser
		if (StringUtils.isEmpty(mappingClassname)) {
			parser = new DefaultFileParser();
		} else {

			try {
				logger.debug("Found mapping" + mappingClassname + " for file " + fileName);
				parser = createFileParserInstance(mappingClassname);
			} catch (Exception e) {
				logger.warn("Instance creation failed for class " + mappingClassname
						+ ". Using DefaultFileParser as fallback.");
				parser = new DefaultFileParser();
			}
		}
		if(parser instanceof Configurable) {
			((Configurable) parser).setProperties(properties);
		}
		return parser;
	}

	/**
	 * method that returns a file parser from an input stream.
	 * 
	 * @param is
	 *            inputstream of the file.
	 * @return parser the selected file parser.
	 */
	public FileParser getFileParser(InputStream is) {

		FileParser parser = null;
		String mappingClassname = null;
		try {

			// detect mime type from input stream
			Tika tika = new Tika();
			Metadata metadata = new Metadata();
			Reader r = tika.parse(is, metadata);
			r.close();
			String contentType = metadata.get("Content-Type");
			logger.info("content-type=" + contentType);

			mappingClassname = mappings.getProperty(getPropMappingName(contentType));
			if (mappingClassname != null) {
				parser = createFileParserInstance(mappingClassname);
			} else {
				logger.warn("No parser defined for mimetype " + contentType);
				parser = new TikaFileParser();
			}
		} catch (IOException e) {
			logger.warn("Error reading input stream. Using TikaFileParser as fallback.");
			parser = new TikaFileParser();
		} catch (Exception e) {
			logger.warn("Instance creation faild for class " + mappingClassname + ". Using TikaFileParser as fallback.");
			parser = new TikaFileParser();
		}
		return parser;

	}

	private FileParser createFileParserInstance(String className) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		return (FileParser) Class.forName(className).newInstance();
	}

	private String getPropMappingName(String mimeType) {
		return MIME_TYPE_PREFIX + mimeType;
	}

	private String getMappingName(String fileName) {
		String[] nameElements = FileParserUtils.getNameElements(fileName);
		if (nameElements != null) {
			return FILE_TYPE_PREFIX + nameElements[nameElements.length - 1].toLowerCase();
		}
		return null;
	}
}
