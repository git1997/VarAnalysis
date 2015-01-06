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
	
	public PositionRange getRange1() {
		return range1;
	}
	
	public PositionRange getRange2() {
		return range2;
	}

	@Override
	public int getLength() {
		int length1 = range1.getLength();
		int length2 = range2.getLength();
		return (length1 == -1 || length2 == -1 ? -1 : (length1 + length2));
	}

	@Override
	public ArrayList<Range> getRanges() {
		ArrayList<Range> ranges = new ArrayList<Range>();
		ranges.addAll(range1.getRanges());
		ranges.addAll(range2.getRanges());
		return ranges;
	}

	@Override
	public Position getPositionAtRelativeOffset(int relOffset) {
		int length1 = range1.getLength();
		
		if (length1 == -1)
			return Position.UNDEFINED;
		else if (relOffset < length1)
			return range1.getPositionAtRelativeOffset(relOffset);
		else
			return range2.getPositionAtRelativeOffset(relOffset - length1);
	}
	
}
