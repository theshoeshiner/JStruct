package org.thshsh.struct;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Caches the struct annotation mapping for a class
 *
 */
public class StructEntityMapping<T> {
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StructEntityMapping.class);

	
	protected static final Map<Class<?>,StructEntityMapping<?>> CACHE = new HashMap<Class<?>, StructEntityMapping<?>>();
	
	protected Class<T> structClass;
	protected Struct<T> struct;
	protected StructEntity classAnnotation;
	protected List<Mapping> mappings;
	
	public StructEntityMapping(Class<T> structClass,StructEntity classAnnotation,List<Mapping> p) {
		super();
		this.classAnnotation = classAnnotation;
		this.structClass = structClass;
		this.mappings = p;
		this.struct = createStruct(this);
	}
	
	public Struct<T> createStruct() {
		return createStruct(this);
	}
	
	public int prefix() {
		return classAnnotation!=null?classAnnotation.prefix():0;
	}
	
	public int suffix() {
		return classAnnotation!=null?classAnnotation.suffix():0;
	}
	
	public void validate(Struct<?> struct) {
		if(!this.struct.tokens.equals(struct.tokens)) {
			LOGGER.error("struct tokens: {} do not match class tokens: {}",struct.tokens,this.struct.tokens);
			throw new IllegalArgumentException("Object does not match this struct");
		}
	}
	
	protected static <T> StructEntityMapping<T> create(Class<T> structClass) {
		
		StructEntity sc = structClass.getAnnotation(StructEntity.class);
		List<Mapping> properties = new ArrayList<Mapping>();
			
		for(PropertyDescriptor d : PropertyUtils.getPropertyDescriptors(structClass)) {
			Mapping mapping = null;
			if(d.getReadMethod() != null && d.getReadMethod().isAnnotationPresent(StructToken.class)) {
				mapping = new Mapping(d, null,d.getReadMethod().getAnnotation(StructToken.class),d.getReadMethod().getReturnType());
			}
			else if(d.getWriteMethod() != null && d.getWriteMethod().isAnnotationPresent(StructToken.class)) {
				mapping = new Mapping(d,null, d.getWriteMethod().getAnnotation(StructToken.class),d.getWriteMethod().getParameterTypes()[0]);
			}
			if(mapping != null) {
				properties.add(mapping);
			}
		};
			

		Class<?> search = structClass;
		
		do {
			for(Field field :search.getDeclaredFields()) {

				if(field.isAnnotationPresent(StructToken.class)) {
					StructToken annotation = field.getAnnotation(StructToken.class);
					try {
						PropertyDescriptor pd = new PropertyDescriptor(field.getName(), search);
						Mapping mapping = new Mapping(pd,null, annotation,field.getType());
						properties.add(mapping);
					} 
					catch (IntrospectionException e) {
						
						if(!Modifier.isPublic(field.getModifiers()) && StringUtils.isEmpty(annotation.constant())) {
							throw new IllegalArgumentException("Field "+field.getName()+" has no property accessors and is not public or a constant");
						}
						else {
							Mapping mapping = new Mapping(null,field, annotation,field.getType());
							properties.add(mapping);
						}
					}
				}
			}
			search = search.getSuperclass();
		}
		while(search != null);
		
		if(properties.size()==0) throw new IllegalArgumentException("No Struct properties found on class "+structClass.getCanonicalName());
		Set<Integer> orders = new HashSet<>();
		for(Mapping m : properties) {
			Integer i = m.annotation.order();
			if(orders.contains(i)) throw new IllegalArgumentException("Found two StructTokens at index "+i+" for entity "+structClass);
			orders.add(i);
		}
		properties.sort((f0,f1)-> {
			return ((Integer)f0.annotation.order()).compareTo(f1.annotation.order());
			
		});
		StructEntityMapping<T> config = new StructEntityMapping<T>(structClass,sc,properties);

		return config;

	}
	
	public static class Mapping {
		protected PropertyDescriptor property;
		protected Field field;
		protected StructToken annotation;
		protected Class<?> type;
		public Mapping(PropertyDescriptor property, Field f,StructToken annotation, Class<?> type) {
			super();
			this.field = f;
			this.property = property;
			this.annotation = annotation;
			this.type = type;
		}
		public void setValue(Object instance, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
			if(isConstant()) {
				//confirm constant has correct value
				String constant = annotation.constant();
				if(!constant.equals(value)) throw new IllegalArgumentException("Value: '"+value+"' does not match specified constant: '"+constant+"'");
			}
			else {
				if(property != null) {
					PropertyUtils.setSimpleProperty(instance, property.getName(), value);
				}
				else {
					field.set(instance, value);
				}
			}
		}
		public Object getValue(Object o) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
			if(isConstant())
				return annotation.constant();
			else if(property != null) 
				return PropertyUtils.getSimpleProperty(o, property.getName());
			else 
				return field.get(o);
		}
		public boolean isConstant() {
			return StringUtils.isNotEmpty(annotation.constant());
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> StructEntityMapping<T> get(Class<T> classs) {
		if(!CACHE.containsKey(classs)) CACHE.put(classs, create(classs));
		return (StructEntityMapping<T>) CACHE.get(classs);
	}
	
	public static <T> Struct<T> createStruct(StructEntityMapping<T> config) {
		Struct<T> s = new Struct<T>();
		s.entityClass = config.structClass;		
		
		if(config.classAnnotation!=null) {
			
			if(config.classAnnotation.prefix() > 0) {
				Token t = new Token(TokenType.Bytes,1, config.classAnnotation.prefix(),0,0,null);
				s.appendToken(t);
			}
			
			if(StringUtils.isNotBlank(config.classAnnotation.charset())){
				s.charset = Charset.forName(config.classAnnotation.charset());
			}
			s.byteOrder(config.classAnnotation.byteOrder())
			.trimAndPad(config.classAnnotation.trimAndPad());
		}
		
	
		for(Mapping mapping : config.mappings) {
			StructToken st = mapping.annotation;
			Object constant = mapping.isConstant()?st.constant():null;
			int length = mapping.isConstant()?st.constant().length():st.length();
			TokenType tt = st.type();
			if(st.type() == TokenType.Auto) {
				tt = TokenType.fromClass(mapping.type, false);
			}
			Token t = new Token(tt,st.count(), length,st.prefix(),st.suffix(),constant);
			s.appendToken(t);
		}
		
		if(config.classAnnotation!=null && config.classAnnotation.suffix() > 0) {
			Token t = new Token(TokenType.Bytes,1, config.classAnnotation.suffix(),0,0,null);
			s.appendToken(t);
		}
		
		
		return s;
	}

}
