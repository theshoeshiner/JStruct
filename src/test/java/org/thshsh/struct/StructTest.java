package org.thshsh.struct;


import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"rawtypes","unchecked"})
public class StructTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StructTest.class);

	@Test
	public void testInteger() {
		Struct f = Struct.create("iii");
		List<Object> input = Arrays.asList(Integer.MAX_VALUE,Integer.MIN_VALUE,0);
		testListInputAllByteOrders(f,input);
	}
	
	@Test
	public void testBytes() {
		Struct f = Struct.create("4s3s");
		List<Object> input = Arrays.asList(new byte[] {4,6,2,12},new byte[] {Byte.MAX_VALUE,Byte.MIN_VALUE,0});
		testListInputAllByteOrders(f,input);
	}
	
	@Test
	public void testByte() {
		Struct f = Struct.create("4bb");
		List<Object> input = Arrays.asList((byte)1,(byte)2,(byte)3,(byte)4,(byte)5);
		testListInputAllByteOrders(f,input);
	}
	
	@Test
	public void testBoolean() {
		Struct f = Struct.create(">4t");
		List<Object> input = Arrays.asList(true,false,true,true);
		testListInputAllByteOrders(f,input);
	}
	
	/*@Test
	public void testUnsignedShort() {
		Struct f = Struct.create(">h");
		byte[] bytes = new byte[] {0,63};
		List<Object> tokens = f.unpack(bytes);
	}*/
	
	@Test
	public void testShort() {
		Struct f = Struct.create("hhh");
		List<Object> input = Arrays.asList(Short.MAX_VALUE,Short.MIN_VALUE,(short)0);
		testListInputAllByteOrders(f,input);
	}
	
	@Test
	public void testLong() {
		Struct f = Struct.create("qqq");
		List<Object> input = Arrays.asList(Long.MAX_VALUE,Long.MIN_VALUE,0l);
		testListInputAllByteOrders(f,input);
	}
	
	@Test
	public void testDouble() {
		Struct f = Struct.create("ddd");
		List<Object> input = Arrays.asList(Double.MAX_VALUE,Double.MIN_VALUE,0d);
		testListInputAllByteOrders(f,input);
		f = Struct.create("<ddd");
		testListInputAllByteOrders(f,input);
	}
	
	@Test
	public void testCombined() {
		Struct struct = Struct.create("2h2i2q2d4s5S");
		List<Object> input = Arrays.asList(
				Short.MAX_VALUE,Short.MIN_VALUE,
				Integer.MAX_VALUE,Integer.MIN_VALUE,
				Long.MAX_VALUE,Long.MIN_VALUE,
				Double.MAX_VALUE,Double.MIN_VALUE,
				new byte[] {4,6,2,12},
				"ABCDE"
				);
		byte[] output = struct.pack(input);
		LOGGER.info("{}",new Object[] {output});
	}
	
	@Test()
	public void testCount() {
		Struct f = Struct.create("2d2q2h2i3s4S");
		List<Object> input = Arrays.asList(
				Double.MAX_VALUE,Double.MIN_VALUE,
				Long.MAX_VALUE,Long.MIN_VALUE,
				Short.MAX_VALUE,Short.MIN_VALUE,
				Integer.MAX_VALUE,Integer.MIN_VALUE,
				new byte[] {12,3,4},
				"test"
				);
		testListInputAllByteOrders(f,input);
		
	}
	
	@Test()
	public void testInvalidCount() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			Struct.create(">0S");
		});
	}
	
	@Test()
	public void testInvalidLength() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			Struct.create(EntityInvalidLength.class);
		});
	}
	
	@Test()
	public void testPublicFields() {
		StructWithPublicFields i = new StructWithPublicFields();
		i.id = 12;
		testObjectInputAllOrders(i);
	}
	
	@Test()
	public void testPrivateFields() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			Struct.create(StructWithPrivateFields.class);
		});
	}
	
	@Test()
	public void testWrongLength() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			Struct s = Struct.create("2S");
			s.pack("ab");
			s.pack("abc");
		});
	}
	
	
	
	@Test
	public void testString()  {
		Struct f = Struct.create("S3S4S");
		List<Object> input = Arrays.asList("A","BBB","CCCC");
		testListInputAllByteOrders(f,input);
	}
	
	@Test
	public void testEntity() {
		EntityEverything mea = new EntityEverything("abc",(short)12,322,3439l,4.222d,new byte[] {4,3,2,1},true,(byte) 4,Short.MAX_VALUE+1,Integer.MAX_VALUE+1l,Long.MIN_VALUE);
		testObjectInputAllOrders(mea);
	}
	
	@Test
	public void testCollision() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			Struct.create(EntityOrderCollision.class);
		});
	}
	
	@Test
	public void testConstant() {
		EntityConstant entity = new EntityConstant("abc",(short)12,322,3439l,4.222d,new byte[] {4,3,2,1},true,(byte) 4,Short.MAX_VALUE+1,Integer.MAX_VALUE+1l,Long.MIN_VALUE);
		entity.myConstant = EntityConstant.CONSTANT;
		testObjectInputAllOrders(entity);
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			byte[] packed = Struct.create(EntityConstant.class).packEntity(entity);
			Struct.create(EntityConstant2.class).unpackEntity(packed);
		});
	}
	
	@Test
	public void testChildEntity()  {
		EntityChild entity = new EntityChild("abc",(short)12,322,3439l,4.222d,new byte[] {4,3,2,1},true,(byte) 4,Short.MAX_VALUE+1,Integer.MAX_VALUE+1l,Long.MIN_VALUE,"abcdefgh");
		testObjectInputAllOrders(entity);
	}
	
	@Test
	public void testPrefixAndStuffix() {
		EntityPrefixAndSuffix mea = new EntityPrefixAndSuffix("abc",(short)12,322,3439l,4.222d,new byte[] {4,3,2,1},true,(byte) 4,Short.MAX_VALUE+1,Integer.MAX_VALUE+1l,Long.MIN_VALUE);
		testObjectInputAllOrders(mea);
		
		byte[] packed = Struct.create(EntityPrefixAndSuffix.class).packEntity(mea);
		EntityPrefixAndSuffix unpacked = Struct.create(EntityPrefixAndSuffix.class).unpackEntity(packed);
		
		EntityEverything original = new EntityEverything("abc",(short)12,322,3439l,4.222d,new byte[] {4,3,2,1},true,(byte) 4,Short.MAX_VALUE+1,Integer.MAX_VALUE+1l,Long.MIN_VALUE);
		Assertions.assertTrue(unpacked.equalsOriginal(original));
	}
	
	@Test
	public void testGeneric() {
		EntityEverything mea = new EntityEverything("abc",(short)12,322,3439l,4.222d,new byte[] {4,3,2,1},true,(byte) 4,Short.MAX_VALUE+1,Integer.MAX_VALUE+1l,Long.MIN_VALUE);
		Struct<EntityEverything> s = Struct.create(EntityEverything.class);
		byte[] bytes =s.packEntity(mea);
		EntityEverything unpacked = s.unpackEntity(bytes);
		Assertions.assertTrue(unpacked.equals(mea));
	}
	
	@Test
	public void testEntityWrongLength() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			EntityEverything mea = new EntityEverything("abcd",(short)12,322,3439l,4.222d,new byte[] {4,3,2,1},true,(byte) 4,Short.MAX_VALUE+1,Integer.MAX_VALUE+1l,Long.MIN_VALUE);
			Struct.create(EntityEverything.class).packEntity(mea);
		});
	}
	
	@Test
	public void testDuplicateEntity()  {
		//make sure we can unpack objects of different types as long as the struct configs are the same
		EntityEverything mea = new EntityEverything("abc",(short)12,322,3439l,4.222d,new byte[] {4,3,2,1},true,(byte) 4,Short.MAX_VALUE+1,Integer.MAX_VALUE+1l,Long.MIN_VALUE);
		Struct<EntityEverything> s = Struct.create(EntityEverything.class);
		byte[] bytes = s.packEntity(mea);
		EntityCopy copy = s.unpackEntity(EntityCopy.class, bytes);
		Assertions.assertTrue(copy.equalsOriginal(mea));
	}
	
	@Test
	public void testEntityWithClassAnnotation()  {
		EntityAnnotation mea = new EntityAnnotation("abc",(short)12,322,3439l,4.222d,new byte[] {4,3,2,1},true,(byte) 4,Short.MAX_VALUE+1,Integer.MAX_VALUE+1l,Long.MIN_VALUE);
		StructEntity sc = EntityAnnotation.class.getAnnotation(StructEntity.class);
		Struct<EntityAnnotation> s = Struct.create(EntityAnnotation.class);
		
		Assertions.assertEquals(s.byteOrder,sc.byteOrder());
		Assertions.assertEquals(s.charset.name(),sc.charset());
		Assertions.assertEquals(13,s.tokenCount());
		
		byte[] bytes = s.packEntity(mea);
		EntityAnnotation copy = s.unpackEntity(EntityAnnotation.class, bytes);
		Assertions.assertTrue(copy.equals(mea));
	}
	
	@Test
	public void testTrimAndPad() {
		EntityAnnotation mea = new EntityAnnotation("ab ",(short)12,322,3439l,4.222d,new byte[] {4,3,2,1},true,(byte) 4,Short.MAX_VALUE+1,Integer.MAX_VALUE+1l,Long.MIN_VALUE);
		Struct<EntityAnnotation> s = Struct.create(EntityAnnotation.class);
		byte[] bytes = s.packEntity(mea);
		EntityAnnotation copy = s.unpackEntity(EntityAnnotation.class, bytes);
		Assertions.assertEquals(copy.myString, "ab");
		bytes = s.packEntity(copy);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			s.trimAndPad(false);
			s.packEntity(copy);
		});
		
	}
	
	
	@Test
	public void testChildOverride() {
		
		Struct<EntityChildLong> sl = Struct.create(EntityChildLong.class);
		Struct<EntityChildInteger> si = Struct.create(EntityChildInteger.class);
		
		Assertions.assertEquals(sl.byteCount(), si.byteCount()+4);
		
		byte[] slbytes  = sl.packEntity(new EntityChildLong("abc",123l));
		byte[] sibytes = si.packEntity(new EntityChildInteger("abc",123));
		
		LOGGER.info("lon bytes: {}",slbytes);
		LOGGER.info("int bytes: {}",sibytes);
		
	}
	
	
	public static void testObjectInputAllOrders(Object input) {
		Struct struct = Struct.create(input.getClass());
		struct.byteOrder = ByteOrder.Big;
		testObjectInput(struct, input);
		struct.byteOrder = ByteOrder.Little;
		testObjectInput(struct, input);
		struct.byteOrder = ByteOrder.Native;
		testObjectInput(struct, input);
	}
	
	public static void testObjectInput(Struct struct, Object in) {
		byte[] array = struct.packEntity(in);
		LOGGER.info("packed: {}",new Object[] {array});
		Object out = struct.unpackEntity(in.getClass(),array);
		Assertions.assertEquals(in, out);
	
	}
	
	public static void testListInputAllByteOrders(Struct format, List<Object> input) {
		format.byteOrder = ByteOrder.Little;
		testListInput(format, input);
		format.byteOrder = ByteOrder.Big;
		testListInput(format, input);
		format.byteOrder = ByteOrder.Native;
		testListInput(format, input);
	}
	
	public static void testListInput(Struct format, List<Object> input) {
		
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
