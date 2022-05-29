package org.thshsh.struct;

public class MyStructEntityChild extends MyStructEntityParent {
	
	@StructToken(order = 10,length=8)
	public String myChildString;

	public MyStructEntityChild() {
		super();
	}

	public MyStructEntityChild(String myString, Short myShort, Integer myInteger, Long myLong, Double myDouble, byte[] myByteArray, Boolean myBoolean,
			Byte myByte, Integer myShortUnsigned, Long myIntegerUnsigned, Long myLongUnsigned,String child) {
		super(myString, myShort, myInteger, myLong, myDouble, myByteArray, myBoolean, myByte, myShortUnsigned, myIntegerUnsigned, myLongUnsigned);
		this.myChildString = child;
	}

	
	
	public String getMyChildString() {
		return myChildString;
	}

	public void setMyChildString(String myChildString) {
		this.myChildString = myChildString;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((myChildString == null) ? 0 : myChildString.hashCode());
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
		MyStructEntityChild other = (MyStructEntityChild) obj;
		if (myChildString == null) {
			if (other.myChildString != null)
				return false;
		} else if (!myChildString.equals(other.myChildString))
			return false;
		return true;
	}
	
	

}
