package net.sf.iqser.plugin.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import net.sf.iqser.plugin.file.parser.pdf.PdfFileParser;

import org.apache.log4j.Logger;

import com.iqser.core.event.Event;
import com.iqser.core.exception.IQserRuntimeException;
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

	public byte[] getBinaryData(Content content) {
		logger.debug("getBinaryData( Content content=" + content
				+ ") - end - return value=" + null);
		byte[] data;

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
	public File getFile(String url) {
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
		// TODO Auto-generated method stub

	}

	/**
	 * Return always null, because this ContentProvider doesn't support any
	 * Actions.
	 * 
	 * @param content
	 *            A Content
	 * 
	 * @return null
	 */
	public Collection getActions(Content content) {
		return null;
	}

	@Override
	public Content getContent(String contentUrl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Content getContent(InputStream inputStream) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection getContentUrls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onChangeEvent(Event arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void performAction(String arg0, Content arg1) {
		// TODO Auto-generated method stub

	}

}
