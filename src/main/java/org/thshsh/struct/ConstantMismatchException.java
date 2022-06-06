package org.thshsh.struct;

import org.apache.commons.codec.binary.Hex;

@SuppressWarnings("serial")
public class ConstantMismatchException extends RuntimeException {
	
	public ConstantMismatchException(int token,Object expected,Object found) {
		super("Token "+token+" Expected constant: '"+(expected.getClass().isArray()? Hex.encodeHexString((byte[])expected):expected.toString())+"' but found: '"+(found.getClass().isArray()? Hex.encodeHexString((byte[])found):found.toString())+"'");
	}


}

