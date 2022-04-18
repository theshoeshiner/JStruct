package org.thshsh.struct;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

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


	private static final Map<Character, ByteOrder> BYTE_ORDER_MAP = new HashMap<Character, ByteOrder>();
	static {
		BYTE_ORDER_MAP.put('>', ByteOrder.BIG_ENDIAN);
		BYTE_ORDER_MAP.put('<', ByteOrder.LITTLE_ENDIAN);
		BYTE_ORDER_MAP.put('!', ByteOrder.BIG_ENDIAN);
		BYTE_ORDER_MAP.put('@', ByteOrder.nativeOrder());
	}

	protected List<Token> tokens = new ArrayList<Struct.Token>();
	protected ByteOrder byteOrder;
	protected Charset charset;
	

	public Struct(ByteOrder byteOrder, Charset charset) {
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
	
	public static Struct create(String fmt) {
		return create(fmt, null);
	}

	public static Struct create(String fmt, Charset cs) {
		Struct format = new Struct(null, cs);
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

	protected static byte[] packRaw_16b(short val, ByteOrder byteOrder) {
		
		
		byte[] bx = new byte[2];

		bx[0] = (byte) (val & 0xff);
		bx[1] = (byte) ((val >>> 8) & 0xff);

		if (byteOrder == ByteOrder.BIG_ENDIAN) ArrayUtils.reverse(bx);

		return bx;
	}

	protected static byte[] packRaw_u16b(int val, ByteOrder byteOrder) {
		byte[] bx = new byte[2];

		bx[0] = (byte) (val & 0xff);
		bx[1] = (byte) ((val >>> 8) & 0xff);

		if (byteOrder == ByteOrder.BIG_ENDIAN) ArrayUtils.reverse(bx);

		return bx;
	}

	protected static byte[] packRaw_32b(int val, ByteOrder byteOrder) {
		byte[] bx = new byte[4];

		bx[0] = (byte) (val & 0xff);
		bx[1] = (byte) ((val >>> 8) & 0xff);
		bx[2] = (byte) ((val >>> 16) & 0xff);
		bx[3] = (byte) ((val >>> 24) & 0xff);

		if (byteOrder == ByteOrder.BIG_ENDIAN) ArrayUtils.reverse(bx);
		
		return bx;
	}

	protected static byte[] packRaw_64b(long val, ByteOrder byteOrder) {

		byte[] bx = new byte[8];

		bx[0] = (byte) (val & 0xff);
		bx[1] = (byte) ((val >>> 8) & 0xff);
		bx[2] = (byte) ((val >>> 16) & 0xff);
		bx[3] = (byte) ((val >>> 24) & 0xff);
		bx[4] = (byte) ((val >>> 32) & 0xff);
		bx[5] = (byte) ((val >>> 40) & 0xff);
		bx[6] = (byte) ((val >>> 48) & 0xff);
		bx[7] = (byte) ((val >>> 56) & 0xff);

		if (byteOrder == ByteOrder.BIG_ENDIAN) ArrayUtils.reverse(bx);
		
		return bx;
	}

	protected static byte[] packFloat_64b(double val, ByteOrder byteOrder) {

		long bits = Double.doubleToLongBits(val);
		byte[] bytes = packRaw_64b(bits, byteOrder);
		return bytes;

	}

	protected static byte[] packRaw_u32b(long val, ByteOrder byteOrder) {
		byte[] bx = new byte[4];

		val = val & 0xffffffff;

		if (val >= 0) {
			bx[0] = (byte) (val & 0xff);
			bx[1] = (byte) ((val >> 8) & 0xff);
			bx[2] = (byte) ((val >> 16) & 0xff);
			bx[3] = (byte) ((val >> 24) & 0xff);

		}

		if (byteOrder == ByteOrder.BIG_ENDIAN) ArrayUtils.reverse(bx);
		
		return bx;
	}

	
	public byte[] pack(List<Object> vals) {

		Struct format = this;
		int len = format.tokenCount();
		if (len != vals.size())throw new IllegalArgumentException("format tokens: " + len + " does not equal value tokens: " + vals.size());

		byte[] result = new byte[format.byteCount()];

		int position = 0;
		
		Iterator<Token> tokens = format.tokens.iterator();
		Iterator<Object> values = vals.iterator();
		
		while(tokens.hasNext()) {
			
			Token token = tokens.next();
			
			for (int i = 0; i < token.tokenCount(); i++) {
				Object val = values.next();
				
				byte[] bx;
				switch (token.type) {
					case h:
						bx = packRaw_16b((short) val, format.byteOrder);
						break;
					case H:
						bx = packRaw_u16b((int) val, format.byteOrder);
						break;
					case i:
					case l:
						bx = packRaw_32b(((int) val & 0xffffffff), format.byteOrder);
						break;
					case I:
						bx = packRaw_u32b((long) val, format.byteOrder);
						break;
					case q:
					case Q:
						bx = packRaw_64b(((long) val & 0xffffffffffffffffl), format.byteOrder);
						break;
					case d:
						bx = packFloat_64b((double) val, format.byteOrder);
						break;
					case s:
						bx = (byte[]) val;
						break;
					case S:
						bx = ((String)val).getBytes(format.charset);
						break;
					default:
						throw new IllegalArgumentException("Unhandled case: " + token.type);

				}
				
				System.arraycopy(bx, 0, result, position, bx.length);
				position += bx.length;
				
				if(token.type.array) break;
			}
			
		}
		


		return result;

	}


	protected static short unpackRaw_16b(byte[] val, ByteOrder byteOrder) {

		if (byteOrder == ByteOrder.LITTLE_ENDIAN) ArrayUtils.reverse(val);

		int x = ((int) (val[0] & 0xff) << 8) | ((int) (val[1] & 0xff));

		return (short) x;
	}

	protected static int unpackRaw_u16b(byte[] val, ByteOrder byteOrder) {

		if (byteOrder == ByteOrder.LITTLE_ENDIAN) ArrayUtils.reverse(val);

		int x = ((val[0] & 0xff) << 8) | (val[1] & 0xff);
		return x;
	}

	protected static int unpackRaw_32b(byte[] val, ByteOrder byteOrder) {
		
		if (byteOrder == ByteOrder.LITTLE_ENDIAN) ArrayUtils.reverse(val);
		
		int x = ((int) (val[0] & 0xff) << 24) | ((int) (val[1] & 0xff) << 16) | ((int) (val[2] & 0xff) << 8)
				| ((int) (val[3] & 0xff));

		return x;
	}

	protected static long unpackRaw_u32b(byte[] val, ByteOrder byteOrder) {
		
		if (byteOrder == ByteOrder.LITTLE_ENDIAN) ArrayUtils.reverse(val);

		long x = (((long) (val[0] & 0xff)) << 24) | (((long) (val[1] & 0xff)) << 16) | (((long) (val[2] & 0xff)) << 8)
				| ((long) (val[3] & 0xff));
		return x;
	}

	protected static long unpackRaw_64b(byte[] val, ByteOrder byteOrder) {
		
		if (byteOrder == ByteOrder.LITTLE_ENDIAN) ArrayUtils.reverse(val);

		long x = (((long) (val[0] & 0xff)) << 56) | (((long) (val[1] & 0xff)) << 48) | (((long) (val[2] & 0xff)) << 40)
				| (((long) (val[3] & 0xff)) << 32) | (((long) (val[4] & 0xff)) << 24) | (((long) (val[5] & 0xff)) << 16)
				| (((long) (val[6] & 0xff)) << 8) | ((long) (val[7] & 0xff));
		return x;
	}

	protected static double unpackFloat_64b(byte[] val, ByteOrder byteOrder) {

		if (byteOrder == ByteOrder.LITTLE_ENDIAN) ArrayUtils.reverse(val);

		long x = unpackRaw_64b(val, byteOrder);
		double d = Double.longBitsToDouble(x);

		return d;
	}

	public List<Object> unpack(byte[] vals) {

		Struct format = this;
		
		int len = format.byteCount();

		if (len != vals.length) throw new IllegalArgumentException("format length: " + len + " does not equal value length: " + vals.length);

		List<Object> tokens = new ArrayList<Object>();

		byte[] shortBytes = new byte[2];
		byte[] intBytes = new byte[4];
		byte[] longBytes = new byte[8];
		byte[] dynamicBytes;

		try {
			ByteArrayInputStream bs = new ByteArrayInputStream(vals);

			for (Token token : format.tokens) {

				count: for (int i = 0; i < token.tokenCount(); i++) {
					switch (token.type) {
					case H:
						bs.read(shortBytes);
						tokens.add(unpackRaw_u16b(shortBytes, format.byteOrder));
						break;
					case h:
						bs.read(shortBytes);
						tokens.add(unpackRaw_16b(shortBytes, format.byteOrder));
						break;
					case I:
						bs.read(intBytes);
						tokens.add(unpackRaw_u32b(intBytes, format.byteOrder));
						break;
					case i:
						bs.read(intBytes);
						tokens.add((int) unpackRaw_32b(intBytes, format.byteOrder));
						break;
					case l:
						bs.read(intBytes);
						tokens.add(unpackRaw_32b(intBytes, format.byteOrder));
						break;
					case q:
						// TODO no such thing as an unsigned 64 bit in java so we process as signed
					case Q:
						bs.read(longBytes);
						tokens.add(unpackRaw_64b(longBytes, format.byteOrder));
						break;
					case d:
						bs.read(longBytes);
						tokens.add(unpackFloat_64b(longBytes, format.byteOrder));
						break;
					case s:
						dynamicBytes = new byte[token.count];
						bs.read(dynamicBytes);
						tokens.add(dynamicBytes);
						break count;
					case S:
						dynamicBytes = new byte[token.count];
						bs.read(dynamicBytes);
						String string = new String(dynamicBytes, format.charset);
						tokens.add(string);
						break count;
					default:
						throw new IllegalArgumentException("unhandled case: " + token.type);
					}
				}

			}

		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

		return tokens;

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

	public static class Token {
		protected TokenType type;
		private int count;
		protected int byteCount;

		public Token(TokenType type, int count) {
			super();
			this.type = type;
			this.count = count;
			if (count == 0)
				throw new IllegalArgumentException("Count cannot be zero");
			byteCount = count * type.size;
		}

		public int tokenCount() {
			return type.array?1:count;
		}
		
		public int byteCount() {
			return byteCount;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("[type=");
			builder.append(type);
			builder.append(", count=");
			builder.append(count);
			builder.append(", byteCount=");
			builder.append(byteCount);
			builder.append("]");
			return builder.toString();
		}

	}

	/**
	 * i and l are the same q and Q are treated the same because we cannot represent
	 * an unsigned long
	 * 
	 * @author daniel.watson
	 *
	 */
	public enum TokenType {
		h(2,false), // short
		H(2,false), // short unsigned
		i(4,false), // integer
		l(4,false), // integer
		I(4,false), // integer unsigned
		q(8,false), // long
		Q(8,false), // long unsigned
		d(8,false), // double
		s(1,true), // byte array
		S(1,true) // string
		;

		//size in bytes of this token
		int size;
		//array types combine the entire count into one array
		boolean array;

		private TokenType(int size,boolean ar) {
			this.size = size;
			this.array = ar;
		}
	}
	
}