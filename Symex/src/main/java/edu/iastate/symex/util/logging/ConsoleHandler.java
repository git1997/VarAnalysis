package edu.iastate.symex.util.logging;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * 
 * @author HUNG
 *
 */
public class ConsoleHandler extends Handler {

	@Override
	public void publish(LogRecord logRecord) {
		System.out.println(logRecord.getMessage());
	}

	@Override
	public void flush() {
	}	
	
	@Override
	public void close() throws SecurityException {
	}

}