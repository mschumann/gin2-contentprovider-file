package net.sf.iqser.plugin.filesystem;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.iqser.plugin.file.parser.FileParser;
import net.sf.iqser.plugin.file.parser.FileParserException;
import net.sf.iqser.plugin.file.parser.FileParserFactory;
import net.sf.iqser.plugin.file.parser.pdf.PdfFileParser;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.iqser.core.event.Event;
import com.iqser.core.exception.IQserException;
import com.iqser.core.exception.IQserRuntimeException;
import com.iqser.core.model.Attribute;
import com.iqser.core.model.Content;
import com.iqser.core.plugin.AbstractContentProvider;

public class FilesystemContentProvider extends AbstractContentProvider {

	/**
	 * Default Logger for this class.
	 */
	private static Logger logger = Logger.getLogger(PdfFileParser.class);

	/**
	 * UID.
	 */
	private static final long serialVersionUID = 6781181225882526721L;

	private Map<String, String> attributeMappings = new HashMap<String, String>();

	public byte[] getBinaryData(Content content) {
		logger.debug("getBinaryData( Content content=" + content
				+ ") - end - return value=" + null);

		try {
			File f = getFile(content.getContentUrl());
			InputStream is = new FileInputStream(f);

			// Get the size of the file
			long length = f.length();

			// if file is larger than Integer.MAX_VALUE, we are boned... :-/
			if (length > Integer.MAX_VALUE) {
				logger.error("File too large to handle...");
				return null;
			}

			byte[] bytes = new byte[(int) length];

			// Read in the bytes
			int offset = 0;
			int numRead = 0;
			while ((offset < bytes.length)
					&& ((numRead = is
							.read(bytes, offset, bytes.length - offset)) >= 0)) {
				offset += numRead;
			}

			// Ensure all the bytes have been read in
			if (offset < bytes.length) {
				throw new IQserRuntimeException(
						"Could not completely read file " + f.getName());
			}

			is.close();
			return bytes;
		} catch (IOException ioe) {
			throw new IQserRuntimeException("Error while reading file data: "
					+ ioe.getMessage());
		}
	}

	/**
	 * Returns a File object to the given URL.
	 * 
	 * @param url
	 *            URL of the File.
	 * 
	 * @return A File object or null
	 */
	private File getFile(String url) {
		File f = new File(url);

		if (f.exists()) {
			return f;
		}

		return null;
	}

	/**
	 * Nothing happens here.
	 * 
	 * @see com.iqser.plugin.ContentProvider#destroy()
	 */
	public void destroy() {
		// Nothing to do
	}

	@Override
	public void doHousekeeping() {
		// TODO Auto-generated method stub
	}

