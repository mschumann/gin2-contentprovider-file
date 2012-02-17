package net.sf.iqser.plugin.filesystem;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import net.sf.iqser.plugin.file.parser.FileParser;
import net.sf.iqser.plugin.file.parser.FileParserException;
import net.sf.iqser.plugin.file.parser.FileParserFactory;
import net.sf.iqser.plugin.file.parser.zip.ZipFileModel;
import net.sf.iqser.plugin.filesystem.utils.ContentUpdate;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.iqser.core.event.Event;
import com.iqser.core.exception.IQserException;
import com.iqser.core.exception.IQserRuntimeException;
import com.iqser.core.model.Content;
import com.iqser.core.model.Parameter;
import com.iqser.core.plugin.provider.AbstractContentProvider;
import com.iqser.core.plugin.provider.ContentProvider;

/**
 * file system content provider.
 * 
 * @author alexandru.galos
 * 
 */
public class FilesystemContentProvider extends AbstractContentProvider implements ContentProvider {

	/**
	 * Default Logger for this class.
	 */
	private static Logger logger = Logger.getLogger(FilesystemContentProvider.class);

	/**
	 * UID.
	 */
	private static final long serialVersionUID = 6781181225882526721L;

	/**
	 * map for new attribute---for replacing the name of the attributes.
	 */
	private final Map<String, String> attributeMappings = new HashMap<String, String>();

	/**
	 * a collection of new key attributes.
	 */
	private Collection<String> keyAttributesList = new ArrayList<String>();

	/**
	 * get the binary data from a content.
	 * 
	 * @param content
	 *            the content for which to extract the binary data
	 * @return an array of bytes
	 */
	@Override
	public byte[] getBinaryData(Content content) {
		logger.debug("getBinaryData( Content content=" + content + ") - end - return value=" + null);

		try {
			String contentURL = content.getContentUrl();
			if (contentURL.startsWith("zip://"))
				return extractBinaryPackedFiles(content);
			else if (getFile(contentURL) != null)
				return extractBinaryUnpackedFiles(content);
		} catch (IOException ioe) {
			throw new IQserRuntimeException(ioe);
		}
		// else throw exception
		throw new IQserRuntimeException("No files for content found");
	}

	/**
	 * extract the binary data from a content that contains a zip entry.
	 * 
	 * @param content
	 * @return an array of bytes representing the content data
	 * @throws IOException
	 */
	private byte[] extractBinaryPackedFiles(Content content) throws IOException {
		ZipFileModel zfm = getZipFileModel(content.getContentUrl());
		ZipFile zipFile = zfm.getZipFile();
		ZipEntry zipEntry = zfm.getZipEntry();
		InputStream inputStream = zipFile.getInputStream(zipEntry);

		return IOUtils.toByteArray(inputStream);
	}

	/**
	 * extract the binary data from the content that is unpacked.
	 * 
	 * @param content
	 *            the unpacked content for which to extract binary content
	 * @return an array of bytes representing the content data
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private byte[] extractBinaryUnpackedFiles(Content content) throws FileNotFoundException, IOException {
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
		while ((offset < bytes.length) && ((numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IQserRuntimeException("Could not completely read file " + f.getName());
		}

		is.close();
		return bytes;
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
	@Override
	public void destroy() {
		// Nothing to do
	}

	/**
	 * erases the content objects from the object graph if the corresponding
	 * files are no longer on the file system.
	 */
	@Override
	public void doHousekeeping() {
		// collection of source URLs
		Collection<String> sourceContentUrls = getContentUrls();
		Collection<Content> existingContents;
		try {
			existingContents = getExistingContents();
		} catch (IQserException e) {
			throw new IQserRuntimeException(e);
		}

		if (existingContents != null) {
			for (Content content : existingContents) {
				try {
					String contentUrl = content.getContentUrl();
					if (!sourceContentUrls.contains(contentUrl))
						removeContent(content.getContentUrl());
				} catch (IQserException e) {
					logger.error("Error while doing Housekeeping: ", e);
				}
			}
		}
	}

