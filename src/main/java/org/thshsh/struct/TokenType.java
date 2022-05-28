package org.thshsh.struct;

import java.lang.reflect.Field;

/**
 * i and l are the same q and Q are treated the same because we cannot represent
 * an unsigned long
 * 
 * @author daniel.watson
 *
 */
public enum TokenType {

	//this order matters, because TokenType.Auto will find the signed types first
	Short(2,false,"h",Short.class),
	Integer(4,false,"il",Integer.class),
	Long(8,false,"q",Long.class),
	
	ShortUnsigned(2,false,"H",true,Integer.class),
	IntegerUnsigned(4,false,"I",true,Long.class),
	LongUnsigned(8,false,"Q",true,Long.class),
	
	Double(8,false,"d",Double.class),
	Bytes(1,true,"s",byte[].class),
	Byte(1,false,"cb",Byte.class),
	String(1,true,"S",String.class),
	Boolean(1,false,"t",Boolean.class),
	Auto(1,false,"a",null)
	;

	//size in bytes of this token type
	int size;
	boolean array;
	String characters;
	Class<?> defaultClass;
	boolean unsigned;
	
	private TokenType(int size,boolean ar,String chars,Class<?> defaultClass) {
		this(size,ar,chars,false,defaultClass);
	}

	private TokenType(int size,boolean ar,String chars,boolean unsigned,Class<?> defaultClass) {
		this.size = size;
		this.array = ar;
		this.defaultClass = defaultClass;
		this.characters = chars;
		this.unsigned = unsigned;
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