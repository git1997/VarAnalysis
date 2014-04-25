package edu.cmu.va.varanalysis.model;

public class Range {
	private String file;
	private int from;
	private int to;

	public Range(String file, int from, int to) {
		this.file = file;
		this.from = from;
		this.to = to;
	}

	public String toString() {
		StringBuilder r = new StringBuilder();
		r.append(file);
		r.append("@");
		r.append(from);
		r.append("-");
		r.append(to);
		return r.toString();
	}

	public int getFrom() {
		return from;
	}
	public int getTo() {
		return to;
	}
	public String getFile() {
		return file;
	}
}