package edu.iastate.webtesting.values_clone;

import java.util.ArrayList;
import java.util.List;

import edu.iastate.webtesting.util_clone.CodeLocation;

/**
 * 
 * @author HUNG
 *
 */
public class CondValueFactory {
	
	public static Concat createConcat(List<CondValue> childValues) {
		return new Concat(new ArrayList<CondValue>(childValues));
	}
	
	public static Concat createConcat(CondValue childValue1, CondValue childValue2) {
		List<CondValue> childValues = new ArrayList<CondValue>();
		if (childValue1 instanceof Concat)
			childValues.addAll(((Concat) childValue1).getChildValues());
		else
			childValues.add(childValue1);
		if (childValue2 instanceof Concat)
			childValues.addAll(((Concat) childValue2).getChildValues());
		else
			childValues.add(childValue2);
		return new Concat(childValues);
	}
	
	public static Literal createLiteral(String stringValue, CodeLocation location) {
		return new Literal(stringValue, location);
	}
}
