package org.thshsh.struct;

public class EntityInvalidLength {

	@StructToken(order=0,length = 2)
	Short id;

	public Short getId() {
		return id;
	}

	public void setId(Short id) {
		this.id = id;
	}
	
	
	
}
