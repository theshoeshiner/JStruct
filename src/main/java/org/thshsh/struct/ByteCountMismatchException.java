package org.thshsh.struct;

@SuppressWarnings("serial")
public class ByteCountMismatchException extends RuntimeException {
	
	public ByteCountMismatchException(int expected,int found) {
		super("Expected bytes: "+expected+" but found: "+found);
	}

	public ByteCountMismatchException(int expected,int found,Object val) {
		super("Expected bytes: "+expected+" but found: "+found+" for value: "+val);
	}
	
}
