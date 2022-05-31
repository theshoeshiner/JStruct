package org.thshsh.struct;

@SuppressWarnings("serial")
public class MappingException extends RuntimeException {
	
	public MappingException(String s,Exception e) {
		super(s,e);
	}

	public MappingException(String s) {
		super(s);
	}
	
	
	public MappingException(Class<?> c,Exception e) {
		super("Cannot unpack class "+c,e);
	}

}
