package org.thshsh.struct;

public class StructChildLong extends StructParent {


	
	
	public StructChildLong(String name, Number id) {
		super(name, id);
	}

	@StructToken(order = 1)
	public void setId(Long id) {
		super.setId(id);
	}
	
	

}
