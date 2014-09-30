package net.sf.iqser.plugin.file.parser.pdf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import net.sf.iqser.plugin.file.parser.Configurable;
import net.sf.iqser.plugin.file.parser.FileParser;
import net.sf.iqser.plugin.file.parser.FileParserException;
import net.sf.iqser.plugin.file.parser.FileParserUtils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.util.PDFTextStripper;

import com.iqser.core.model.Attribute;
import com.iqser.core.model.Content;

/**
 * {@link FileParser} implementation to read PDF files.
 * 
 * @author Christian Magnus
 * 
 */
public class PdfFileParser implements FileParser, Configurable {

	/** Constant from the default content type for text documents. */
	private static final String PDF_FILE_CONTENT_TYPE = "PDF Document";

	private static final String PROPERTY_SORT_BY_POSITION = "PdfFileParser.sortByPosition";

	/**
	 * Default Logger for this class.
	 */
	private static Logger logger = Logger.getLogger(PdfFileParser.class);

	private Properties properties;

	/**
	 * Method implementation from {@link FileParser} interface.
	 * 
	 * @see net.sf.iqser.plugin.file.parser.FileParser#getContent(String,
	 *      InputStream)
	 * @param fileName
	 *            the url of the file
	 * @param inputStream
	 *            the input stream of the file
	 * @return content the content that is created for the file
	 * @throws FileParserException
	 *             exception
	 */
	@Override
	public Content getContent(String fileName, InputStream inputStream) throws FileParserException {
		logger.info("Parsing file " + fileName);

		// Create a new Content
		Content content = new Content();

		// Set the content type for a unknown format (file extention +
		// 'Document')
		content.setType(PDF_FILE_CONTENT_TYPE);

		// Set the file name attribute. This Attribute is no key.
		content.addAttribute(new Attribute("FILENAME", FilenameUtils.getName(fileName), Attribute.ATTRIBUTE_TYPE_TEXT,
				false));

		// Set the title attribute. This Attribute is a key.
		content.addAttribute(new Attribute("TITLE", FileParserUtils.getFileTitle(fileName),
				Attribute.ATTRIBUTE_TYPE_TEXT, true));

		try {
			// inputStream = new BufferedInputStream(inputStream);
			parsePdfDocument(inputStream, content);
		} catch (IOException e) {
			logger.error("Failed to read stream for file " + fileName, e);
			throw new FileParserException("Failed to read stream for file " + fileName, e);
		}

		Attribute keywordsAttribute = content.getAttributeByName("KEYWORDS");
		FileParserUtils.transformIntoMultiValue(keywordsAttribute, ", ");

		Attribute pdfkeywordsAttribute = content.getAttributeByName("PDFKEYWORDS");
		FileParserUtils.transformIntoMultiValue(pdfkeywordsAttribute, ", ");
		if (null == keywordsAttribute && null != pdfkeywordsAttribute) {
			pdfkeywordsAttribute.setName("KEYWORDS");
		}

		return content;
	}
	
	@Override
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	private String getPropertyValue(String name) {
		return properties == null ? null : properties.getProperty(name);
	}

	/**
	 * Extract Attributes from PDF Document.
	 * 
	 * @param is
	 *            An InputStream
	 * @param content
	 * @return A Collection of Attribute objects
	 * @throws IOException
	 *             exception
	 */
	private void parsePdfDocument(InputStream is, Content content) throws IOException {
		PDFParser parser = null;
		PDDocument doc = null;

		//parser = new PDFParser(is, new RandomAccessBuffer(), true);
		parser = new PDFParser(is);
		parser.parse();

		COSDocument cosdoc = parser.getDocument();
		doc = new PDDocument(cosdoc);

		if (doc != null && !doc.isEncrypted()) {
			getDocumentInformation(doc, content);
			content.setFulltext(FileParserUtils.cleanUpText(extractText(cosdoc)));
		}
		cosdoc.close();
		doc.close();
	}

