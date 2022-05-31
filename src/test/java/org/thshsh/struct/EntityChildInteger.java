package org.thshsh.struct;

public class EntityChildInteger extends EntityParent {

	public EntityChildInteger(String name, Number id) {
		super(name, id);
	}

	@StructToken(order = 1)
	public void setId(Integer id) {
		super.setId(id);
	}
	
	
}
