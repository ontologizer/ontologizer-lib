package de.ontologizer.immutable.io;

import java.io.OutputStream;

/**
 * Interface for writing to for {@link OntologyWriter}.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface WriterOutput {

	/**
	 * Query for {@link OutputStream} to write to
	 * 
	 * @return the wrapped output stream
	 */
	public OutputStream outputStream();

	/**
	 * Close the associated output streams.
	 */
	public void close();

	/**
	 * Query for file name.
	 * 
	 * @return the filename associated to the outputor <code>null</code> if no
	 *         filename is associated.
	 */
	public String getFilename();

}
