package org.thshsh.struct;

import java.util.Arrays;

@StructEntity(pad = true)
public class MyEntityA {
	
	@StructToken(order = 0,length = 2)
	public String myString;
	@StructToken(order = 1)
	public Long myLong;
	@StructToken(order = 2)
	public 	Short myShort;
	@StructToken(order = 3)
	public 	Double myDouble;
	@StructToken(order = 4,length = 3)
	public 	byte[] myByteArray;
	
	public MyEntityA() {}
	
	public MyEntityA(String myString, Long myLong, Short myShort, Double myDouble, byte[] myByteArray) {
		super();
		this.myString = myString;
		this.myLong = myLong;
		this.myShort = myShort;
		this.myDouble = myDouble;
		this.myByteArray = myByteArray;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(myByteArray);
		result = prime * result + ((myDouble == null) ? 0 : myDouble.hashCode());
		result = prime * result + ((myLong == null) ? 0 : myLong.hashCode());
		result = prime * result + ((myShort == null) ? 0 : myShort.hashCode());
		result = prime * result + ((myString == null) ? 0 : myString.hashCode());
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
		MyEntityA other = (MyEntityA) obj;
		if (!Arrays.equals(myByteArray, other.myByteArray))
			return false;
		if (myDouble == null) {
			if (other.myDouble != null)
				return false;
		} else if (!myDouble.equals(other.myDouble))
			return false;
		if (myLong == null) {
			if (other.myLong != null)
				return false;
		} else if (!myLong.equals(other.myLong))
			return false;
		if (myShort == null) {
			if (other.myShort != null)
				return false;
		} else if (!myShort.equals(other.myShort))
			return false;
		if (myString == null) {
			if (other.myString != null)
				return false;
		} else if (!myString.equals(other.myString))
			return false;
		return true;
	}

	
	
}
