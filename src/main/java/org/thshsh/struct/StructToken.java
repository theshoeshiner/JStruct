package org.thshsh.struct;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD})
public @interface StructToken {

	int order();
	TokenType type() default TokenType.Auto;
	int count() default 1;
	int length() default 0;
	boolean unsigned() default false;
	int prefix() default 0;
	int suffix() default 0;
	
}
