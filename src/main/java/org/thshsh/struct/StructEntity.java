package org.thshsh.struct;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface StructEntity {

	
	ByteOrder byteOrder() default ByteOrder.Native;
	String charset() default "";
	boolean trimAndPad() default false;
	
	
}
