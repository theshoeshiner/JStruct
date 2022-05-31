package org.thshsh.struct;

@SuppressWarnings("serial")
public class CountMismatchException extends RuntimeException {
	
	public CountMismatchException(int expected,int vals) {
		super("Struct tokens: " + expected + " does not equal value tokens: " + vals);
	}

}
