package org.thshsh.struct;

@SuppressWarnings("serial")
public class ByteMismatchException extends RuntimeException {
	
	public ByteMismatchException(int expected,int found) {
		super("Struct byte count: "+expected+" does not match input byte count: "+found);
	}


}
