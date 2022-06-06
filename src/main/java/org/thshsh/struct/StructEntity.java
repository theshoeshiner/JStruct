package org.thshsh.struct;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface StructEntity {

	/**
	 * The byte order to use for this entity
	 * 
	 * @return
	 */
	ByteOrder byteOrder() default ByteOrder.Native;

	/**
	 * The charset to use for this entity
	 * 
	 * @return
	 */
	String charset() default "";

	/**
	 * Tells the Struct to trim and pad this String or Array
	 * 
	 * @return
	 */
	boolean trimAndPad() default false;


}
