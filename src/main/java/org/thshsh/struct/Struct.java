package org.thshsh.struct;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * This class is based off the original port of Pythons struct functions found here:
 * https://github.com/ronniebasak/JStruct
 * 
 * But with significant updates to support strongly typed variables, arrays, and strings
 * 
 * The signed pack methods technically work because they create the correct
 * packed bytes. The downside is that value passed in must be the 2s compliment
 * version
 * 
 * The unsigned methods allow you to pass in the full value
 * 
 *
 */
public class Struct<T> {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(Struct.class);


	private static final Map<Character, ByteOrder> BYTE_ORDER_MAP = new HashMap<Character, ByteOrder>();
	static {
		BYTE_ORDER_MAP.put('>', ByteOrder.Big);
		BYTE_ORDER_MAP.put('<', ByteOrder.Little);
		BYTE_ORDER_MAP.put('!', ByteOrder.Big);
		BYTE_ORDER_MAP.put('@', ByteOrder.nativeOrder());
	}


	protected List<Token> tokens = new ArrayList<Token>();
	protected ByteOrder byteOrder;
	protected Charset charset;
	protected Class<T> entityClass;
	protected Boolean pad = false;
	protected Boolean trimAndPad = false;
	
	public Struct() {
		this(null,null);
	}

	public Struct(ByteOrder byteOrder, Charset charset) {
		this.byteOrder = byteOrder != null ? byteOrder : ByteOrder.nativeOrder();
		this.charset = charset != null ? charset : Charset.defaultCharset();
	}
	
	/* Chaining builder methods */
	
	public Struct<T> appendToken(Token t) {
		tokens.add(t);
		return this;
	}
	
	public Struct<T> appendTokens(Iterable<Token> it) {
		it.forEach(t -> tokens.add(t));
		return this;
	}
	
	public Struct<T> insertToken(Token t,int index) {
		tokens.add(index, t);
		return this;
	}

	public Struct<T> appendToken(TokenType type, int countOrLength) {
		return appendToken(new Token(type, countOrLength));
	}
	
	public Struct<T> insertToken(TokenType type, int countOrLength, int index) {
		return insertToken(new Token(type, countOrLength),index);
	}
	
	public Struct<T> byteOrder(ByteOrder bo) {
		this.byteOrder = bo;
		return this;
	}

	public Struct<T> charset(Charset cs) {
		this.charset = cs;
		return this;
	}

	public Struct<T> trimAndPad(boolean b) {
		this.trimAndPad = b;
		return this;
	}
	
	public int byteCount() {
		int size = 0;
		for (Token t : tokens)
			size += t.byteCount();
		return size;
	}
	
	public int tokenCount() {
		int count = 0;
		for (Token t : tokens) {
			if(!t.hide) count += t.tokenCount();
		}
		return count;
	}
	
	

	public T unpackEntity(InputStream stream) throws IOException {
		return unpackEntity(entityClass, stream);
	}
	
	public T unpackEntity(byte[] bytes) {
		return unpackEntity(entityClass, bytes);
	}
	
	public <V> V unpackEntity(Class<V> c, InputStream stream) throws IOException {
		byte[] buffer = new byte[byteCount()];
		IOUtils.readFully(stream, buffer);
		return unpackEntity(c,buffer);
	}
	