	/**
	 * Extract additional Attributes from PDF Document.
	 * 
	 * @param doc
	 *            A PDDocument
	 * @return A Collection of Attribute objects
	 * @throws IOException
	 */
	private void getDocumentInformation(PDDocument doc, Content content) throws IOException {
		PDDocumentInformation docInfo = doc.getDocumentInformation();
		if (docInfo != null && !doc.isEncrypted()) {
			if (!StringUtils.isEmpty(docInfo.getAuthor())) {
				addOrUpdateAttribute(content, new Attribute("AUTHOR", docInfo.getAuthor(),
						Attribute.ATTRIBUTE_TYPE_TEXT, true));
			}

			if (docInfo.getCreationDate() != null) {


				addOrUpdateAttribute(content, new Attribute("CREATIONDATE", String.valueOf(docInfo.getCreationDate().getTimeInMillis()),
						Attribute.ATTRIBUTE_TYPE_DATE, true));
			}

			if (!StringUtils.isEmpty(docInfo.getTitle())) {
				addOrUpdateAttribute(content, new Attribute("TITLE", docInfo.getTitle(), Attribute.ATTRIBUTE_TYPE_TEXT,
						true));
			}

			if (!StringUtils.isEmpty(docInfo.getSubject())) {
				addOrUpdateAttribute(content, new Attribute("SUBJECT", docInfo.getSubject(),
						Attribute.ATTRIBUTE_TYPE_TEXT, true));
			}

			// if (!StringUtils.isEmpty(docInfo.getCreator())) {
			// addOrUpdateAttribute(content,
			// new Attribute("CREATOR", docInfo.getCreator(),
			// Attribute.ATTRIBUTE_TYPE_TEXT, true));
			// }

			if (!StringUtils.isEmpty(docInfo.getKeywords())) {
				addOrUpdateAttribute(content, new Attribute("PDFKEYWORDS", docInfo.getKeywords(),
						Attribute.ATTRIBUTE_TYPE_TEXT, true));
			}

			// if (!StringUtils.isEmpty(docInfo.getProducer())) {
			// addOrUpdateAttribute(content,
			// new Attribute("PRODUCER", docInfo.getProducer(),
			// Attribute.ATTRIBUTE_TYPE_TEXT, true));
			// }
		}
	}

	/**
	 * Validate if content already contains a {@link Attribute} with the same
	 * name, if so, the {@link Attribute} will be updated with the given
	 * attribute otherwise a new {@link Attribute} will be attached to the
	 * content.
	 * 
	 * @param content
	 *            The {@link Content}
	 * @param attribute
	 *            The {@link Attribute}
	 */
	private void addOrUpdateAttribute(Content content, Attribute attribute) {
		Attribute contentAttribute = content.getAttributeByName(attribute.getName());
		if (contentAttribute != null) {
			contentAttribute.setValue(attribute.getValue());
			contentAttribute.setType(attribute.getType());
			contentAttribute.setKey(attribute.isKey());
		} else {
			content.addAttribute(attribute);
		}
	}

	/**
	 * Extract plain Text from PDF Document.
	 * 
	 * @param cosdoc
	 *            A COSDocument
	 * @return A Attribute named 'CONTENT' containing the complete plain text
	 */
	private String extractText(COSDocument cosdoc) {
		String fulltext = "";
		try {
			PDFTextStripper stripper = new PDFTextStripper();
			boolean sortByPosition = Boolean.parseBoolean(getPropertyValue(PROPERTY_SORT_BY_POSITION));
			stripper.setSortByPosition(sortByPosition);
			if (!cosdoc.isEncrypted()) {
				try {
					PDDocument pddoc = new PDDocument(cosdoc);
					fulltext = stripper.getText(pddoc);
				} catch (IOException e) {
					logger.error("Cannot extract Document", e);
				}
			} else {
				logger.warn("Cannot parse encrypted Document");
			}
		} catch (Exception e) {
			logger.warn("Error parsing PDF Document", e);
		}

		return fulltext;
	}
}
