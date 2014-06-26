package edu.iastate.symex.position;

import java.util.ArrayList;

/**
 * RelativeRange is composed of a base PositionRange and a range within that base.
 * 
 * @author HUNG
 *
 */
public class RelativeRange extends PositionRange {
	
	private PositionRange basePositionRange;
	private int offset;
	private int length;
	
	/**
	 * Constructor
	 */
	public RelativeRange(PositionRange basePositionRange, int offset, int length) {
		this.basePositionRange = basePositionRange;
		this.offset = offset;
		this.length = length;
	}

	@Override
	public int getLength() {
		return length;
	}

	@Override
	public Position getPositionAtRelativeOffset(int relOffset) {
		return basePositionRange.getPositionAtRelativeOffset(offset + relOffset);
	}

	@Override
	public ArrayList<Range> getRanges() {
		ArrayList<Range> ranges = new ArrayList<Range>();
		for (int i = 0; i < length; i++) {
			Position p = getPositionAtRelativeOffset(i);
			if (!ranges.isEmpty()) {
				Range lastRange = ranges.get(ranges.size() - 1);
				if (lastRange.getFile() == p.getFile() && lastRange.getOffset() + lastRange.getLength() == p.getOffset()) {
					ranges.set(ranges.size() - 1, new Range(lastRange.getFile(), lastRange.getOffset(), lastRange.getLength() + 1));
					continue;
				}
			}
			ranges.add(new Range(p, 1));
		}
		return ranges;
	}

}
