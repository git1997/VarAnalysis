package errormodel;

import java.io.File;

public class SymExException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private final int line;

	public SymExException(String msg, File file, int line) {
		super(msg);
		this.line = line;
	}

	public int getLineNumber() {
		return line;
	}

}
