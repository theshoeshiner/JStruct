# JStruct
The python struct library's port to java for reading and writing binary data as in python. Based off the code found here: https://github.com/ronniebasak/JStruct with non backwards compatible changes to support strong typing, strings, and byte arrays.

In addition there are now annotations to map Java POJOs to struct tokens. Once mapped the POJOs can be packed and unpacked without having to manually create the Struct format.

## Classes
The Struct class contains static methods for constructing a reuseable Struct object from a format string

## Usage:
To use this, add the maven dependency:

```
	<dependency>
		<groupId>org.thshsh</groupId>
		<artifactId>struct</artifactId>
		<version>1.0.0</version>
	</dependency>
```

Structs can be created via format strings (similar to the python library) or by annotating Java POJOs.

## Format Strings

Format strings can use any of the characters specified [below](#tokens).

```
Struct struct = Struct.create(">2h2i2q2d4s5S");
List<Object> input = Arrays.asList(
		Short.MAX_VALUE,Short.MIN_VALUE,
		Integer.MAX_VALUE,Integer.MIN_VALUE,
		Long.MAX_VALUE,Long.MIN_VALUE,
		Double.MAX_VALUE,Double.MIN_VALUE,
		new byte[] {4,6,2,12},
		"ABCDE"
		);
byte[] output = struct.pack(input);
```

The output array of the above code is:

**in signed decicmal values**

``[127, -1, -128, 0, 127, -1, -1, -1, -128, 0, 0, 0, 127, -1, -1, -1, -1, -1, -1, -1, -128, 0, 0, 0, 0, 0, 0, 0, 127, -17, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 1, 4, 6, 2, 12, 65, 66, 67, 68, 69]``

**in Hex string**

``7fff80007fffffff800000007fffffffffffffff80000000000000007fefffffffffffff00000000000000010406020c4142434445``

This library correctly types the output tokens where possible, but some tokens must be up-cast to allow them to correctly represent unsigned values. You must use the types specified [below](#tokens) when passing in tokens to be packed.


See the StructTest.java file for more examples.


## Annotations

POJO classes can be packed and unpacked using Annotations. Simply annotate each field you want included in the struct with  ``@StructToken``. The advantage of using the annotations is that you can work with typed objects and named fields instead of a generic ``List<Object>``, while the disadvantage is that you cannot dynamically create the Struct, it is tied to the annotations in the code. You *can* however generate a Struct based on annotations and then *customize* the returned Struct object at runtime. However this will not alter how the annotated entity is packed and unpacked, so most changes to the Struct will make it incompatible with the original entity.

Inheritance is supported as long as field ordering and typing is still valid.

``@StructToken`` properties...

* The ``order`` property is required, because field order is not consistently preserved during compilation. Tokens are sorted on this value. Negative values and zero are allowed. These values should be unique.
* The ``type`` property is optional, and the Java types listed [here](#tokens) are used to detect the proper data type. The only exception is the unsigned long type, which must be manually specified because it conflicts with the unsigned Integer type (see example below).
* The ``unsigned`` property defaults to false. Signed types will always be selected unless this is set to true. But remember that unsigned types are represented by the next larger type. e.g. Unsigned Short = Integer, Unsigned Integer = Long.
* The ``length`` property is optional and only valid for ``String`` and ``byte[]`` types.
* The ``prefix`` and ``suffix`` properties tell the Struct to ignore a specified number of empty bytes before or after the token.
* The ``constant`` property allows you specify a constant string to be used for the token. This String will be used regardless of the value of the POJO field, and will be validated during unpacking such that the unpacking *will fail* if the data does not match the constant. Fields for constant tokens do not need to be public. The length property is not required when constant is specified as it will be derived from the constant.

Example usage:

```
	@StructToken(order = -1,constant="MYSTRUCTHEADER")
	protected String alwaysPresentHeader;

	@StructToken(order = 0,length=3)
	public String myString;
	
	@StructToken(order = 2)
	public Short myShort;
	
	@StructToken(order = 1)
	public Integer myInteger;
	
	@StructToken(order = 3)
	public Long myLong;
	
	@StructToken(order = 4)
	public Double myDouble;
	
	@StructToken(order = 5,length=4)
	public byte[] myByteArray;
	
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
```

The ``@StructEntity`` Annotation can be used to further customize the packing process. It has 5 properties which can be specified...

* The ``byteOrder`` property is used to specify the Byte Order used for packing the entity.
* The ``charset`` property is used to specify the name of the Charset to be used for unpacking Strings.
* The ``trimAndPad`` property can be used to enable trimming and padding of byte[] and String tokens.
* The ``prefix`` and ``suffix`` properties are used to configure a number of empty bytes at the beginning and end of the struct.


## Tokens

  The first char can be a endianness indicator, the following are available
  * ``@`` : Default endianness
  * ``<`` : Little Endian
  * ``>`` : Big Endian
  * ``!`` : Network byte order, same as >

  The following token specifiers are supported:
  
  | Data | Character | Enum | Bytes | Java Type |
  | --- | --- | --- | --- | --- |
  | Signed Short | ``h  `` | TokenType.Short | 2 | java.lang.Short
  | Unsigned Short | ``H`` | TokenType.ShortUnsigned | 2 | java.lang.Integer
  | Signed Integer | ``i\|l`` | TokenType.Integer | 4 | java.lang.Integer
  | Unsigned Integer | ``I`` | TokenType.IntegerUnsigned | 4 | java.lang.Long
  |  Signed Long | ``q`` | TokenType.Long | 8 | java.lang.Long
  | Unsigned Long | ``Q`` | TokenType.LongUnsigned | 8 | java.lang.Long
  | Floating Point Double | ``d`` | TokenType.Double | 8 | java.lang.Double
  |  Byte Array | ``s`` | TokenType.Bytes | * | java.lang.Byte[]
  | Byte | ``c\|b`` | TokenType.Byte | 1 | java.lang.Byte
  | String | ``S`` | TokenType.IntegerString | * | java.lang.String
  | Boolean | ``t`` | TokenType.Boolean | 1 | java.lang.Boolean
  
  
  
Unlike python, a integer prefix can be present on *any* token. For the byte array and String types ('s' and 'S') the number specified is the length of the array / characters in the string. For all other token types it specifies the *number of times the next token is repeated*. This allows format strings to be more concise and readable e.g. the following two patterns are functionally the same:

```
4i4q4d4s4S
iiiiqqqqdddd4s4S

```

## Flaws

Because java cannot represent an unsigned 8 byte integer with a primitive, the unsigned long type ('Q') returns a signed Long.  *The underlying bits are correct* , so the 2's compliment decimal value will not match the desired decimal value. If you need the unsigned value you can use the Java 8 function Long.toUnsignedString(n) to convert and store in a BigInteger:

```
BigInteger bi = new BigInteger(Long.toUnsignedString(n));
```
