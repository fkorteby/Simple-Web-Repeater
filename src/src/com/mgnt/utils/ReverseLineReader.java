/*
 * 
 */
package com.mgnt.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

// TODO: Auto-generated Javadoc
/**
 * Read a file from end to start.
 *
 * @author Crunchify.com
 */

public class ReverseLineReader {
	
	/** The Constant BUFFER_SIZE. */
	private static final int BUFFER_SIZE = 8192;
	
	/** The channel. */
	private final FileChannel channel;
	
	/** The encoding. */
	private final String encoding;
	
	/** The file pos. */
	private long filePos;
	
	/** The buf. */
	private ByteBuffer buf;
	
	/** The buf pos. */
	private int bufPos;
	
	/** The baos. */
	private ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	/** The raf. */
	private RandomAccessFile raf;
	
	/** The last line break. */
	private byte lastLineBreak = '\n';

	/**
	 * Instantiates a new reverse line reader.
	 *
	 * @param file the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ReverseLineReader(File file) throws IOException {
		this(file, null);
	}

	/**
	 * Instantiates a new reverse line reader.
	 *
	 * @param file the file
	 * @param encoding the encoding
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ReverseLineReader(File file, String encoding) throws IOException {
		raf = new RandomAccessFile(file, "r");
		channel = raf.getChannel();
		filePos = raf.length();
		this.encoding = encoding;
	}

	/**
	 * Close.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void close() throws IOException {
		raf.close();
	}

	/**
	 * Read line.
	 *
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String readLine() throws IOException {
		byte c;
		while (true) {
			if (bufPos < 0) {
				if (filePos == 0) {
					if (baos == null) {
						return null;
					}
					String line = bufToString();
					baos = null;
					return line;
				}

				long start = Math.max(filePos - BUFFER_SIZE, 0);
				long end = filePos;
				long len = end - start;

				buf = channel.map(FileChannel.MapMode.READ_ONLY, start, len);
				bufPos = (int) len;
				filePos = start;

				// Ignore Empty New Lines
				c = buf.get(--bufPos);
				if (c == '\r' || c == '\n')
					while (bufPos > 0 && (c == '\r' || c == '\n')) {
						bufPos--;
						c = buf.get(bufPos);
					}
				if (!(c == '\r' || c == '\n'))
					bufPos++;// IS THE NEW LENE
			}

			/*
			 * This will ignore all blank new lines.
			 */
			while (bufPos-- > 0) {
				c = buf.get(bufPos);
				if (c == '\r' || c == '\n') {
					// skip \r\n
					while (bufPos > 0 && (c == '\r' || c == '\n')) {
						c = buf.get(--bufPos);
					}
					// restore cursor
					if (!(c == '\r' || c == '\n'))
						bufPos++;// IS THE NEW Line
					return bufToString();
				}
				baos.write(c);
			}

			
			 /* If you don't want to ignore new line and would like to print new
			 * line too then use below code and comment out above while loop
			 */
			while (bufPos-- > 0) {
				byte c1 = buf.get(bufPos);
				if (c1 == '\r' || c1 == '\n') {
					if (c1 != lastLineBreak) {
						lastLineBreak = c1;
						continue;
					}
					lastLineBreak = c1;
					return bufToString();
				}
				baos.write(c1);
			}
			 

		}
	}

	/**
	 * Buf to string.
	 *
	 * @return the string
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	private String bufToString() throws UnsupportedEncodingException {
		if (baos.size() == 0) {
			return "";
		}

		byte[] bytes = baos.toByteArray();
		for (int i = 0; i < bytes.length / 2; i++) {
			byte t = bytes[i];
			bytes[i] = bytes[bytes.length - i - 1];
			bytes[bytes.length - i - 1] = t;
		}

		baos.reset();
		if (encoding != null)
			return new String(bytes, encoding);
		else
			return new String(bytes);
	}
}
