package edu.iastate.symex.position;

import java.util.ArrayList;

/**
 * 
 * @author HUNG
 *
 */
public class UndefinedPositionRange extends PositionRange {

	public static UndefinedPositionRange inst = new UndefinedPositionRange();
	
	/**
	 * Private constructor.
	 */
	private UndefinedPositionRange() {
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
	public ArrayList<AtomicPositionRange> getAtomicPositionRanges() {
		// TODO Fix this
		return null;
	}

}
