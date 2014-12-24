package edu.iastate.parsers.conditional;

/**
 * CondList is used to represent a list with conditional items.
 * 
 * @author HUNG
 *
 * @param <T>
 */
public abstract class CondList<T> {
	
	/**
	 * Used for debugging
	 */
	public abstract String toDebugString();
	
	/**
	 * Writes the CondList to #ifdef format
	 */
	public String toIfDefString() {
		StringBuilder strBuilder = new StringBuilder();
		writeCondListToIfDefs(this, strBuilder);
		return strBuilder.toString();
	}
		
	private void writeCondListToIfDefs(CondList<T> condList, StringBuilder strBuilder) {
		if (condList instanceof CondListConcat<?>) {
			CondListConcat<T> concat = (CondListConcat<T>) condList;
			for (CondList<T> childNode : concat.getChildNodes())
				writeCondListToIfDefs(childNode, strBuilder);
		}
		else if (condList instanceof CondListSelect<?>) {
			CondListSelect<T> select = (CondListSelect<T>) condList;
			strBuilder.append("#if (" + select.getConstraint().toDebugString() + ")" + System.lineSeparator());
			writeCondListToIfDefs(select.getTrueBranchNode(), strBuilder);
			strBuilder.append("#else" + System.lineSeparator());
			writeCondListToIfDefs(select.getFalseBranchNode(), strBuilder);
			strBuilder.append("#endif" + System.lineSeparator());
		}
		else if (condList instanceof CondListItem<?>) {
			CondListItem<T> item = (CondListItem<T>) condList;
			strBuilder.append(item.toDebugString() + System.lineSeparator());
		}
		else { // if (condList instanceof CondListEmpty<?>)
			// Do nothing
		}
	}

}
