package org.thshsh.struct;

import java.util.Arrays;

public class EntityConstant extends EntityEverything {
	
	public static final String CONSTANT = "thisisconstant";
	
	@StructToken(order = 12,constant = CONSTANT)
	public String myConstant;
	
	@StructToken(order = 13,constant = "12345")
	public Integer myIntegerConstant;
	
	@StructToken(order = 14,constant = Long.MAX_VALUE+"")
	public Long myLongConstant;

	public EntityConstant() {
		super();
	}

	public EntityConstant(String myString, Short myShort, Integer myInteger, Long myLong, Double myDouble, byte[] myByteArray, Boolean myBoolean,
			Byte myByte, Integer myShortUnsigned, Long myIntegerUnsigned, Long myLongUnsigned) {
		super(myString, myShort, myInteger, myLong, myDouble, myByteArray, myBoolean, myByte, myShortUnsigned, myIntegerUnsigned, myLongUnsigned);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EntityConstant [myConstant=");
		builder.append(myConstant);
		builder.append(", myString=");
		builder.append(myString);
		builder.append(", myShort=");
		builder.append(myShort);
		builder.append(", myInteger=");
		builder.append(myInteger);
		builder.append(", myLong=");
		builder.append(myLong);
		builder.append(", myDouble=");
		builder.append(myDouble);
		builder.append(", myByteArray=");
		builder.append(Arrays.toString(myByteArray));
		builder.append(", myBoolean=");
		builder.append(myBoolean);
		builder.append(", myByte=");
		builder.append(myByte);
		builder.append(", myShortUnsigned=");
		builder.append(myShortUnsigned);
		builder.append(", myIntegerUnsigned=");
		builder.append(myIntegerUnsigned);
		builder.append(", myLongUnsigned=");
		builder.append(myLongUnsigned);
		builder.append("]");
		return builder.toString();
	}

	/*@Override
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
		EntityConstant other = (EntityConstant) obj;
		if (myConstant == null) {
			if (other.myConstant != null)
				return false;
		} else if (!myConstant.equals(other.myConstant))
			return false;
		return true;
	}*/
	
	

}
