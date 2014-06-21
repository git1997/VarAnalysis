package edu.iastate.symex.position;

import java.util.ArrayList;

/**
 * 
 * @author HUNG
 *
 */
public class ScatteredRegion extends Region {

	private Region region1;
	private Region region2;

	/**
	 * Constructor
	 */
	public ScatteredRegion(Region region1, Region region2) {
		this.region1 = region1;
		this.region2 = region2;
	}

	@Override
	public int getLength() {
		return region1.getLength() + region2.getLength();
	}

	@Override
	public Position getPositionAtRelativeOffset(int relOffset) {
		if (relOffset < region1.getLength())
			return region1.getPositionAtRelativeOffset(relOffset);
		else
			return region2.getPositionAtRelativeOffset(relOffset - region1.getLength());
	}

	@Override
	public ArrayList<ContinuousRegion> getContinuousRegions() {
		ArrayList<ContinuousRegion> regions = new ArrayList<ContinuousRegion>();
		regions.addAll(region1.getContinuousRegions());
		regions.addAll(region2.getContinuousRegions());
		
		return regions;
	}
	
}
