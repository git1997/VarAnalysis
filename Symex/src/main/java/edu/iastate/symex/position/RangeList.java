package edu.iastate.symex.position;

import java.util.ArrayList;

/**
 * 
 * @author HUNG
 *
 */
public class RangeList extends PositionRange {

	private ArrayList<Range> ranges;

	/**
	 * Constructor
	 */
	public RangeList(PositionRange range1, PositionRange range2) {
		ranges.addAll(range1.getRanges());
		ranges.addAll(range2.getRanges());
	}

	@Override
	public int getLength() {
		int length = 0;
		for (Range range : ranges)
			length += range.getLength();
		return length;
	}

	@Override
	public Position getPositionAtRelativeOffset(int relOffset) {
		for (Range range : ranges)
			if (relOffset < range.getLength())
				return range.getPositionAtRelativeOffset(relOffset);
			else
				relOffset -= range.getLength();
		
		return Position.UNDEFINED;
	}

	@Override
	public ArrayList<Range> getRanges() {
		return new ArrayList<Range>(ranges);
	}
	
}
