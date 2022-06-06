package org.thshsh.struct;

public class EntityConstant3 {
	
	public static final String CONSTANT = "SOMECONSTANT";
	
	@StructTokenPrefix({
		@StructToken(type = TokenType.String,constant = "MYCONPREFIX")
		})
	@StructToken(order = 12,length = 2)
	@StructTokenSuffix({@StructToken(type = TokenType.String,constant = "MYCONSUF111")})
	public String myConstant;
	
	@StructTokenPrefix({@StructToken(type = TokenType.String,constant = "MYCONPREFIX2")})
	@StructToken(order = 13,length = 5)
	@StructTokenSuffix({@StructToken(type = TokenType.String,constant = "MYCONSUF2")})
	public String val;
	
	public EntityConstant3() {}
	
	public EntityConstant3(String val,String other) {
		super();
		this.val = val;
		this.myConstant = other;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((myConstant == null) ? 0 : myConstant.hashCode());
		result = prime * result + ((val == null) ? 0 : val.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EntityConstant3 other = (EntityConstant3) obj;
		if (myConstant == null) {
			if (other.myConstant != null)
				return false;
		} else if (!myConstant.equals(other.myConstant))
			return false;
		if (val == null) {
			if (other.val != null)
				return false;
		} else if (!val.equals(other.val))
			return false;
		return true;
	}

	
}
