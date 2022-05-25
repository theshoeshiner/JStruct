package org.thshsh.struct;

import org.apache.commons.lang3.ArrayUtils;

public class Packer {

	protected static byte[] packRaw_16b(short val, java.nio.ByteOrder byteOrder) {
		
		
		byte[] bx = new byte[2];
	
		bx[0] = (byte) (val & 0xff);
		bx[1] = (byte) ((val >>> 8) & 0xff);
	
		if (byteOrder == java.nio.ByteOrder.BIG_ENDIAN) ArrayUtils.reverse(bx);
	
		return bx;
	}

	protected static byte[] packRaw_u16b(int val, java.nio.ByteOrder byteOrder) {
		byte[] bx = new byte[2];
	
		bx[0] = (byte) (val & 0xff);
		bx[1] = (byte) ((val >>> 8) & 0xff);
	
		if (byteOrder == java.nio.ByteOrder.BIG_ENDIAN) ArrayUtils.reverse(bx);
	
		return bx;
	}

	protected static byte[] packRaw_32b(int val, java.nio.ByteOrder byteOrder) {
		byte[] bx = new byte[4];
	
		bx[0] = (byte) (val & 0xff);
		bx[1] = (byte) ((val >>> 8) & 0xff);
		bx[2] = (byte) ((val >>> 16) & 0xff);
		bx[3] = (byte) ((val >>> 24) & 0xff);
	
		if (byteOrder == java.nio.ByteOrder.BIG_ENDIAN) ArrayUtils.reverse(bx);
		
		return bx;
	}

	protected static byte[] packRaw_64b(long val, java.nio.ByteOrder byteOrder) {
	
		byte[] bx = new byte[8];
	
		bx[0] = (byte) (val & 0xff);
		bx[1] = (byte) ((val >>> 8) & 0xff);
		bx[2] = (byte) ((val >>> 16) & 0xff);
		bx[3] = (byte) ((val >>> 24) & 0xff);
		bx[4] = (byte) ((val >>> 32) & 0xff);
		bx[5] = (byte) ((val >>> 40) & 0xff);
		bx[6] = (byte) ((val >>> 48) & 0xff);
		bx[7] = (byte) ((val >>> 56) & 0xff);
	
		if (byteOrder ==java.nio.ByteOrder.BIG_ENDIAN) ArrayUtils.reverse(bx);
		
		return bx;
	}

	protected static byte[] packFloat_64b(double val, java.nio.ByteOrder byteOrder) {
	
		long bits = Double.doubleToLongBits(val);
		byte[] bytes = packRaw_64b(bits, byteOrder);
		return bytes;
	
	}

	protected static byte[] packRaw_u32b(long val, java.nio.ByteOrder byteOrder) {
		byte[] bx = new byte[4];
	
		val = val & 0xffffffff;
	
		if (val >= 0) {
			bx[0] = (byte) (val & 0xff);
			bx[1] = (byte) ((val >> 8) & 0xff);
			bx[2] = (byte) ((val >> 16) & 0xff);
			bx[3] = (byte) ((val >> 24) & 0xff);
	
		}
	
		if (byteOrder == java.nio.ByteOrder.BIG_ENDIAN) ArrayUtils.reverse(bx);
		
		return bx;
	}

	protected static short unpackRaw_16b(byte[] val, java.nio.ByteOrder byteOrder) {
	
		if (byteOrder == java.nio.ByteOrder.LITTLE_ENDIAN) ArrayUtils.reverse(val);
	
		int x = ((int) (val[0] & 0xff) << 8) | ((int) (val[1] & 0xff));
	
		return (short) x;
	}

	protected static int unpackRaw_u16b(byte[] val, java.nio.ByteOrder byteOrder) {
	
		if (byteOrder == java.nio.ByteOrder.LITTLE_ENDIAN) ArrayUtils.reverse(val);
	
		int x = ((val[0] & 0xff) << 8) | (val[1] & 0xff);
		return x;
	}

	protected static int unpackRaw_32b(byte[] val, java.nio.ByteOrder byteOrder) {
		
		//LOGGER.info("val1: {}",new Object[] {val});
		
		if (byteOrder == java.nio.ByteOrder.LITTLE_ENDIAN) ArrayUtils.reverse(val);
		
		//LOGGER.info("val2: {}",new Object[] {val});
		
		int x = ((int) (val[0] & 0xff) << 24) | ((int) (val[1] & 0xff) << 16) | ((int) (val[2] & 0xff) << 8)
				| ((int) (val[3] & 0xff));
	
		return x;
	}

	protected static long unpackRaw_u32b(byte[] val, java.nio.ByteOrder byteOrder) {
		
		if (byteOrder == java.nio.ByteOrder.LITTLE_ENDIAN) ArrayUtils.reverse(val);
	
		long x = (((long) (val[0] & 0xff)) << 24) | (((long) (val[1] & 0xff)) << 16) | (((long) (val[2] & 0xff)) << 8)
				| ((long) (val[3] & 0xff));
		return x;
	}

	protected static long unpackRaw_64b(byte[] val, java.nio.ByteOrder byteOrder) {
		
		if (byteOrder == java.nio.ByteOrder.LITTLE_ENDIAN) ArrayUtils.reverse(val);
	
		long x = (((long) (val[0] & 0xff)) << 56) | (((long) (val[1] & 0xff)) << 48) | (((long) (val[2] & 0xff)) << 40)
				| (((long) (val[3] & 0xff)) << 32) | (((long) (val[4] & 0xff)) << 24) | (((long) (val[5] & 0xff)) << 16)
				| (((long) (val[6] & 0xff)) << 8) | ((long) (val[7] & 0xff));
		return x;
	}

	protected static double unpackFloat_64b(byte[] val, java.nio.ByteOrder byteOrder) {
	
		long x = unpackRaw_64b(val, byteOrder);
		double d = Double.longBitsToDouble(x);
	
		return d;
	}


}
