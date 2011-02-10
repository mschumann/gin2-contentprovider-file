package net.sf.iqser.plugin.file.parser.html;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import net.sf.iqser.plugin.file.parser.FileParser;
import net.sf.iqser.plugin.file.parser.FileParserException;
import net.sf.iqser.plugin.file.parser.FileParserUtils;

import org.apache.log4j.Logger;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.html.HTMLAnchorElement;
import org.w3c.dom.html.HTMLMetaElement;
import org.w3c.dom.html.HTMLTitleElement;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.iqser.core.model.Attribute;
import com.iqser.core.model.Content;

/**
 * {@link FileParser} implementation to read HTML files.
 * 
 * @author Christian Magnus
 * 
 */
public class HtmlFileParser implements FileParser {

	/** Constant from the default content type for HTML documents. */
	private static final String HTML_FILE_CONTENT_TYPE = "HTML Document";

	/**
	 * Default Logger for this class.
	 */
	private static Logger logger = Logger.getLogger(HtmlFileParser.class);

	/**
	 * DOM Parser.
	 */
	private DOMParser parser = new DOMParser();

	/**
	 * Fulltext.
	 */
	private String fulltext = "";

	/**
	 * Collection of Attribute objects.
	 */
	private Collection<Attribute> attributes;

	/**
	 * Collection of Links.
	 */
	private Collection<String> links;

	/**
	 * Parse a HTML Document from an InputStream.
	 * 
	 * @param in
	 *            The InputStream
	 * @throws FileParserException
	 *             if the Document could not be parsed
	 */
	public void parse(InputStream in) throws FileParserException {
		fulltext = "";
		attributes = new ArrayList<Attribute>();
		links = new ArrayList<String>();
		try {
			parser.parse(new InputSource(in));
			parse(parser.getDocument());
		} catch (SAXException e) {
			throw new FileParserException("Cannot parse HTML Document", e);
		} catch (IOException e) {
			throw new FileParserException("Cannot parse HTML Document", e);
		}
	}

	/**
	 * Parse a Node.
	 * 
	 * @param node
	 *            A Node
	 */
	private void parse(Node node) {
		// System.out.println(indent + node.getClass().getName() + "-"
		// + node.getNodeType() + "-" + node.getNodeValue());
		handleNode(node);
		Node child = node.getFirstChild();
		while (child != null) {
			parse(child);
			child = child.getNextSibling();
		}
	}

	/**
	 * Handle a Node.
	 * 
	 * @param node
	 *            A Node
	 */
	private void handleNode(Node node) {
		if (node instanceof Text) {
			handleText((Text) node);
		} else if (node instanceof HTMLMetaElement) {
			handleMetaTag((HTMLMetaElement) node);
		} else if (node instanceof HTMLTitleElement) {
			handleTitleTag((HTMLTitleElement) node);
		} else if (node instanceof HTMLAnchorElement) {
			handleAnchorTag((HTMLAnchorElement) node);
		}
	}

	/**
	 * Handle a HTMLAnchorElement.
	 * 
	 * @param element
	 *            A HTMLAnchorElement
	 */
	private void handleAnchorTag(HTMLAnchorElement element) {
		if (!"".equals(element.getHref()) && !element.getHref().startsWith("#")) {
			links.add(element.getHref());
		}
	}

	/**
	 * Handle a HTMLTitleElement.
	 * 
	 * @param element
	 *            A HTMLTitleElement
	 */
	private void handleTitleTag(HTMLTitleElement element) {
		attributes.add(new Attribute("TITLE", element.getText(),
				Attribute.ATTRIBUTE_TYPE_TEXT, true));
	}

	/**
	 * Handle a HTMLMetaElement.
	 * 
	 * @param element
	 *            A HTMLMetaElement
	 */
	private void handleMetaTag(HTMLMetaElement element) {
		if (!"".equals(element.getName())) {
			attributes.add(new Attribute(element.getName().toUpperCase(),
					element.getContent(), Attribute.ATTRIBUTE_TYPE_TEXT, true));
		}
	}

	/**
	 * Handle a Text element.
	 * 
	 * @param text
	 *            A Text element
	 */
	private void handleText(Text text) {
		fulltext = fulltext + filterCommentsAndScript(text.getNodeValue());
	}

	private String filterCommentsAndScript(String nodeValue) {
		String text = "";
		// text = filterElements(nodeValue,"<!--", "-->");
		// text = filterElements(text,"<script", "</script>");

		int begin = nodeValue.indexOf("<!--");
		int end = nodeValue.indexOf("-->");
		if (begin == -1 && end == -1) {
			text = nodeValue;
		} else if (begin > -1) {
			text = text + nodeValue.substring(0, begin);
			if (end > -1 && (end + 3) <= nodeValue.length()) {
				text = text + nodeValue.substring(end + 3);
			}
		}
		return text;
	}

	/**
	 * Method implementation from {@link FileParser} interface.
	 * 
	 * @see net.sf.iqser.plugin.file.parser.FileParser#getContent(String,
	 *      InputStream)
	 */
	public Content getContent(String fileName, InputStream inputStream)
			throws FileParserException {
		logger.info("Parsing file " + fileName);

		// Create a new Content
		Content content = new Content();

		// Set the content type for a unknown format (file extention +
		// 'Document')
		content.setType(HTML_FILE_CONTENT_TYPE);

		// Set the file name attribute. This Attribute is no key.
		content.addAttribute(new Attribute("FILENAME", fileName,
				Attribute.ATTRIBUTE_TYPE_TEXT, false));

		// Set the title attribute. This Attribute is a key.
		content.addAttribute(new Attribute("TITLE", FileParserUtils
				.getFileTitle(fileName), Attribute.ATTRIBUTE_TYPE_TEXT, true));

		// Parse the HTML file
		parse(inputStream);

		// Set the fulltext to content
		content.setFulltext(fulltext);

		// Append the additional Attributes from HTML document
		Collection<Attribute> htmlAttributes = attributes;
		for (Attribute a : htmlAttributes) {

			Attribute contentAttribute = content
					.getAttributeByName(a.getName());
			if (contentAttribute == null) {
				content.addAttribute(a);
			} else {
				contentAttribute.setValue(a.getValue());
				contentAttribute.setType(a.getType());
				contentAttribute.setKey(a.isKey());
			}
		}

		return content;
	}
}
