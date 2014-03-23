package errormodel;

import java.io.File;

public class SymExException extends Exception {

	private static final long serialVersionUID = 1L;

	private final int offset;

	private final int line;

	public SymExException(String msg, File file, int line, int offset) {
		super(msg);
		this.line = line;
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}

	public int getLine() {
		return line;
	}
}
