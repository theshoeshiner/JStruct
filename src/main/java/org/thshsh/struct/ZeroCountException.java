package org.thshsh.struct;

@SuppressWarnings("serial")
public class ZeroCountException extends RuntimeException {
	
	public ZeroCountException() {
		super("Token Count cannot be zero");
	}

}
