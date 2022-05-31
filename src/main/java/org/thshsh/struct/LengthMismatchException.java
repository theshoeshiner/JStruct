package org.thshsh.struct;

@SuppressWarnings("serial")
public class LengthMismatchException extends RuntimeException {
	
	public LengthMismatchException(int expected,int found) {
		super("Expected Length: "+expected+" but found: "+found);
	}
	
	

}
