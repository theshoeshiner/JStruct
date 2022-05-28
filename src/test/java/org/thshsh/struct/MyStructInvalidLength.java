package org.thshsh.struct;

public class MyStructInvalidLength {

	@StructToken(order=0,length = 2)
	Short id;

	public Short getId() {
		return id;
	}

	public void setId(Short id) {
		this.id = id;
	}
	
	
	
}
