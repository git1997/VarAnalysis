package edu.iastate.symex.position;

import java.util.ArrayList;

/**
 * CompositeRange is composed of 2 PositionRanges.
 * 
 * @author HUNG
 *
 */
public class CompositeRange extends PositionRange {

	private PositionRange range1;
	private PositionRange range2;
	
	/**
	 * Constructor
	 * @param range1
	 * @param range2
	 */
	public CompositeRange(PositionRange range1, PositionRange range2) {
		this.range1 = range1;
		this.range2 = range2;
	}

	@Override
	public int getLength() {
		return range1.getLength() + range2.getLength();
	}

	@Override
	public Position getPositionAtRelativeOffset(int relOffset) {
		if (relOffset < range1.getLength())
			return range1.getPositionAtRelativeOffset(relOffset);
		else
			return range2.getPositionAtRelativeOffset(relOffset - range1.getLength());
	}

	@Override
	public ArrayList<Range> getRanges() {
		ArrayList<Range> ranges = new ArrayList<Range>();
		ranges.addAll(range1.getRanges());
		ranges.addAll(range2.getRanges());
		
		return ranges;
	}
	
}
