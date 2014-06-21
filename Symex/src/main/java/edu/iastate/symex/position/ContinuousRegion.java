package edu.iastate.symex.position;

import java.io.File;
import java.util.ArrayList;

/**
 * 
 * @author HUNG
 *
 */
public class ContinuousRegion extends Region {

	private File file;
	private int offset;
	private int length;
	
	/**
	 * Constructor
	 */
	public ContinuousRegion(File file, int offset, int length) {
		this.file = file;
		this.offset = offset;
		this.length = length;
	}
	
	public File getFile() {
		return file;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public int getLength() {
		return length;
	}

	@Override
	public Position getPositionAtRelativeOffset(int relOffset) {
		return new Position(file, offset + relOffset);
	}
	
	@Override
	public ArrayList<ContinuousRegion> getContinuousRegions() {
		ArrayList<ContinuousRegion> regions = new ArrayList<ContinuousRegion>(1);
		regions.add(this);
		return regions;
	}
	
}
