package org.thshsh.struct;

public class StructChildInteger extends StructParent {

	public StructChildInteger(String name, Number id) {
		super(name, id);
	}

	@StructToken(order = 1)
	public void setId(Integer id) {
		super.setId(id);
	}
	
	
}
