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
	
	public PositionRange getBasePositionRange() {
		return basePositionRange;
	}
	
	public int getOffset() {
		return offset;
	}

	@Override
	public int getLength() {
		return length;
	}

	@Override
	public ArrayList<Range> getRanges() {
		ArrayList<Range> ranges = new ArrayList<Range>();
		
		for (int i = 0; i < length; i++) {
			Position position = getPositionAtRelativeOffset(i);
			
			Range newRange;
			if (position.isUndefined())
				newRange = new Range(1);
			else
				newRange = new Range(position.getFile(), position.getOffset(), 1);
			
			if (ranges.isEmpty()) {
				ranges.add(newRange);
			}
			else {
				Range lastRange = ranges.get(ranges.size() - 1);
				if (lastRange.getEndPosition().sameAs(newRange.getStartPosition()))
					ranges.set(ranges.size() - 1, new Range(lastRange.getFile(), lastRange.getOffset(), lastRange.getLength() + 1));
				else
					ranges.add(newRange);
			}
		}
		
		return ranges;
	}

	@Override
	public Position getPositionAtRelativeOffset(int relOffset) {
		return basePositionRange.getPositionAtRelativeOffset(offset + relOffset);
	}

}
