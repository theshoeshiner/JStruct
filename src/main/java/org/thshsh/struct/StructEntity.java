package org.thshsh.struct;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface StructEntity {

	//TODO handle these in Struct class
	ByteOrder byteOrder() default ByteOrder.Native;
	boolean pad() default false;
	byte padByte() default 0;
	
}
