package org.thshsh.struct;

import java.util.Arrays;

@StructEntity(byteOrder = ByteOrder.Big,charset = "UTF-8",trimAndPad = true)
public class MyStructEntityWithAnn {

	@StructToken(order = 0,length=3)
	public String myString;
	@StructToken(order = 2)
	public 	Short myShort;
	@StructToken(order = 1)
	public Integer myInteger;
	@StructToken(order = 3)
	public Long myLong;
	@StructToken(order = 4)
	public 	Double myDouble;
	@StructToken(order = 5,length=4)
	public 	byte[] myByteArray;
	@StructToken(order = 6)
	public Boolean myBoolean;
	@StructToken(order = 7)
	public Byte myByte;
	@StructToken(order = 8,unsigned = true)
	public Integer myShortUnsigned;
	@StructToken(order = 9,unsigned = true)
	public Long myIntegerUnsigned;
	@StructToken(type=TokenType.LongUnsigned, order = 10)
	public Long myLongUnsigned;
	
	
	public MyStructEntityWithAnn() {}
	
	public MyStructEntityWithAnn(String myString, Short myShort, Integer myInteger, Long myLong, Double myDouble, byte[] myByteArray,
			Boolean myBoolean, Byte myByte, Integer myShortUnsigned, Long myIntegerUnsigned, Long myLongUnsigned) {
		super();
		this.myString = myString;
		this.myShort = myShort;
		this.myInteger = myInteger;
		this.myLong = myLong;
		this.myDouble = myDouble;
		this.myByteArray = myByteArray;
		this.myBoolean = myBoolean;
		this.myByte = myByte;
		this.myShortUnsigned = myShortUnsigned;
		this.myIntegerUnsigned = myIntegerUnsigned;
		this.myLongUnsigned = myLongUnsigned;
	}
	
	
	
	public String getMyString() {
		return myString;
	}

	public void setMyString(String myString) {
		this.myString = myString;
	}

	public Short getMyShort() {
		return myShort;
	}

	public void setMyShort(Short myShort) {
		this.myShort = myShort;
	}

	public Integer getMyInteger() {
		return myInteger;
	}

	public void setMyInteger(Integer myInteger) {
		this.myInteger = myInteger;
	}

	public Long getMyLong() {
		return myLong;
	}

	public void setMyLong(Long myLong) {
		this.myLong = myLong;
	}

	public Double getMyDouble() {
		return myDouble;
	}

	public void setMyDouble(Double myDouble) {
		this.myDouble = myDouble;
	}

	public byte[] getMyByteArray() {
		return myByteArray;
	}

	public void setMyByteArray(byte[] myByteArray) {
		this.myByteArray = myByteArray;
	}

	public Boolean getMyBoolean() {
		return myBoolean;
	}

	public void setMyBoolean(Boolean myBoolean) {
		this.myBoolean = myBoolean;
	}

	public Byte getMyByte() {
		return myByte;
	}

	public void setMyByte(Byte myByte) {
		this.myByte = myByte;
	}

	public Integer getMyShortUnsigned() {
		return myShortUnsigned;
	}

	public void setMyShortUnsigned(Integer myShortUnsigned) {
		this.myShortUnsigned = myShortUnsigned;
	}

	public Long getMyIntegerUnsigned() {
		return myIntegerUnsigned;
	}

	public void setMyIntegerUnsigned(Long myIntegerUnsigned) {
		this.myIntegerUnsigned = myIntegerUnsigned;
	}

	public Long getMyLongUnsigned() {
		return myLongUnsigned;
	}

	public void setMyLongUnsigned(Long myLongUnsigned) {
		this.myLongUnsigned = myLongUnsigned;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((myBoolean == null) ? 0 : myBoolean.hashCode());
		result = prime * result + ((myByte == null) ? 0 : myByte.hashCode());
		result = prime * result + Arrays.hashCode(myByteArray);
		result = prime * result + ((myDouble == null) ? 0 : myDouble.hashCode());
		result = prime * result + ((myInteger == null) ? 0 : myInteger.hashCode());
		result = prime * result + ((myIntegerUnsigned == null) ? 0 : myIntegerUnsigned.hashCode());
		result = prime * result + ((myLong == null) ? 0 : myLong.hashCode());
		result = prime * result + ((myLongUnsigned == null) ? 0 : myLongUnsigned.hashCode());
		result = prime * result + ((myShort == null) ? 0 : myShort.hashCode());
		result = prime * result + ((myShortUnsigned == null) ? 0 : myShortUnsigned.hashCode());
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
		MyStructEntityWithAnn other = (MyStructEntityWithAnn) obj;
		if (myBoolean == null) {
			if (other.myBoolean != null)
				return false;
		} else if (!myBoolean.equals(other.myBoolean))
			return false;
		if (myByte == null) {
			if (other.myByte != null)
				return false;
		} else if (!myByte.equals(other.myByte))
			return false;
		if (!Arrays.equals(myByteArray, other.myByteArray))
			return false;
		if (myDouble == null) {
			if (other.myDouble != null)
				return false;
		} else if (!myDouble.equals(other.myDouble))
			return false;
		if (myInteger == null) {
			if (other.myInteger != null)
				return false;
		} else if (!myInteger.equals(other.myInteger))
			return false;
		if (myIntegerUnsigned == null) {
			if (other.myIntegerUnsigned != null)
				return false;
		} else if (!myIntegerUnsigned.equals(other.myIntegerUnsigned))
			return false;
		if (myLong == null) {
			if (other.myLong != null)
				return false;
		} else if (!myLong.equals(other.myLong))
			return false;
		if (myLongUnsigned == null) {
			if (other.myLongUnsigned != null)
				return false;
		} else if (!myLongUnsigned.equals(other.myLongUnsigned))
			return false;
		if (myShort == null) {
			if (other.myShort != null)
				return false;
		} else if (!myShort.equals(other.myShort))
			return false;
		if (myShortUnsigned == null) {
			if (other.myShortUnsigned != null)
				return false;
		} else if (!myShortUnsigned.equals(other.myShortUnsigned))
			return false;
		if (myString == null) {
			if (other.myString != null)
				return false;
		} else if (!myString.equals(other.myString))
			return false;
		return true;
	}
	
	
	
}
