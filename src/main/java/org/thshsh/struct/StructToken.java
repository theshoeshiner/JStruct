package org.thshsh.struct;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface StructToken {

	int order();
	TokenType type() default TokenType.Auto;
	int length() default 1;
	boolean unsigned() default false;
	
}
