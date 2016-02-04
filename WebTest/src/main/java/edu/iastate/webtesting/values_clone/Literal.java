package edu.iastate.webtesting.values_clone;

import edu.iastate.webtesting.util_clone.CodeLocation;

/**
 * 
 * @author HUNG
 *
 */
public class Literal extends CondValue {
	private String stringValue;
	private CodeLocation location;
	
	/**
	 * Constructor
	 * Called by CondValueFactory only.
	 * @param stringValue
	 * @param location
	 */
	Literal(String stringValue, CodeLocation location) {
		this.stringValue = stringValue;
		this.location = location;
	}
	
	public String getStringValue() {
		return stringValue;
	}
	
	public CodeLocation getLocation() {
		return location;
	}
	
	public String getConcreteStringValue() {
		return stringValue;
	}
}
