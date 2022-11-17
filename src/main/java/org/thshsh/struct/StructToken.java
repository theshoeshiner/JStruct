package org.thshsh.struct;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD})
public @interface StructToken {
	
	public static final int NULL = Integer.MIN_VALUE;

	int order() default NULL;
	int offset() default NULL;
	TokenType type() default TokenType.Auto;
	int length() default 0;
	boolean unsigned() default false; //tells the API that this is an unsigned value
	boolean unsignedNarrow() default false; //TODO implement this - tells the api to force the value into a signed primitive rather than upcasting to the next largest type
	String constant() default "";
	boolean validate() default true;
	
}
