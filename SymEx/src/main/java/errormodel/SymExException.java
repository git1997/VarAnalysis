package errormodel;

import java.io.File;

public class SymExException extends Exception {

	private static final long serialVersionUID = 1L;

	private final int offset;

	public SymExException(String msg, File file, int offset) {
		super(msg);
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}

}