	@Override
	public void doSynchonization() {
		/**
		 * synchronize file system against the object graph - if a file is new
		 * INSERT in Object Graph - if a file has been modified ( see file
		 * LAST_MODIFIED), UPDATE Object Graph - the Content Object from
		 * ObjectGraph that do not have a coresponding file in the file system
		 * will be DELETE from the Object Graph
		 */
		try {
			// get the object graph content URLs
			Collection<Content> existingContents = getExistingContents();
			if (existingContents == null) {
				existingContents = new ArrayList<Content>();
			}
			// collection of objectGraph URLs
			Collection<String> objectGraphContentUrls = new ArrayList<String>();
			for (Content content : existingContents) {
				objectGraphContentUrls.add(content.getContentUrl());
			}
			// collection of source URLs
			Collection<String> sourceContentUrls = getContentUrls();

			// handle new files
			Collection<String> newSourceContentUrls = new ArrayList<String>();
			newSourceContentUrls.addAll(sourceContentUrls);
			newSourceContentUrls.removeAll(objectGraphContentUrls);

			for (String contentUrl : newSourceContentUrls) {
				logger.info("Synch - add conntent " + contentUrl);
				addContent(getContent(contentUrl));
			}

			// handle deleted content - content object that do not have a
			// coresponding file in the file system
			Collection<String> deletedContentsUrls = new ArrayList<String>();
			deletedContentsUrls.addAll(objectGraphContentUrls);
			deletedContentsUrls.removeAll(sourceContentUrls);

			for (String contentUrl : deletedContentsUrls) {
				logger.info("Synch - delete conntent " + contentUrl);
				removeContent(contentUrl);
			}

			// handle common files - files that are both in file system and in
			// content object
			Collection<String> commonContentsUrls = new ArrayList<String>();
			commonContentsUrls.addAll(objectGraphContentUrls);
			commonContentsUrls.retainAll(sourceContentUrls);

			for (String contentUrl : commonContentsUrls) {
				for (Content content : existingContents) {
					if (contentUrl.equalsIgnoreCase(content.getContentUrl())) {
						// match file LAST_MODIFIED
						File file = getFile(contentUrl);
						long lastModified = file.lastModified();
						long contentLastModified = content
								.getModificationDate();
						if (lastModified > contentLastModified) {
							logger.info("Synch - delete update " + contentUrl);
							performContentUpdate(content, file);
							updateContent(content);
						}
					}
				}
			}

		} catch (IQserException e) {
			throw new IQserRuntimeException("Error while do synch: "
					+ e.getMessage());
		}

	}

	private void performContentUpdate(Content content, File file) {

		String path = file.getAbsolutePath();
		Content newContent = getContent(path);

		Collection<Attribute> attributes = newContent.getAttributes();

		for (Attribute attribute : attributes) {
			String name = attribute.getName();
			Attribute attributeByName = content.getAttributeByName(name);
			if (attributeByName != null) {
				attributeByName.setName(name);
				attributeByName.setKey(attribute.isKey());
				attributeByName.setValue(attribute.getValue());
			}
		}

		content.setFulltext(newContent.getFulltext());
	}

	/**
	 * Return "save" and "delete"
	 * 
	 * @param content
	 *            A Content
	 * 
	 * @return a collection of String actions
	 */
	public Collection getActions(Content content) {
		String[] actions = new String[] { "delete", "save" };
		return Arrays.asList(actions);
	}

