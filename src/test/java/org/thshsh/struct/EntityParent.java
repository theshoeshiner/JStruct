package org.thshsh.struct;

public class EntityParent {
	
	@StructToken(order=0,length = 3)
	protected String name;
	
	protected Number id;

	public EntityParent(String name, Number id) {
		super();
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Number getId() {
		return id;
	}

	public void setId(Number id) {
		this.id = id;
	}
	
	

}
