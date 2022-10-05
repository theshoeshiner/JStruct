package org.thshsh.struct;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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

	
	public void validate(Struct<?> struct) {
		if(!this.struct.tokens.equals(struct.tokens)) {
			LOGGER.error("struct tokens: {} do not match class tokens: {}",struct.tokens,this.struct.tokens);
			throw new CountMismatchException(struct.tokens.size(), this.struct.tokens.size());
		}
	}
	
	protected static <T> StructEntityMapping<T> create(Class<T> structClass) {
		
		StructEntity sc = structClass.getAnnotation(StructEntity.class);
		List<Mapping> properties = new ArrayList<Mapping>();
			
		for(PropertyDescriptor d : PropertyUtils.getPropertyDescriptors(structClass)) {
			Mapping mapping = null;
			Method method = null;
			Class<?> type = null;
			if(d.getReadMethod() != null && d.getReadMethod().isAnnotationPresent(StructToken.class)) {
				method = d.getReadMethod();
				type = method.getReturnType();
			}
			else if(d.getWriteMethod() != null && d.getWriteMethod().isAnnotationPresent(StructToken.class)) {
				method = d.getWriteMethod();
				type = method.getParameterTypes()[0];
			}
			
			if(method != null) {
				mapping = new Mapping(d, null,method.getAnnotation(StructToken.class),method.getAnnotation(StructTokenPrefix.class),method.getAnnotation(StructTokenSuffix.class),type);
				properties.add(mapping);
			}
	
		};
			

		Class<?> search = structClass;
		
		do {
			for(Field field :search.getDeclaredFields()) {

				if(field.isAnnotationPresent(StructToken.class)) {
					StructToken annotation = field.getAnnotation(StructToken.class);
					StructTokenPrefix pre = field.getAnnotation(StructTokenPrefix.class);
					StructTokenSuffix suf = field.getAnnotation(StructTokenSuffix.class);
					try {
						PropertyDescriptor pd = new PropertyDescriptor(field.getName(), search);
						Mapping mapping = new Mapping(pd,null, annotation,pre,suf,field.getType());
						properties.add(mapping);
					} 
					catch (IntrospectionException e) {
						
						if(!Modifier.isPublic(field.getModifiers()) && StringUtils.isEmpty(annotation.constant())) {
							throw new MappingException("Field "+field.getName()+" has no property accessors and is not public or a constant",e);
						}
						else {
							Mapping mapping = new Mapping(null,field, annotation,pre,suf,field.getType());
							properties.add(mapping);
						}
					}
				}
			}
			search = search.getSuperclass();
		}
		while(search != null);
		
		if(properties.size()==0) throw new MappingException("No Struct properties found on class "+structClass.getCanonicalName());
		

		Collections.sort(properties, (p0,p1) -> {
			return  ((Integer)p0.annotation.order()).compareTo(p1.annotation.order());
		});
		
		Set<Integer> orders = new HashSet<>();
		Set<Integer> offsets = new HashSet<>();
		Map<Integer,Mapping> offsetMap = new TreeMap<>();
		Integer length = 0;
		for(Mapping m : properties) {
			//LOGGER.info("ann: {}",m.annotation);
			//Token t = annotationToToken(m.annotation,m.tokenType);
			Integer order = m.annotation.order();
			Integer offset = m.annotation.offset();
			
			if((order.equals((Integer)StructToken.NULL) && offset.equals((Integer)StructToken.NULL)) ||
				(!order.equals((Integer)StructToken.NULL) && !offset.equals((Integer)StructToken.NULL))
			) throw new MappingException("One of Index or Offset must be specified for mapped token on entity: "+structClass);
			
			if(order.equals((Integer)StructToken.NULL)) {
				//use offset
				if(offsets.contains(offset)) throw new MappingException("Found two StructTokens at offset "+offset+" for entity "+structClass);
				offsets.add(offset);
				offsetMap.put(offset, m);
			}
			else {
				//derive offset
				if(orders.contains(order)) throw new MappingException("Found two StructTokens at index "+order+" for entity "+structClass);
				if(offsets.contains(length)) throw new MappingException("Found two StructTokens at offset "+length+" for entity "+structClass);
				LOGGER.debug("deriving offset from order: {} = {}",order,length);
				orders.add(order);
				offsets.add(length);
				offsetMap.put(length, m);
				length+=m.length;
			}

		}
		
		LOGGER.debug("offsetMap: {}",offsetMap);
		
		List<Mapping> finalList = new ArrayList<>();
		
		Integer currentOffset = 0;
		for(Integer offset : offsetMap.keySet()) {
			
			Mapping mapping = offsetMap.get(offset);
			LOGGER.debug("offset: {} has mapping: {}",offset,mapping.token);
			LOGGER.debug("currentOffset: {}",currentOffset);
			if(offset.equals(currentOffset)) {
				finalList.add(mapping);
			}
			else {
				LOGGER.debug("Offset is off by {}",offset-currentOffset);
				mapping.addPrefixBytes(offset-currentOffset);
				//currentOffset = offset;
			}
			currentOffset += mapping.length;
		}
		
		properties.sort((f0,f1)-> {
			return ((Integer)f0.annotation.order()).compareTo(f1.annotation.order());
			
		});
		StructEntityMapping<T> config = new StructEntityMapping<T>(structClass,sc,properties);

		return config;

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
			
			
			if(StringUtils.isNotBlank(config.classAnnotation.charset())){
				s.charset = Charset.forName(config.classAnnotation.charset());
			}
			s.byteOrder(config.classAnnotation.byteOrder())
			.trimAndPad(config.classAnnotation.trimAndPad());
		}
		
	
		for(Mapping mapping : config.mappings) {
			
			s.appendTokens(mapping.prefixTokens);
			s.appendToken(mapping.token);
			s.appendTokens(mapping.suffixTokens);
			
		
		}
		

		
		return s;
	}
	
	public static Token annotationToToken(StructToken anno) {
		return annotationToToken(anno, anno.type());
	}
	
	public static Token annotationToToken(StructToken anno,TokenType type) {
		
		if(type == null) {
			if(anno.type() == TokenType.Auto) throw new MappingException("Type must be specified for annotation: "+anno);
			type = anno.type();
		}
		int length = anno.length();
		
		//get constant value and length
		Object constantValue = null;
		if(StringUtils.isNotEmpty(anno.constant())){
			if(type.convert == null) throw new MappingException("Cannot specify constants for TokenType: "+type);
			constantValue = type.convert(anno.constant());
			length = type.length(constantValue);
		}
		
		
		Token t = new Token(type,1, length,constantValue,anno.validate());
		
		return t;
	}

}
