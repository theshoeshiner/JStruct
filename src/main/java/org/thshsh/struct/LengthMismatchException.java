package org.thshsh.struct;

@SuppressWarnings("serial")
public class LengthMismatchException extends RuntimeException {
	
	public LengthMismatchException(int expected,int found) {
		super("Expected token of length "+expected+" but recieved "+found);
	}
	
	public LengthMismatchException(int expected,int found,Object val) {
		super("Expected token of length "+expected+" but recieved "+found+" for val "+val);
	}

}
