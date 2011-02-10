package net.sf.iqser.plugin.file.parser;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

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
    private static final String MAPPING_PREFIX = "filetype.";

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
	    mappings.load(ClassLoader
		    .getSystemResourceAsStream(PARSER_MAPPINGS_PROPERTIES));
	} catch (IOException e) {
	    logger.fatal("Unable to load FileParser mappings", e);
	    // If this happens, there is no sense to continue
	    throw new RuntimeException("Unable to load FileParser mappings", e);
	}
    }

    /**
     * Return a conctrete {@link FileParser} implementation. If there is no
     * {@link FileParser} defined in the mappings, the {@link DefaultFileParser}
     * is returned. You'll never get a null from this method.
     * 
     * @param fileName
     *            The name of the file including extention (e.q. sample.txt).
     * @return A {@link FileParser} instance
     */
    public FileParser getFileParser(String fileName) {
	FileParser parser = null;

	// Get FileParser from mappings
	String mappingClassname = mappings
		.getProperty(getMappingName(fileName));

	// If there is no FileParser defined in mapping use the
	// DefaultFileParser
	if (StringUtils.isEmpty(mappingClassname)) {
	    parser = new DefaultFileParser();
	} else {

	    try {
		logger.debug("Found mapping" + mappingClassname + " for file "
			+ fileName);
		parser = createFileParserInstance(mappingClassname);
	    } catch (Exception e) {
		logger.warn("Instance creation faild for class "
			+ mappingClassname
			+ ". Using DefaultFileParser as fallback.");
		parser = new DefaultFileParser();
	    }
	}
	return parser;
    }

    private FileParser createFileParserInstance(String className)
	    throws InstantiationException, IllegalAccessException,
	    ClassNotFoundException {
	return (FileParser) Class.forName(className).newInstance();
    }

    private String getMappingName(String fileName) {
	String[] nameElements = FileParserUtils.getNameElements(fileName);
	if (nameElements != null) {
	    return MAPPING_PREFIX
		    + nameElements[nameElements.length - 1].toLowerCase();
	}
	return null;
    }
}
