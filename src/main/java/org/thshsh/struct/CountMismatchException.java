package org.thshsh.struct;

@SuppressWarnings("serial")
public class CountMismatchException extends RuntimeException {
	
	public CountMismatchException(int expected,int vals) {
		super("Expected tokens: " + expected + " but found: " + vals);
	}

}
