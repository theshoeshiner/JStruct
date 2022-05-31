package org.thshsh.struct;

import java.lang.reflect.Field;
import java.util.function.Function;

/**
 * i and l are the same q and Q are treated the same because we cannot represent
 * an unsigned long
 * 
 *
 */
public enum TokenType {

	//this order matters, because TokenType.Auto will find the signed types first
	Short(2,false,"h",Short.class,java.lang.Short::valueOf),
	Integer(4,false,"il",Integer.class,java.lang.Integer::valueOf),
	Long(8,false,"q",Long.class,java.lang.Long::valueOf),
	
	ShortUnsigned(2,false,"H",true,Integer.class,java.lang.Integer::valueOf),
	IntegerUnsigned(4,false,"I",true,Long.class,java.lang.Long::valueOf),
	LongUnsigned(8,false,"Q",true,Long.class,null),
	
	Double(8,false,"d",Double.class,java.lang.Double::valueOf),
	Bytes(1,true,"s",byte[].class,null),
	Byte(1,false,"cb",Byte.class,java.lang.Byte::valueOf),
	String(1,true,"S",String.class,java.lang.String::valueOf),
	Boolean(1,false,"t",Boolean.class,java.lang.Boolean::valueOf),
	Auto(1,false,"a",null,null)
	;

	//size in bytes of this token type
	int size;
	boolean array;
	String characters;
	Class<?> defaultClass;
	boolean unsigned;
	Function<String, ?> convert;
	
	private TokenType(int size,boolean ar,String chars,Class<?> defaultClass,Function<String, ?> convert) {
		this(size,ar,chars,false,defaultClass,convert);
	}

	private TokenType(int size,boolean ar,String chars,boolean unsigned,Class<?> defaultClass,Function<String, ?> convert) {
		this.size = size;
		this.array = ar;
		this.defaultClass = defaultClass;
		this.characters = chars;
		this.unsigned = unsigned;
		this.convert = convert;
		
		
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
		throw new IllegalArgumentException();
	}
}