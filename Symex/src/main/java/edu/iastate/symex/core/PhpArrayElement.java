package edu.iastate.symex.core;

/**
 * 
 * @author HUNG
 *
 */
public class PhpArrayElement extends PhpVariable {

	private String key;	// The key of the array element, e.g. $x[1] has name = x, key = 1.
	
	/**
	 * Constructor
	 * @param name The name of the array
	 * @param key The key of the array element, e.g. $x[1] has name = x, key = 1.
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
