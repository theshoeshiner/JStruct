package org.thshsh.struct;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
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
public class Struct {
	
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
	
	public Struct() {
		this(null,null);
	}

	public Struct(ByteOrder byteOrder, Charset charset) {
		this.byteOrder = byteOrder != null ? byteOrder : ByteOrder.nativeOrder();
		this.charset = charset != null ? charset : Charset.defaultCharset();
	}
	
	/* Chaining builder methods */
	
	public Struct appendToken(Token t) {
		tokens.add(t);
		return this;
	}
	
	public Struct insertToken(Token t,int index) {
		tokens.add(index, t);
		return this;
	}

	public Struct appendToken(TokenType type, int countOrLength) {
		return appendToken(new Token(type, countOrLength));
	}
	
	public Struct insertToken(TokenType type, int countOrLength, int index) {
		return insertToken(new Token(type, countOrLength),index);
	}
	
	public Struct byteOrder(ByteOrder bo) {
		this.byteOrder = bo;
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
		for (Token t : tokens)
			count += t.tokenCount();
		return count;
	}
	
	

	public byte[] packEntity(Object o) {
				
		try {
			StructEntityConfig config = StructEntityConfig.get(o.getClass());
			
			if(!config.struct.tokens.equals(this.tokens)) {
				LOGGER.error("struct tokens: {} do not match class tokens: {}",this.tokens,config.struct.tokens);
				throw new IllegalArgumentException("Object does not match this struct");
			}
			
			List<Object> values = new ArrayList<Object>();
			for(Field field : config.fields) {
				Object value = field.get(o);
				values.add(value);
			}
									
			return this.pack(values);
		} 
		catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		} 
		
	}
	
	public <T> T unpackEntity(Class<T> c, byte[] bytes) {
				
		try {
			StructEntityConfig config = StructEntityConfig.get(c);
			
			if(!config.struct.tokens.equals(this.tokens)) {
				LOGGER.error("struct tokens: {} do not match class tokens: {}",this.tokens,config.struct.tokens);
				throw new IllegalArgumentException("Object does not match this struct");
			}
			
			List<Object> values = this.unpack(bytes);
			
			T instance = c.newInstance();
			
			for(int i=0;i<values.size();i++) {
				Field field = config.fields.get(i);
				Object value = values.get(i);
				field.set(instance, value);
			}
			
			return instance;
		} 
		catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
				
	}
	
	public byte[] pack(Object...objects ) {
		return pack(Arrays.asList(objects));
	}
	
	public byte[] pack(List<Object> vals) {

		Struct format = this;
		int len = format.tokenCount();
		if (len != vals.size())throw new IllegalArgumentException("format tokens: " + len + " does not equal value tokens: " + vals.size());

		byte[] result = new byte[format.byteCount()];

		int position = 0;
		
		Iterator<Token> tokens = format.tokens.iterator();
		Iterator<Object> values = vals.iterator();
		java.nio.ByteOrder byteOrder = format.byteOrder.getByteOrder();
		
		while(tokens.hasNext()) {
			
			Token token = tokens.next();
			int length = token.length;
			
			for (int i = 0; i < token.tokenCount(); i++) {
				Object val = values.next();
				
				byte[] packedBytes;
				switch (token.type) {
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
					case Long:
					case LongUnsigned:
						packedBytes = Packer.packRaw_64b(((long) val & 0xffffffffffffffffl), byteOrder);
						break;
					case Double:
						packedBytes = Packer.packFloat_64b((double) val, byteOrder);
						break;
					case Bytes:
						packedBytes = (byte[]) val;
						break;
					case String:
						packedBytes = ((String)val).getBytes(format.charset);
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
				
				if(packedBytes.length != length) throw new IllegalArgumentException("Expected "+length +" bytes but found "+packedBytes.length+" for value "+val);
				
				System.arraycopy(packedBytes, 0, result, position, packedBytes.length);
				position += packedBytes.length;
				
				//if(token.type.array) break;
			}
			
		}
		


		return result;

	}


	public List<Object> unpack(InputStream stream) throws IOException{
		byte[] buffer = new byte[byteCount()];
		IOUtils.readFully(stream, buffer);
		return unpack(buffer);
	}
	
	
	
	public List<Object> unpack(byte[] vals) {
		
		Struct format = this;
		
		int len = format.byteCount();

		if (len != vals.length) throw new IllegalArgumentException("format length: " + len + " does not equal value length: " + vals.length);

		List<Object> tokens = new ArrayList<Object>();

		//reuse arrays when possible
		byte[][] arrays = new byte[][] {null,new byte[1],new byte[2],null,new byte[4],null,null,null,new byte[8]};

		try {
			ByteArrayInputStream bs = new ByteArrayInputStream(vals);

			for (Token token : format.tokens) {

				count: for (int i = 0; i < token.tokenCount(); i++) {
					
					byte[] ar = token.type.array?null:arrays[token.type.size];
					Object o = unpack(token.type, token.length,bs, byteOrder, charset, ar);
					tokens.add(o);
					if(token.type.array) break count;

				}

			}

		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

		return tokens;

	}
	
	public static Struct create(String fmt) {
		return create(fmt, null);
	}

	public static Struct create(Class<?> structClass) {
		return StructEntityConfig.get(structClass).createStruct();
	}
	


	public static Struct create(String fmt, Charset cs) {
		Struct format = new Struct(null, cs);
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
					if(countOrLength<1) throw new IllegalArgumentException("Count for token "+type+" must be > 0");
					countOrLengthBuffer.setLength(0);
				}
				format.appendToken(type, countOrLength);
			}
		}

		return format;
	}
	
	public static Object unpack(TokenType type, ByteOrder bo,InputStream is) throws IOException {
		return unpack(type, 1, is, bo, Charset.defaultCharset(), new byte[type.size]);
	}
	
	public static Object unpack(TokenType type, ByteOrder bo,byte[] bytes) throws IOException {
		return unpack(type, bo, Charset.defaultCharset(),bytes);
	}
	
	public static Object unpack(TokenType type, InputStream is) throws IOException {
		return unpack(type, 1, is, ByteOrder.nativeOrder(), Charset.defaultCharset(), new byte[type.size]);
	}
	
	public static Object unpack(TokenType type, Integer length,InputStream bs,ByteOrder byteOrder,Charset charset,byte[] bytes) throws IOException {
		if(bytes == null) bytes = new byte[length];
		IOUtils.readFully(bs, bytes);
		return unpack(type,  byteOrder, charset, bytes);
	}
	
	public static Object unpack(TokenType type, ByteOrder order,Charset charset,byte[] bytes) throws IOException {
	
		java.nio.ByteOrder byteOrder = order.getByteOrder();
				
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
				//no such thing as an unsigned 64 bit in java so we process as signed
			case Long:
				return Packer.unpackRaw_64b(bytes, byteOrder);
			case Double:
				return Packer.unpackFloat_64b(bytes, byteOrder);
			case Bytes:
				return bytes;
			case String:
				String string = new String(bytes, charset);
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
		Struct other = (Struct) obj;
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

	 
	
	/*	public static Format createFormat(String fmt) {
			return createFormat(fmt, null);
		}
	
		public static Format createFormat(String fmt, Charset cs) {
			Format format = new Format(null, cs);
			Character x = null;
			StringBuilder buf = new StringBuilder();
			for (int i = 0; i < fmt.length(); i++) {
				x = fmt.charAt(i);
				if (BYTE_ORDER_MAP.keySet().contains(x)) {
					format.byteOrder = BYTE_ORDER_MAP.get(x);
				} else if (Character.isDigit(x)) {
					buf.append(x);
				} else {
					TokenType type = TokenType.valueOf(x.toString());
					int multiplier = 1;
					if (buf.length() > 0) {
						multiplier = Integer.valueOf(buf.toString());
						if(multiplier<1) throw new IllegalArgumentException("Count for token "+type+" must be > 0");
						buf.setLength(0);
					}
					format.appendToken(type, multiplier);
				}
			}
	
			return format;
		}
	*/
	/*public static class Format {
		protected List<Token> tokens = new ArrayList<Struct.Token>();
		protected ByteOrder byteOrder;
		protected Charset charset;
	
		public Format() {
		}
	
		public Format(ByteOrder byteOrder, Charset charset) {
			super();
			this.byteOrder = byteOrder != null ? byteOrder : ByteOrder.nativeOrder();
			this.charset = charset != null ? charset : Charset.defaultCharset();
		}
	
		public void appendToken(Token t) {
			tokens.add(t);
		}
	
		public void appendToken(TokenType type, int count) {
			appendToken(new Token(type, count));
		}
	
		public int byteCount() {
			int size = 0;
			for (Token t : tokens)
				size += t.byteCount();
			return size;
		}
		
		public int tokenCount() {
			int count = 0;
			for (Token t : tokens)
				count += t.tokenCount();
			return count;
		}
	
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("[byteOrder=");
			builder.append(byteOrder);
			builder.append(", tokens=");
			builder.append(tokens);
			builder.append("]");
			return builder.toString();
		}
	
	}*/
	
}