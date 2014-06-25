package edu.iastate.symex.position;

import java.util.ArrayList;

/**
 * 
 * @author HUNG
 *
 */
public class CompositeRange_deprecated extends PositionRange {

	private ArrayList<Range> ranges;

	/**
	 * Constructor
	 */
	public CompositeRange_deprecated(PositionRange range1, PositionRange range2) {
		ranges.addAll(range1.getRanges());
		ranges.addAll(range2.getRanges());
	}
	
	/**
	 * Constructor
	 */
	public CompositeRange_deprecated(ArrayList<Range> ranges) {
		this.ranges = ranges;
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
		int offset = 0;
		for (Range range : ranges) {
			if (relOffset < offset + range.getLength())
				return range.getPositionAtRelativeOffset(relOffset - offset);
			offset += range.getLength();
		}
		
		return Position.UNDEFINED;
	}
	
	@Override
	public ArrayList<Range> getRangesAtRelativeOffset(int relOffset, int length) {
		ArrayList<Range> extractedRanges = new ArrayList<Range>();
		int offset = 0;
		for (Range range : ranges) {
		 	if (relOffset < offset + range.getLength()) {
		 		if (relOffset + length <= offset + range.getLength()) {
		 			Position startPosition = range.getPositionAtRelativeOffset(relOffset - offset);
		 			extractedRanges.add(new Range(startPosition, length));
		 			break;
		 		}
		 		else {
		 			Position startPosition = range.getPositionAtRelativeOffset(relOffset - offset);
		 			extractedRanges.add(new Range(startPosition, offset + range.getLength() - relOffset));
		 			length = length - (offset + range.getLength() - relOffset);
		 			relOffset = offset + range.getLength();
		 		}
		 	}
		 	offset += range.getLength();
		}
		
		return extractedRanges;
	}

	@Override
	public ArrayList<Range> getRanges() {
		return new ArrayList<Range>(ranges);
	}
	
}
