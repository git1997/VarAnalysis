package edu.iastate.symex.position;

import java.util.ArrayList;

/**
 * 
 * @author HUNG
 *
 */
public class ScatteredPositionRange extends PositionRange {

	private PositionRange range1;
	private PositionRange range2;

	/**
	 * Constructor
	 */
	public ScatteredPositionRange(PositionRange range1, PositionRange range2) {
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
	public ArrayList<AtomicPositionRange> getAtomicPositionRanges() {
		ArrayList<AtomicPositionRange> ranges = new ArrayList<AtomicPositionRange>();
		ranges.addAll(range1.getAtomicPositionRanges());
		ranges.addAll(range2.getAtomicPositionRanges());
		
		return ranges;
	}
	
}
