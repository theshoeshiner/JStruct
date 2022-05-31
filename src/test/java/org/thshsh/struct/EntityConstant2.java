package org.thshsh.struct;

public class EntityConstant2 extends EntityEverything  {

public static final String CONSTANT = "THISISCONSTANTISDIFFERENT";
	
	@StructToken(order = 12,constant = CONSTANT)
	public String myConstant;

	public EntityConstant2() {
		super();
	}

	public EntityConstant2(String myString, Short myShort, Integer myInteger, Long myLong, Double myDouble, byte[] myByteArray, Boolean myBoolean,
			Byte myByte, Integer myShortUnsigned, Long myIntegerUnsigned, Long myLongUnsigned) {
		super(myString, myShort, myInteger, myLong, myDouble, myByteArray, myBoolean, myByte, myShortUnsigned, myIntegerUnsigned, myLongUnsigned);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((myConstant == null) ? 0 : myConstant.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EntityConstant2 other = (EntityConstant2) obj;
		if (myConstant == null) {
			if (other.myConstant != null)
				return false;
		} else if (!myConstant.equals(other.myConstant))
			return false;
		return true;
	}
	
	
}
