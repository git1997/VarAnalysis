package edu.iastate.symex.util.logging;

import java.io.File;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import edu.iastate.symex.util.FileIO;

/**
 * 
 * @author HUNG
 *
 */
public class FileHandler extends Handler {
	
	private StringBuilder buffer = new StringBuilder();

	@Override
	public void publish(LogRecord logRecord) {
		buffer.append(logRecord.getMessage() + System.lineSeparator());
	}

	@Override
	public void flush() {
	}	
	
	@Override
	public void close() throws SecurityException {
	}
	
	public void writeLogMessagesToFile(File file) {
		FileIO.writeStringToFile(buffer.toString(), file);
	}

}