package edu.iastate.symex.position;

import java.util.ArrayList;

/**
 * 
 * @author HUNG
 *
 */
public class UndefinedRegion extends Region {

	/**
	 * Protected constructor.
	 */
	protected UndefinedRegion() {
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
	public ArrayList<ContinuousRegion> getContinuousRegions() {
		// TODO Fix this
		return null;
	}

}