	public <V> V unpackEntity(Class<V> c, ReadableByteChannel channel) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(byteCount());
		IOUtils.readFully(channel, buffer);
		return unpackEntity(c,buffer.array());
	}
	
	public <V> V unpackEntity(Class<V> c, byte[] bytes) {
				
		try {
			StructEntityMapping<V> config = StructEntityMapping.get(c);
			config.validate(this);
			
			List<Object> values = this.unpack(bytes);
			V instance = c.newInstance();
			
			Iterator<Object> valuesIt = values.iterator();
			Iterator<Mapping> mapit = config.mappings.iterator();
			
			for(;valuesIt.hasNext();) {
				Object value = valuesIt.next();
				Mapping mapping = mapit.next();
				mapping.setValue(instance, value);
			}
			
			return instance;
		} 
		catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new MappingException(c,e);
		}
				
	}
	

	public  byte[] packEntity(Object o) {
				
		try {
			StructEntityMapping<?> config = StructEntityMapping.get(o.getClass());
			config.validate(this);
			
			List<Object> values = new ArrayList<Object>();
			
			for(Mapping mapping : config.mappings) {
				Object value = mapping.getValue(o);
				values.add(value);
			}
				
			return this.pack(values);
		} 
		catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new MappingException(o.getClass(),e);
		} 
		
	}
	
	
	public byte[] pack(Object...objects ) {
		return pack(Arrays.asList(objects));
	}
	
	public byte[] pack(List<Object> vals) {

		int tokenCount = tokenCount();
		if (tokenCount != vals.size())throw new CountMismatchException( tokenCount , vals.size());

		byte[] result = new byte[byteCount()];

		int position = 0;
		
		Iterator<Token> tokens = this.tokens.iterator();
		Iterator<Object> values = vals.iterator();
		
		LOGGER.debug("Packing values: {}",vals);
		
		while(tokens.hasNext()) {
			
			Token token = tokens.next();
			
			LOGGER.debug("Packing token: {}",token);
			
			int length = token.length;
									
			for (int i = 0; i < token.tokenCount(); i++) {
				
				Object val;
				
				if(token.isConstant()) {
					val = token.constant;
					if(!token.hide) values.next();
				}
				else {
					val = values.next();
				}
				
				LOGGER.debug("Packing val: {}",val);
				
				byte[] packedBytes = pack(token, val);
				if(packedBytes.length != length) throw new ByteCountMismatchException(length ,packedBytes.length,val);
				System.arraycopy(packedBytes, 0, result, position, packedBytes.length);
				position += packedBytes.length;
				
			}
			
			
		}
		
		return result;

	}

	protected byte[] pack(Token token,Object val) {
		
		TokenType type = token.type;
		java.nio.ByteOrder byteOrder = this.byteOrder.getByteOrder();
		
		byte[] packedBytes;
		switch (type) {
			case Short:
				packedBytes = Packer.packRaw_16b((short) val, byteOrder);
				break;
			case ShortUnsigned:
				packedBytes = Packer.packRaw_u16b((int) val, byteOrder);
				break;
			case Integer:
				packedBytes = Packer.packRaw_32b(((int) val & 0xffffffff), byteOrder);
				break;
			case IntegerUnsigned:
				packedBytes = Packer.packRaw_u32b((long) val, byteOrder);
				break;
			case LongUnsigned:
				//convert BigInteger to primitive signed long
				val = ((BigInteger)val).longValue();
			case Long:
			case LongUnsignedToSigned:
				packedBytes = Packer.packRaw_64b(((long)val & 0xffffffffffffffffl), byteOrder);
				break;
			case Double:
				packedBytes = Packer.packFloat_64b((double) val, byteOrder);
				break;
			case Bytes:
				packedBytes = (byte[]) val;
				break;
			case String:
				String string = (String) val;
				if(string.length() != token.length) {
					if(!trimAndPad || token.isConstant()) throw new LengthMismatchException(token.length,string.length());
					else string = StringUtils.rightPad(string, token.length);
				}
				packedBytes = (string).getBytes(this.charset);
				break;
			case Boolean:
				packedBytes = new byte[] {((boolean)val)?(byte)1:(byte)0};
				break;
			case Byte:
				packedBytes = new byte[] {(byte) val};
				break;
			default:
				throw new IllegalArgumentException("Unhandled case: " + token.type);
		}
		
		return packedBytes;
		
	}

	public List<Object> unpack(InputStream stream) throws IOException{
		byte[] buffer = new byte[byteCount()];
		IOUtils.readFully(stream, buffer);
		return unpack(buffer);
	}
	
	
	
	public List<Object> unpack(byte[] vals) {
				
		Struct<?> format = this;
		
		int len = format.byteCount();

		if (len != vals.length) throw new ByteCountMismatchException(len , vals.length);

		List<Object> tokens = new ArrayList<Object>();

		//reuse arrays when possible
		byte[][] arrays = new byte[][] {null,new byte[1],new byte[2],null,new byte[4],null,null,null,new byte[8]};

		
		ByteArrayInputStream bs = new ByteArrayInputStream(vals);
		int position = 0;
		try {
			for(int t=0;t<format.tokens.size();t++) {

				Token token = format.tokens.get(t);
			
				
				count: for (int i = 0; i < token.tokenCount(); i++) {
					
					LOGGER.debug("position: {}",position);
					
					byte[] ar = token.type.array?new byte[token.length]:arrays[token.type.size];
					
					//dont trim constants
					boolean trim = token.isConstant()?false:this.trimAndPad;
					Object o = unpack(token.type,  byteOrder, charset, ar,bs,trim);
					
					LOGGER.debug("unpacked: '{}'",o);

					if(token.isConstant()) {
						
					LOGGER.debug("expecting: '{}'",token.constant);
					
						if(token.validate) {
							if(!token.type.equalsFunction.apply(token.constant, o)) {
								//LOGGER.error("constant mismatch: {} vs {}",new Object[] {token.constant,o});
								throw new ConstantMismatchException(t,token.constant,o);
							}
						}
					}
					
					if(!token.hide) {
						tokens.add(o);
					}
					else {
						LOGGER.debug("hiding token");
					}
					position+=ar.length;
					
					if(token.type.array) break count;

				}
				

			}
			
			LOGGER.debug("end position: {}",position);
			
		} 
		catch (IOException e) {
			//We can catch this IO exception because it's unlikely since we're working with in memory bytes
			throw new IllegalStateException("Exception reading in memory byte array",e);
		}

		LOGGER.debug("unpacked: {}",tokens);

		return tokens;

	}
	
	public static Struct<List<Object>> create(String fmt) {
		return create(fmt, null);
	}

	public static <T> Struct<T> create(Class<T> structClass) {
		LOGGER.debug("create: {}",structClass);
		return StructEntityMapping.get(structClass).createStruct();
	}

	public static Struct<List<Object>> create(String fmt, Charset cs) {
		Struct<List<Object>> format = new Struct<>(null, cs);
		Character x = null;
		StringBuilder countOrLengthBuffer = new StringBuilder();
		for (int i = 0; i < fmt.length(); i++) {
			x = fmt.charAt(i);
			if (BYTE_ORDER_MAP.keySet().contains(x)) {
				format.byteOrder = BYTE_ORDER_MAP.get(x);
			} else if (Character.isDigit(x)) {
				countOrLengthBuffer.append(x);
			} else {
				TokenType type = TokenType.fromCharacter(x);
				int countOrLength = 1;
				if (countOrLengthBuffer.length() > 0) {
					countOrLength = Integer.valueOf(countOrLengthBuffer.toString());
					if(countOrLength<1) throw new ZeroCountException();
					countOrLengthBuffer.setLength(0);
				}
				format.appendToken(type, countOrLength);
			}
		}

		return format;
	}
	
	/*
	 * Static methods for quickly unpacking single tokens without creating a struct
	 */
	
	public static Object unpack(TokenType type, ByteOrder bo,InputStream is) throws IOException {
		return unpack(type,  bo, Charset.defaultCharset(), new byte[type.size],is,(Boolean) null);
	}
	
	public static Object unpack(TokenType type, ByteOrder bo,byte[] bytes) throws IOException {
		return unpack(type, bo, Charset.defaultCharset(),bytes);
	}
	
	public static Object unpack(TokenType type,int length,Charset charset,InputStream is) throws IOException {
		return unpack(type,  ByteOrder.nativeOrder(), charset, new byte[length],is,(Boolean) null);
	}
	
	public static Object unpack(TokenType type, InputStream is) throws IOException {
		return unpack(type,  ByteOrder.nativeOrder(), Charset.defaultCharset(), new byte[type.size],is,(Boolean) null);
	}
	
	public static Object unpack(TokenType type, Integer length,ByteOrder byteOrder,Charset charset,InputStream bs) throws IOException {
		byte[] bytes = new byte[length];
		IOUtils.readFully(bs, bytes);
		return unpack(type,  byteOrder, charset, bytes);
	}
	
	public static Object unpack(TokenType type, ByteOrder byteOrder,Charset charset,byte[] bytes,InputStream bs, Boolean trim) throws IOException {
		IOUtils.readFully(bs, bytes);
		return unpack(type,  byteOrder, charset, bytes,trim);
	}
	
	public static Object unpack(TokenType type, ByteOrder order,Charset charset,byte[] bytes) throws IOException {
		return unpack(type,order,charset,bytes,(Boolean)null);
	}
	
	public static Object unpack(TokenType type, ByteOrder order,Charset charset,byte[] bytes, Boolean trim) throws IOException {
	
		if(trim == null) trim = false;
		java.nio.ByteOrder byteOrder = order.getByteOrder();
		
		LOGGER.debug("unpacking: 0x{}",Hex.encodeHexString(bytes));
				
		switch (type) {
			case ShortUnsigned:
				return Packer.unpackRaw_u16b(bytes, byteOrder);
			case Short:
				return Packer.unpackRaw_16b(bytes, byteOrder);
			case IntegerUnsigned:
				return Packer.unpackRaw_u32b(bytes, byteOrder);
			case Integer:
				return Packer.unpackRaw_32b(bytes, byteOrder);
			case LongUnsigned:
				//no such thing as an unsigned 64 bit primitive java so we return a BigInteger
				return new BigInteger(Long.toUnsignedString(Packer.unpackRaw_64b(bytes, byteOrder)));
			case LongUnsignedToSigned:
				return new BigInteger(Long.toUnsignedString(Packer.unpackRaw_64b(bytes, byteOrder))).longValueExact();
			case Long:
				return Packer.unpackRaw_64b(bytes, byteOrder);
			case Double:
				return Packer.unpackFloat_64b(bytes, byteOrder);
			case Bytes:
				return bytes;
			case String:
				String string = new String(bytes, charset);
				if(trim) string = string.trim();
				return string;
			case Byte:
				return bytes[0];
			case Boolean:
				return bytes[0] != 0;
			default:
				throw new IllegalArgumentException("unhandled case: " + type);
		}
	}

	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[tokens=");
		builder.append(tokens);
		builder.append(", byteOrder=");
		builder.append(byteOrder);
		builder.append(", charset=");
		builder.append(charset);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((byteOrder == null) ? 0 : byteOrder.hashCode());
		result = prime * result + ((charset == null) ? 0 : charset.hashCode());
		result = prime * result + ((tokens == null) ? 0 : tokens.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Struct<?> other = (Struct<?>) obj;
		if (byteOrder == null) {
			if (other.byteOrder != null)
				return false;
		} else if (!byteOrder.equals(other.byteOrder))
			return false;
		if (charset == null) {
			if (other.charset != null)
				return false;
		} else if (!charset.equals(other.charset))
			return false;
		if (tokens == null) {
			if (other.tokens != null)
				return false;
		} else if (!tokens.equals(other.tokens))
			return false;
		return true;
	}

	 
	
	
}