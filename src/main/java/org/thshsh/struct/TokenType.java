package org.thshsh.struct;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

/**
 * i and l are the same q and Q are treated the same because we cannot represent
 * an unsigned long
 * 
 *
 */
public enum TokenType {

	//this order matters, because TokenType.Auto will find the signed types first
	Short(2,false,"h",Short.class,java.lang.Short::valueOf,null,Objects::equals),
	Integer(4,false,"il",Integer.class,java.lang.Integer::valueOf,null,Objects::equals),
	Long(8,false,"q",Long.class,java.lang.Long::valueOf,null,Objects::equals),
	
	ShortUnsigned(2,false,"H",true,Integer.class,java.lang.Integer::valueOf,null,Objects::equals),
	IntegerUnsigned(4,false,"I",true,Long.class,java.lang.Long::valueOf,null,Objects::equals),
	LongUnsigned(8,false,"Q",true,Long.class,null,null,Objects::equals),
	
	Double(8,false,"d",Double.class,java.lang.Double::valueOf,null,Objects::equals),
	Bytes(1,true,"s",byte[].class,s-> {
		try {return Hex.decodeHex(s);} 
		catch (DecoderException e) {throw new IllegalArgumentException(e);}
	},s-> {return ((byte[])s).length;},(o1,o2) -> {
		return Arrays.equals((byte[])o1,(byte[])o2);
	}),
	Byte(1,false,"cb",Byte.class,java.lang.Byte::valueOf,null,Objects::equals),
	String(1,true,"S",String.class,java.lang.String::valueOf, s-> {return ((java.lang.String)s).length();},Objects::equals),
	Boolean(1,false,"t",Boolean.class,java.lang.Boolean::valueOf,null,Objects::equals),
	Auto(1,false,"a",null,null,null,null)
	;

	//size in bytes of this token type
	int size;
	boolean array;
	String characters;
	Class<?> defaultClass;
	boolean unsigned;
	Function<String, ?> convert;
	ToIntFunction<Object> lengthFunction;
	BiFunction<Object,Object,Boolean> equalsFunction;
	
	private TokenType(int size,boolean ar,String chars,Class<?> defaultClass,Function<String, ?> convert,ToIntFunction<Object> l,BiFunction<Object,Object,Boolean> equalsFunction) {
		this(size,ar,chars,false,defaultClass,convert,l,equalsFunction);
	}

	private TokenType(int size,boolean ar,String chars,boolean unsigned,Class<?> defaultClass,Function<String, ?> convert,ToIntFunction<Object> l,BiFunction<Object,Object,Boolean> equalsFunction) {
		this.size = size;
		this.array = ar;
		this.defaultClass = defaultClass;
		this.characters = chars;
		this.unsigned = unsigned;
		this.convert = convert;
		this.lengthFunction = l;
		this.equalsFunction = equalsFunction;
	}
	
	public Object convert(String value) {
		if(StringUtils.isEmpty(value)) return null;
		else return this.convert.apply(value);
	}
	
	public int length(Object value) {
		if(value == null) return 0;
		else if(lengthFunction == null) return size;
		else return this.lengthFunction.applyAsInt((Object)value);
	}

	public static TokenType fromCharacter(char c) {
		for(TokenType type : values()) {
			if(type.characters.indexOf(c) > -1) {
				return type;
			}
		}
		throw new IllegalArgumentException();
	}
	
	public static TokenType fromField(Field field) {
		
		StructToken st = field.getAnnotation(StructToken.class);
		Class<?> classs = field.getType();
		for(TokenType type : values()) {
			if(classs.equals(type.defaultClass) && st.unsigned() == type.unsigned) return type;
		}
		throw new IllegalArgumentException();
	}
	
	public static TokenType fromClass(Class<?> classs, boolean unsigned) {
		for(TokenType type : values()) {
			if(classs.equals(type.defaultClass)) return type;
		}
		throw new IllegalArgumentException("Cant create token type for: "+classs);
	}
}