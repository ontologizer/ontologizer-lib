package de.ontologizer.immutable.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.zip.GZIPInputStream;

/**
 * Implementation of {@link ReaderInput} from {@link File}s.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class FileReaderInput implements ReaderInput {

	/** The {@link File} to read from. */
	private final File inputFile;

	/** The {@link InputStream} to read from. */
	private InputStream inputStream;

	/** The {@link FileChannel} to read from. */
	private FileChannel fileChannel;

	/**
	 * Construct with path to file.
	 * 
	 * @param filePath
	 *            Path to file to open for reading.
	 */
	public FileReaderInput(String filePath) {
		this(new File(filePath));
	}

	/**
	 * Construct with input {@link File}.
	 * 
	 * @param inputFile
	 *            {@link File} to read from.
	 */
	public FileReaderInput(File inputFile) {
		this.inputFile = inputFile;

		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(this.inputFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not find file for opening", e);
		}

		// Try to open as gzip file and fall back to uncompressed.
		try {
			this.inputStream = new GZIPInputStream(fileInputStream);
		} catch (IOException exp) {
			try {
				fileInputStream.close();
				this.inputStream = new FileInputStream(this.inputFile);
			} catch (IOException e) {
				throw new RuntimeException("Problem with opening input file", e);
			}
			inputStream = fileInputStream;
		}

		this.fileChannel = fileInputStream.getChannel();
	}

	@Override
	public InputStream inputStream() {
		return inputStream;
	}

	@Override
	public void close() {
		if (fileChannel != null) {
			try {
				fileChannel.close();
				inputStream.close();
			} catch (IOException e) {
				// XXX add to interface and document
				throw new RuntimeException("Could not close file channel", e);
			}
			fileChannel = null;
			inputStream = null;
		}
	}

	@Override
	public int getSize() {
		try {
			return (int) fileChannel.size();
		} catch (IOException e) {
			return -1;
		}
	}

	@Override
	public int getPosition() {
		try {
			return (int) fileChannel.position();
		} catch (IOException e) {
			return -1;
		}
	}

	@Override
	public String getFilename() {
		return inputFile.getName();
	}

}
