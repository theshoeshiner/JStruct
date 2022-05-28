package org.thshsh.struct;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyTest {
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PropertyTest.class);

	
	public static class Parent {
	
		String base;
		Number id;
		
		
		@StructToken(order = 0)
		public String getBase() {
			return base;
		}

		public void setBase(String base) {
			this.base = base;
		}

		public Number getId() {
			return id;
		}

		public void setId(Number id) {
			this.id = id;
		}
		
		
		
	}
	
	public static class ChildLong extends Parent {
		

		@StructToken(order = 1)
		public void setId(Long id) {
			LOGGER.info("set id in child!");
			super.setId(id);
		}
		
	}
	
	
	public static class ChildInteger extends Parent {
		

		@StructToken(order = 0)
		public void setId(Integer id) {
			LOGGER.info("set id in child!");
			super.setId(id);
		}
		
	}
	
	@Test
	public void test() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		/*for(PropertyDescriptor d : PropertyUtils.getPropertyDescriptors(Child.class)) {
			LOGGER.info("d: {}",d);
			
		}
		Child c = new Child();
		PropertyUtils.setProperty(c, "id", 34l);*/
		
		Struct.create(ChildLong.class);
		Struct.create(ChildInteger.class);
		
		
	}

}
