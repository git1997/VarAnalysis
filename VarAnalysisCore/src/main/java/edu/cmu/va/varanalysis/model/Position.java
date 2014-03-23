package edu.cmu.va.varanalysis.model;

public class Position {
	private final int to;
	private final int from;

	public Position(int from, int to) {
		this.from = from;
		this.to = to;
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

}
