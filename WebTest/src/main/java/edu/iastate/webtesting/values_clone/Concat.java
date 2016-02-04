package edu.iastate.webtesting.values_clone;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author HUNG
 *
 */
public class Concat extends CondValue {
	private List<CondValue> childValues = new ArrayList<CondValue>(); // childValues must be compact (no childValues of type Concat) and contain at least 2 elements

	/**
	 * Constructor
	 * Called by CondValueFactory only.
	 * @param childValues
	 */
	Concat(List<CondValue> childValues) {
		this.childValues = childValues;
	}
	
	public List<CondValue> getChildValues() {
		return new ArrayList<CondValue>(childValues);
	}
	
	public String getConcreteStringValue() {
		StringBuilder strBuilder = new StringBuilder();
		for (CondValue childValue : childValues)
			strBuilder.append(childValue.getConcreteStringValue());
		return strBuilder.toString();
	}
}
