package org.thshsh.struct;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Caches the struct annotation mapping for a class
 *
 */
public class StructEntityConfig {
	
	protected static Map<Class<?>,StructEntityConfig> CACHE = new HashMap<Class<?>, StructEntityConfig>();
	
	public final Struct struct;
	public final List<Field> fields;
	public final StructEntity classAnnotation;
	
	public StructEntityConfig(StructEntity classAnnotation,List<Field> fields) {
		super();
		this.classAnnotation = classAnnotation;
		this.fields = fields;
		this.struct = createStruct(this);
	}
	
	public Struct createStruct() {
		return createStruct(this);
	}
	
	protected static StructEntityConfig create(Class<?> structClass) {
		StructEntity sc = structClass.getAnnotation(StructEntity.class);
		List<Field> fields = new ArrayList<Field>();
		for(Field field :structClass.getFields()) {
			if(field.isAnnotationPresent(StructToken.class)) {
				fields.add(field);
			}
		}
		fields.sort((f0,f1)-> {
			return ((Integer)f0.getAnnotation(StructToken.class).order()).compareTo(f1.getAnnotation(StructToken.class).order());
		});
		StructEntityConfig config = new StructEntityConfig(sc,fields);
		return config;

	}
	
	public static StructEntityConfig get(Class<?> classs) {
		if(!CACHE.containsKey(classs)) CACHE.put(classs, create(classs));
		return CACHE.get(classs);
	}
	
	public static Struct createStruct(StructEntityConfig config) {
		Struct s = new Struct();
		for(Field field : config.fields) {
			StructToken st = field.getAnnotation(StructToken.class);
			TokenType tt = st.type();
			if(st.type() == TokenType.Auto) {
				tt = TokenType.fromField(field);
			}
			s.appendToken(tt, st.length());
		}
		return s;
	}

}
