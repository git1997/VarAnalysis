package php.elements;

/**
 * 
 * @author HUNG
 *
 */
public class PhpArrayElement extends PhpVariable {

	private String key;	// The key of the array element, e.g. $x[1] has name = x, key = 1.
	
	/**
	 * Constructor
	 * @param name
	 */
	public PhpArrayElement(String name, String key) {
		super(name);
		this.key = key;
	}
	
	/*
	 * Get properties
	 */
	
	public String getKey() {
		return key;
	}
	
}