	@Override
	public Content getContent(String contentUrl) {

		FileParserFactory parserFactory = FileParserFactory.getInstance();
		FileParser parser = parserFactory.getFileParser(contentUrl);
		Content content = null;

		
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(contentUrl);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			content = parser.getContent(contentUrl, inputStream);
			content.setProvider(this.getId());
			content.setContentUrl(contentUrl);
			Attribute attributeM = new Attribute();
			attributeM.setName("BYTES_CONTENT");
			byte[] data = this.getBinaryData(content);
			attributeM.setValue(encodeBinaryToString(data));
			content.addAttribute(attributeM);

			File file = getFile(contentUrl);

			if (file != null) {
				long lastModified = file.lastModified();
				content.setModificationDate(lastModified);
				updateAttributes(content);
			
			}
		} catch (FileParserException e) {
			e.printStackTrace();
		}
		return content;
	}

	private void updateAttributes(Content content) {

		Collection<Attribute> attributes = content.getAttributes();

		for (Attribute attribute : attributes) {
			String name = attribute.getName();
			if (attributeMappings.containsKey(name)) {
				name = attributeMappings.get(name);
				attribute.setName(name);
			}

		}

	}

	@Override
	public Content getContent(InputStream inputStream) {

		FileParserFactory parserFactory = FileParserFactory.getInstance();

		Content content = null;
		try {

			//workaround (another solution would be reset the input stream
			byte[] bytes = IOUtils.toByteArray(inputStream);
			InputStream is2 = new ByteArrayInputStream(bytes);
			FileParser parser = parserFactory.getFileParser(is2);

			try {
				content = parser.getContent(null, new ByteArrayInputStream(
						bytes));

				content.setProvider(this.getId());

				//get the bytes content
				Attribute attributeM = new Attribute();
				attributeM.setName("BYTES_CONTENT");
				attributeM.setValue(encodeBinaryToString(bytes));
				content.addAttribute(attributeM);

				//update the attributes with the ones from the 
				//initialization parameters
				updateAttributes(content);

			} catch (FileParserException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	@Override
	public Collection getContentUrls() {

		Properties params = getInitParams();
		//get the filters from the initialization parameters
		String filter = (String) params.get("filter-pattern");
		String filterFolderInclude = (String) params
				.get("filter-folder-include");
		String filterFolderExclude = (String) params
				.get("filter-folder-exclude");
		String folder = (String) params.get("folder");

		List<String> folders = new ArrayList<String>();
		folders.add(folder);
		
		//create path filter
		AcceptedPathFilter apf = null;
		if (filterFolderInclude != null || filterFolderExclude != null) {
			apf = new AcceptedPathFilter();
			apf.addAcceptedPath(filterFolderInclude);
			apf.addDeniedPath(filterFolderExclude);
		}
		FileScanner fs = new FileScanner(folders, apf);

		//create file filter
		AcceptFileFilter aff = null;
		if (filter != null) {
			aff = new AcceptFileFilter();
			aff.addAcceptedFiletype(filter);
		}

		//get all the files that are valid using the filter
		Collection files = fs.scanFiles(aff);

		return files;
	}

	@Override
	public void init() {

		//parse the JSON of attribute mappings
		Properties params = getInitParams();
		//the name of the parameter is attribute.mappings
		String mappings = (String) params.get("attribute.mappings");
		JSONObject json = null;
		try {
			json = new JSONObject(mappings);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new IQserRuntimeException("Invalid JSON string");
		}

		Iterator keys = json.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			try {
				String value = (String) json.get(key);
				attributeMappings.put(key, value);
			} catch (JSONException e) {
				throw new IQserRuntimeException("Invalid JSON string");
			}

		}

	}

	@Override
	public void onChangeEvent(Event arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void performAction(String arg0, Content arg1) {

		Collection<String> actions = this.getActions(arg1);
		if (actions.contains(arg0)) {
			if (arg0.equalsIgnoreCase("delete")) {
				performDeleteAction(arg1);
			} else if (arg0.equalsIgnoreCase("save")) {
				performSaveAction(arg1);
			}
		}

	}

	private void performSaveAction(Content arg1) {

		String contentUrl = arg1.getContentUrl();

		if (contentUrl == null || contentUrl.trim().length() == 0)
			throw new IQserRuntimeException("Content " + arg1.getContentId()
					+ " does not have url");

		File file = new File(contentUrl);

		//get the binary content of the object from the content graph
		Attribute attribute = arg1.getAttributeByName("BYTES_CONTENT");
		byte[] bytes = decodeStringToBinary(attribute.getValue());
		if (bytes != null) {
			try {
				IOUtils.write(bytes, new FileOutputStream(file));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		addContent(arg1);

	}

	private void performDeleteAction(Content arg1) {

		String contentUrl = arg1.getContentUrl();

		if (contentUrl == null || contentUrl.trim().length() == 0)
			throw new IQserRuntimeException("Content " + arg1.getContentId()
					+ " does not have url");

		File file = new File(contentUrl);
		if (file.exists() && !file.isDirectory())
			file.delete();

		try {
			removeContent(contentUrl);
		} catch (IQserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String encodeBinaryToString(byte[] byteContent) {
		char[] charContent = new char[byteContent.length];
		for (int i = 0; i < charContent.length; i++) {
			charContent[i] = (char) byteContent[i];
		}
		return new String(charContent);
	}

	private byte[] decodeStringToBinary(String s) {
		if (s == null)
			return null;

		char[] charContent = s.toCharArray();
		byte[] byteContent = new byte[charContent.length];
		for (int i = 0; i < charContent.length; i++) {
			byteContent[i] = (byte) charContent[i];
		}

		return byteContent;
	}

}
