package org.thshsh.struct;

public enum ByteOrder {
	Big(java.nio.ByteOrder.BIG_ENDIAN),Little(java.nio.ByteOrder.LITTLE_ENDIAN),Native(null);

	private java.nio.ByteOrder order;
	
	private ByteOrder(java.nio.ByteOrder bo) {
		this.order = bo;
	}
	
	public java.nio.ByteOrder getByteOrder() {
		if(this == Native) return java.nio.ByteOrder.nativeOrder();
		else return order;
	}
	
	public static ByteOrder nativeOrder() {
		return java.nio.ByteOrder.nativeOrder() == java.nio.ByteOrder.LITTLE_ENDIAN ? ByteOrder.Little : ByteOrder.Big;
	}
}