	/**
	 * adds or updates the content of the object graph when a file is added or
	 * modified.
	 */
	@Override
	public void doSynchronization() {

		/**
		 * synchronize file system against the object graph - if a file is new
		 * INSERT in Object Graph - if a file has been modified ( see file
		 * LAST_MODIFIED), UPDATE Object Graph - the Content Object from
		 * ObjectGraph that do not have a coresponding file in the file system
		 * will be DELETE from the Object Graph
		 */

		// get the object graph content URLs
		Collection<Content> existingContents = null;
		try {
			existingContents = getExistingContents();
		} catch (IQserException e) {
			throw new IQserRuntimeException(e);
		}

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
		Collection newSourceContentUrls = new ArrayList();
		newSourceContentUrls.addAll(sourceContentUrls);
		newSourceContentUrls.removeAll(objectGraphContentUrls);

		for (Object contentUrl : newSourceContentUrls) {
			logger.info("Synch - add conntent " + contentUrl);
			try {
				addContent(getContent((String) contentUrl));
			} catch (Throwable t) {
				// Make sure to catch everthing to continue with next Content
				logger.error("Could not add content.", t);
			}
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
					try {
						File file = getFile(contentUrl);
						if (file != null) {
							long lastModified = file.lastModified();
							long contentLastModified = content.getModificationDate();
							if (lastModified > contentLastModified) {
								logger.info("Synch - delete update " + contentUrl);
								try {
									updateContent(getContent(file.getAbsolutePath()));
								} catch (Throwable t) {
									// Make sure to catch everthing to continue
									// with next Content
									logger.error("Could not update content.", t);
								}
							}
						} else {
							ZipFileModel zfm = getZipFileModel(contentUrl);

							boolean isModified = zfm.getZipEntry().getTime() > content.getModificationDate();
							if (isModified) {
								try {
									updateContent(getContent(contentUrl));
								} catch (Throwable t) {
									// Make sure to catch everthing to continue
									// with next Content
									logger.error("Could not update content.", t);
								}
							}

						}
					} catch (Exception e) {
						logger.error("Error while performing synch: ", e);
					}
				}

			}
		}

	}

	/**
	 * Return "save" and "delete".
	 * 
	 * @param content
	 *            A Content
	 * 
	 * @return a collection of String actions
	 */
	@Override
	public Collection getActions(Content content) {
		String[] actions = new String[] { "delete", "save" };
		return Arrays.asList(actions);
	}

	/**
	 * extracts the zip data from a zip content.
	 * 
	 * @param zipFileName
	 * @return the inputstream of the content
	 */
	private InputStream getInputStreamForZipContent(String zipFileName) {

		InputStream is = null;

		try {

			ZipFileModel zfm = getZipFileModel(zipFileName);
			is = zfm.getZipFile().getInputStream(zfm.getZipEntry());

		} catch (IOException e) {

			throw new IQserRuntimeException(e);
		}

		return is;
	}

	/**
	 * get the zip file model that contains a zipfile and an entry.
	 * 
	 * @param zipFileName
	 *            the name of the zip file
	 * @return the zip file model
	 * @throws IOException
	 *             the exception
	 */
	public ZipFileModel getZipFileModel(String zipFileName) throws IOException {

		int index = zipFileName.indexOf(".zip!");

		if (index != -1) {
			ZipFileModel zfm = new ZipFileModel();
			index += ".zip".length();
			String fileName = zipFileName.substring(index + 2);
			String zipFilePath = zipFileName.substring("zip://".length(), index);
			ZipFile zipFile = new ZipFile(zipFilePath);
			zfm.setZipFile(zipFile);

			ZipEntry entry = zipFile.getEntry(fileName);
			zfm.setZipEntry(entry);

			return zfm;
		} else
			throw new IQserRuntimeException("Invalid zip url");
	}

	/**
	 * creates a content from an url.
	 * 
	 * @param contentUrl
	 *            the content url
	 * @return the created content
	 */
	@Override
	public Content getContent(String contentUrl) {

		Content content = null;

		FileParserFactory parserFactory = FileParserFactory.getInstance();

		ContentUpdate cu = new ContentUpdate();

		InputStream inputStream = null;

		if (contentUrl.contains(".zip") && !contentUrl.endsWith(".zip"))
			inputStream = getInputStreamForZipContent(contentUrl);
		else if (new File(contentUrl).exists()) {
			try {
				inputStream = new FileInputStream(contentUrl);
			} catch (IOException e1) {
				throw new IQserRuntimeException(e1);
			}
		} else {
			throw new IQserRuntimeException("The content does not have url");
		}
		try {
			if (inputStream != null) {
				FileParser parser = parserFactory.getFileParser(contentUrl);

				content = parser.getContent(contentUrl, inputStream);
				content.setProvider(this.getId());
				content.setContentUrl(contentUrl);

				File file = getFile(contentUrl);

				if (file != null) {
					long lastModified = file.lastModified();
					content.setModificationDate(lastModified);
				}
				cu.updateAttributes(content, attributeMappings);
				cu.updateKeyAttributes(content, keyAttributesList);
			} else
				throw new IQserRuntimeException("Input stream from file " + contentUrl + " is null");
		} catch (FileParserException e) {
			throw new IQserRuntimeException(e);
		}

		return content;

	}

	/**
	 * creates a content from an inputstream.
	 * 
	 * @param inputStream
	 *            the inputstream of the file
	 * @return content the created content
	 */
	@Override
	public Content getContent(InputStream inputStream) {

		FileParserFactory parserFactory = FileParserFactory.getInstance();

		ContentUpdate cu = new ContentUpdate();
		Content content = null;

		try {

			// workaround (another solution would be reset the input stream
			byte[] bytes = IOUtils.toByteArray(inputStream);
			InputStream is2 = new ByteArrayInputStream(bytes);
			FileParser parser;
			parser = parserFactory.getFileParser(is2);

			try {
				if (parser != null) {
					content = parser.getContent(null, new ByteArrayInputStream(bytes));

					content.setProvider(this.getId());

					// update the attributes with the ones from the
					// initialization parameters
					cu.updateAttributes(content, attributeMappings);
					cu.updateKeyAttributes(content, keyAttributesList);
				} else
					throw new IQserRuntimeException("There are no parsers for zip archives.");

			} catch (FileParserException e) {
				throw new IQserRuntimeException(e);
			}
		} catch (IOException e) {
			throw new IQserRuntimeException(e);
		}
		return content;
	}

	/**
	 * extracts the urls that are available for creating contents.
	 * 
	 * @return a collection of string representing the urls of the files
	 */
	@Override
	public Collection getContentUrls() {

		Properties params = getInitParams();
		// get the filters from the initialization parameters
		String filter = (String) params.get("filter-pattern");
		Collection filterFileTypes = extractConfigAttributes(filter);

		String filterFolderInclude = (String) params.get("filter-folder-include");
		Collection includedFolders = null;
		if (filterFolderInclude != null)
			includedFolders = extractConfigAttributes(filterFolderInclude);
		else
			includedFolders = Collections.EMPTY_LIST;

		String filterFolderExclude = (String) params.get("filter-folder-exclude");
		Collection excludedFolders = null;
		if (filterFolderExclude != null)
			excludedFolders = extractConfigAttributes(filterFolderExclude);
		else
			excludedFolders = Collections.EMPTY_LIST;

		String folder = (String) params.get("folder");
		Collection folders = extractConfigAttributes(folder);

		// create path filter
		AcceptedPathFilter apf = new AcceptedPathFilter();

		for (Object includedFolder : includedFolders) {
			apf.addAcceptedPath((String) includedFolder);
		}

		for (Object excludedFolder : excludedFolders) {
			apf.addDeniedPath((String) excludedFolder);
		}

		FileScanner fs = new FileScanner(folders, apf);

		// create file filter
		AcceptFileFilter aff = new AcceptFileFilter();
		for (Object fileType : filterFileTypes) {
			aff.addAcceptedFiletype((String) fileType);
		}

		// get all the files that are valid using the filter
		Collection files = fs.scanFiles(aff);

		return files;
	}

	/**
	 * initializes the parameters of the content object.
	 */
	@Override
	public void init() {

		// parse the JSON of attribute mappings
		Properties params = getInitParams();
		// the name of the parameter is attribute.mappings
		String mappings = (String) params.get("attribute.mappings");
		JSONObject json = null;
		try {
			json = new JSONObject(mappings);
		} catch (JSONException e) {
			throw new IQserRuntimeException(e);
		}

		Iterator keys = json.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			try {
				String value = (String) json.get(key);
				attributeMappings.put(key, value);
			} catch (JSONException e) {
				throw new IQserRuntimeException(e);
			}

		}

		String keyAttributes = (String) params.get("key-attributes");

		keyAttributesList = extractConfigAttributes(keyAttributes);

	}

	/**
	 * extracts the configuration attributes for key attributes.
	 * 
	 * @param keyAttributes
	 *            a string of the form [keyAttr1][keyAttr2]
	 * @return a collection of the new key attributes
	 */
	private Collection extractConfigAttributes(String keyAttributes) {

		String regex = "\\s*\\]\\s*\\[\\s*|\\s*\\[\\s*|\\s*\\]\\s*";
		String[] keyAttrs = keyAttributes.trim().split(regex);

		List<String> keyAttributesList = new ArrayList<String>();
		for (String key : keyAttrs) {
			if (key.trim().length() > 0)
				keyAttributesList.add(key);
		}

		return keyAttributesList;
	}

	/**
	 * nothing to do here.
	 * 
	 * @param arg0
	 *            the event
	 */
	@Override
	public void onChangeEvent(Event arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * perform save or delete action on the object graph and also on the file
	 * system.
	 * 
	 * @param action
	 *            the type of action which can be delete of save
	 * @param content
	 *            the content that is deleted or saved
	 */
	@Override
	public void performAction(String action, Content content) {

		Collection<String> actions = this.getActions(content);
		if (actions.contains(action)) {
			if (action.equalsIgnoreCase("delete")) {
				performDeleteAction(content);
			} else if (action.equalsIgnoreCase("save")) {
				performSaveAction(content);
			}
		}

	}

	/**
	 * perform save action on a content object a save action saves the file on
	 * the file system if it is a text document or a text document in a zip
	 * file.
	 * 
	 * @param content
	 *            the content that is saved
	 */
	private void performSaveAction(Content content) {

		String contentUrl = content.getContentUrl();

		if (contentUrl == null || contentUrl.trim().length() == 0)
			throw new IQserRuntimeException("Content " + content.getContentId() + " does not have url");
		if (content.getType().equalsIgnoreCase("Text Document")) {
			if (contentUrl.startsWith("zip://")) {
				int zipFileIndexEnd = contentUrl.indexOf(".zip") + 4;
				String zipPath = contentUrl.substring("zip://".length(), zipFileIndexEnd);
				String zipEntry = contentUrl.substring(zipFileIndexEnd + 2);
				String text = content.getFulltext();
				// update zip entry
				updateZipEntry(zipPath, zipEntry, text.getBytes(), true);

			} else {

				File file = new File(contentUrl);

				// get the binary content of the object from the content graph
				try {
					FileOutputStream out = new FileOutputStream(file);
					IOUtils.write(content.getFulltext(), out);
					IOUtils.closeQuietly(out);
				} catch (IOException e) {
					throw new IQserRuntimeException(e);
				}
			}
		}
		try {
			if (isExistingContent(contentUrl))
				updateContent(content);
			else
				addContent(content);
		} catch (IQserException e) {
			throw new IQserRuntimeException(e);
		}

	}

	/**
	 * deletes a content object from the object graph and also from the file
	 * system.
	 * 
	 * @param content
	 *            the content that is deleted
	 */
	private void performDeleteAction(Content content) {

		String contentUrl = content.getContentUrl();

		if (contentUrl == null || contentUrl.trim().length() == 0)
			throw new IQserRuntimeException("Content " + content.getContentId() + " does not have url");
		// if zip file
		if (contentUrl.startsWith("zip://")) {
			int zipFileIndexEnd = contentUrl.indexOf(".zip") + 4;
			String zipPath = contentUrl.substring("zip://".length(), zipFileIndexEnd);
			String zipEntry = contentUrl.substring(zipFileIndexEnd + 2);
			String text = content.getFulltext();
			// delete zip entry
			updateZipEntry(zipPath, zipEntry, text.getBytes(), false);

		} else {
			File file = new File(contentUrl);
			if (file.exists() && !file.isDirectory()) {
				boolean isDeleted = file.delete();
				if (!isDeleted) {
					logger.warn(" File not deleted from filesystem " + content.getContentUrl());
				}
			}
		}

		try {
			removeContent(contentUrl);
		} catch (IQserException e) {
			throw new IQserRuntimeException(e);
		}

	}

	/**
	 * updates the zip entry of a zip file.
	 * 
	 * @param zipPath
	 *            the path of the zip file
	 * @param entryName
	 *            the entry name from the zip file
	 * @param content
	 *            the content that is updates
	 * @param replaceOrDelete
	 *            the operation that is performed
	 */
	private void updateZipEntry(String zipPath, String entryName, byte[] content, boolean replaceOrDelete) {
		try {
			// read war.zip and write to append.zip
			ZipFile war = new ZipFile(zipPath);
			// create a temp file
			File tempFile = File.createTempFile("FileSystemContentProvider", "updateZip");
			ZipOutputStream append = new ZipOutputStream(new FileOutputStream(tempFile));

			// first, copy contents from existing war
			Enumeration<? extends ZipEntry> entries = war.entries();
			while (entries.hasMoreElements()) {
				ZipEntry e = entries.nextElement();
				if (!e.isDirectory()) {
					if (e.getName().equalsIgnoreCase(entryName)) {
						if (replaceOrDelete) {
							// replace
							logger.debug("replace: " + e.getName());
							ZipEntry newEntry = new ZipEntry(entryName);
							append.putNextEntry(newEntry);
							append.write(content);
						} else {
							// delete
							logger.debug("deleting: " + e.getName());
						}
					} else {
						// copy others
						append.putNextEntry(new ZipEntry(e.getName()));
						copy(war.getInputStream(e), append);
					}
				}
				append.closeEntry();
			}
			// close
			war.close();
			append.close();

			// replace zip file
			File destFile = new File(zipPath);
			destFile.delete();

			// tempFile.renameTo(new File(zipPath));
			FileInputStream in = new FileInputStream(tempFile);
			FileOutputStream out = new FileOutputStream(destFile);

			IOUtils.copy(in, out);
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);

		} catch (Exception e) {
			throw new IQserRuntimeException(e);
		}

	}

	/**
	 * copies the input stream in output stream.
	 * 
	 * @param input
	 *            the input stream
	 * @param output
	 *            the outputstream
	 * @throws IOException
	 *             exception
	 */
	private void copy(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[4096 * 1024];
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
	}

	@Override
	public void performAction(String arg0, Collection<Parameter> arg1, Content arg2) {
		// TODO Auto-generated method stub

	}

}
