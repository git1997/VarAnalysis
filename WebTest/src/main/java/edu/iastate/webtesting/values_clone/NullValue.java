package edu.iastate.webtesting.values_clone;

/**
 * 
 * @author HUNG
 *
 */
public class NullValue extends CondValue {
	public static final NullValue NOT_IMPLEMENTED = new NullValue();
	
	private NullValue() {
	}
	
	public String getConcreteStringValue() {
		return "";
	}
}