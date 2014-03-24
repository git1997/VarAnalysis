package edu.cmu.va.varanalysis.model;

import java.util.ArrayList;
import java.util.List;

/**
 * range of (not necessarily consecutive) characters
 */
public class PositionRange {

	private List<Range> ranges;

	/**
	 * simple consecutive ranges
	 */
	public PositionRange(String file, int from, int to) {
		this.ranges = new ArrayList<>(1);
		ranges.add(new Range(file, from, to));
	}

	/**
	 * simple consecutive ranges
	 */
	public PositionRange(List<Range> ranges) {
		this.ranges = ranges;
	}

	public PositionRange join(PositionRange that) {
		List<Range> r = new ArrayList<>(this.ranges);
		r.addAll(that.ranges);
		return new PositionRange(r);
	}

	@Override
	public String toString() {
		StringBuilder r = new StringBuilder();
		r.append("[");
		for (Range e : ranges)
			r.append(e.toString());
		r.append("]");
		return r.toString();
	}

	public List<Range> getRanges() {
		return ranges;
	}

	
}
