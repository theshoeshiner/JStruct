package org.thshsh.struct;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mapping {
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(Mapping.class);

	protected PropertyDescriptor property;
	protected Field field;
	protected StructToken annotation;
	protected StructTokenPrefix prefix;
	protected StructTokenSuffix suffix;
	
	List<Token> prefixTokens;
	Token token;
	List<Token> suffixTokens;
	
	protected Class<?> type;
	protected Object constantValue;
	protected TokenType tokenType;
	protected Integer length;
	
	public Mapping(PropertyDescriptor property, Field f,StructToken annotation, StructTokenPrefix pre, StructTokenSuffix suf,Class<?> type) {
		super();
		this.field = f;
		this.property = property;
		this.annotation = annotation;
		this.type = type;
		this.tokenType = annotation.type() != TokenType.Auto?annotation.type():TokenType.fromClass(type, false);
		this.prefix = pre;
		this.suffix = suf;
		this.prefixTokens = new ArrayList<Token>();
		this.suffixTokens = new ArrayList<Token>();
		this.length = 0;
		
		if(StringUtils.isNotEmpty(annotation.constant())) {
			if(tokenType.convert == null) throw new MappingException("Cannot specify constants for TokenType "+tokenType);
			else {
				//convert constant strings to actual token values
				constantValue = tokenType.convert(annotation.constant());
			}
		}
		
		
		if(prefix != null) {
			for(StructToken token : prefix.value()) {
				Token t = StructEntityMapping.annotationToToken(token);
				if(t.constant==null) throw new MappingException("StructToken prefix/suffix must specify a constant");
				t.hide=true;
				prefixTokens.add(t);
				length+=t.byteCount();
			}
		}
		
		{
			StructToken st = annotation;
			TokenType tt = tokenType;
			Token t = StructEntityMapping.annotationToToken(st, tt);
			length+=t.byteCount();
			this.token = t;
		}
		
		if(suffix != null) {
			for(StructToken token : suffix.value()) {
				Token t = StructEntityMapping.annotationToToken(token);
				if(t.constant==null) throw new MappingException("StructToken prefix/suffix must specify a constant");
				t.hide=true;
				length+=t.byteCount();
				suffixTokens.add(t);
			}
		}
		
	}
	
	public void addPrefixBytes(int count) {
		Token t = new Token(TokenType.Bytes, count);
		t.hide = true;
		length+=count;
		prefixTokens.add(t);
	}
	
	public void setValue(Object instance, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if(property != null) {
			PropertyUtils.setSimpleProperty(instance, property.getName(), value);
		}
		else {
			field.set(instance, value);
		}
	}
	
	
	public Object getValue(Object o) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if(isConstant())
			return constantValue;
		else if(property != null) 
			return PropertyUtils.getSimpleProperty(o, property.getName());
		else 
			return field.get(o);
	}
	

	
	public boolean isConstant() {
		return constantValue != null;
	}
}