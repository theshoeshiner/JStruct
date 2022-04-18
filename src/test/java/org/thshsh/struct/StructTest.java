package org.thshsh.struct;


import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StructTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StructTest.class);

	@Test
	public void testInteger() {
		Struct f = Struct.create(">iii");
		List<Object> input = Arrays.asList(Integer.MAX_VALUE,Integer.MIN_VALUE,0);
		testInput(f,input);
	}
	
	@Test
	public void testByte() {
		Struct f = Struct.create(">4s3s");
		List<Object> input = Arrays.asList(new byte[] {4,6,2,12},new byte[] {Byte.MAX_VALUE,Byte.MIN_VALUE,0});
		testInput(f,input);
	}
	
	@Test
	public void testShort() {
		Struct f = Struct.create(">hhh");
		List<Object> input = Arrays.asList(Short.MAX_VALUE,Short.MIN_VALUE,(short)0);
		testInput(f,input);
	}
	
	@Test
	public void testLong() {
		Struct f = Struct.create(">qqq");
		List<Object> input = Arrays.asList(Long.MAX_VALUE,Long.MIN_VALUE,0l);
		testInput(f,input);
	}
	
	@Test
	public void testDouble() {
		Struct f = Struct.create(">ddd");
		List<Object> input = Arrays.asList(Double.MAX_VALUE,Double.MIN_VALUE,0d);
		testInput(f,input);
	}
	
	@Test()
	public void testCount() {
		Struct f = Struct.create(">2d2q2h2i3s4S");
		List<Object> input = Arrays.asList(
				Double.MAX_VALUE,Double.MIN_VALUE,
				Long.MAX_VALUE,Long.MIN_VALUE,
				Short.MAX_VALUE,Short.MIN_VALUE,
				Integer.MAX_VALUE,Integer.MIN_VALUE,
				new byte[] {12,3,4},
				"test"
				);
		testInput(f,input);
		
	}
	
	@Test()
	public void testInvalidCount() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			Struct.create(">0S");
		});
	}
	
	
	@Test
	public void testString() {
		Struct f = Struct.create(">S3S4S");
		List<Object> input = Arrays.asList("A","BBB","CCCC");
		testInput(f,input);
	}
	
	
	
	public static void testInput(Struct format, List<Object> input) {
		LOGGER.info("input: {}",new Object[] {input});
		byte[] packed = format.pack( input);
		LOGGER.info("packed: {}",new Object[] {packed});
		List<Object> unpacked = format.unpack(packed);
		LOGGER.info("unpacked: {}",unpacked);

		Iterator<Object> ins = input.iterator();
		Iterator<Object> outs = unpacked.iterator();
		
		while(ins.hasNext() || outs.hasNext()) {
			Object in =ins.next();
			Object out = outs.next();
			if(in.getClass().isArray()) {
				Assertions.assertArrayEquals((byte[])in, (byte[])out);
			}
			else {
				Assertions.assertEquals(in, out);
			}
		}
	}

}
