package org.thshsh.struct;

public class EntityChildLong extends EntityParent {


	
	
	public EntityChildLong(String name, Number id) {
		super(name, id);
	}

	@StructToken(order = 1)
	public void setId(Long id) {
		super.setId(id);
	}
	
	

}
