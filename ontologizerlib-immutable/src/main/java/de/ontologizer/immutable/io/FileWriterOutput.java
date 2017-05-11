package de.ontologizer.immutable.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Implementation of {@link WriterOutput} for writing to {@link File}s.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class FileWriterOutput implements WriterOutput {

	/** The {@link File} to write to. */
	private final File outputFile;

	/** The {@link InputStream} to write to. */
	private OutputStream outputStream;

	/**
	 * Construct with path to file.
	 * 
	 * @param filePath
	 *            Path to file to open for writing.
	 */
	public FileWriterOutput(String filePath) {
		this(new File(filePath));
	}

	/**
	 * Construct with output {@link File}.
	 * 
	 * @param outputFile
	 *            {@link File} to write to.
	 */
	public FileWriterOutput(File outputFile) {
		this.outputFile = outputFile;

		FileOutputStream fileOutputStream;
		try {
			fileOutputStream = new FileOutputStream(this.outputFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not find file for opening", e);
		}

		// Try to open as gzip file if ends in ".gz"
		if (outputFile.getName().endsWith(".gz")) {
			try {
				this.outputStream = new GZIPOutputStream(fileOutputStream);
			} catch (IOException e) {
				// XXX add to interface and document
				throw new RuntimeException("Could not open compressed file for writing.", e);
			}
		} else {
			this.outputStream = fileOutputStream;
		}
	}

	@Override
	public OutputStream outputStream() {
		return outputStream;
	}

	@Override
	public void close() {
		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (IOException e) {
				// XXX document and add to interface
				new RuntimeException("Coudl not close output stream", e);
			}
			outputStream = null;
		}
	}

	@Override
	public String getFilename() {
		return outputFile.getName();
	}

}
