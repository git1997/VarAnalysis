package edu.iastate.symex.position;

import java.util.ArrayList;

/**
 * 
 * @author HUNG
 *
 */
public class UndefinedRange extends PositionRange {

	/**
	 * Protected constructor.
	 */
	protected UndefinedRange() {
	}
	
	@Override
	public int getLength() {
		return 0;
	}

	@Override
	public Position getPositionAtRelativeOffset(int relOffset) {
		return Position.UNDEFINED;
	}
	
	@Override
	public ArrayList<Range> getRanges() {
		return new ArrayList<Range>();
	}

}
