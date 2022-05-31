# JStruct
The python struct library's port to java for reading and writing binary data as in python. Based off the code found here: https://github.com/ronniebasak/JStruct with non backwards compatible changes to support strong typing, strings, and byte arrays.

In addition there are now annotations to map Java POJOs to struct tokens. Once mapped the POJOs can be packed and unpacked without having to manually create the Struct format.

## Classes
The Struct class contains static methods for constructing a reuseable Struct object from a format string

## USAGE:
To use this, add the maven dependency:

```
	<dependency>
		<groupId>org.thshsh</groupId>
		<artifactId>struct</artifactId>
		<version>1.0.0</version>
	</dependency>
```

Structs can be created from format strings (similar to the python library)...

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

This library makes all attempts to correctly type the output tokens, but some tokens must be up-cast to allow them to correctly represent unsigned decimal values. You must use the types specified below when passing in tokens to be packed.


See the StructTest.java file for more examples.

## Accepted specifiers:
  The first char can be a endianness indicator, the following are available
  * ``@`` : Default endianness
  * ``<`` : Little Endian
  * ``>`` : Big Endian
  * ``!`` : Network byte order, same as >

  The following token specifiers are supported:
  
  | Tokens | Meaning | Bytes | Type |
  | --- | --- | --- | ---|
  | ``h  `` | Signed Short | 2 | java.lang.Short
  | ``H`` | Unsigned Short | 2 | java.lang.Integer
  | ``i|l`` | Signed Integer | 4 | java.lang.Integer
  | ``I`` | Unsigned Integer | 4 | java.lang.Long
  | ``q`` | Signed Long | 8 | java.lang.Long
  | ``Q`` | Unsigned Long | 8 | java.lang.Long
  | ``d`` | Floating Point Double | 8 | java.lang.Double
  | ``s`` | Byte Array | * | java.lang.Byte[]
  | ``c|b`` | Byte | 1 | java.lang.Byte
  | ``S`` | String | * | java.lang.String
  | ``t`` | Boolean | 1 | java.lang.Boolean
  
  
  
Unlike python, a integer prefix can be present on *any* token. For the byte array and String types ('s' and 'S') the number specified is the length of the array / characters in the string. For all other token types it specifies the *number of times the next token is repeated*. This allows format strings to be more concise and readable e.g. the following two patterns are functionally the same:

```
4i4q4d4s4S
iiiiqqqqdddd4s4S

```

## Flaws

Because java cannot represent an unsigned 8 byte integer with a primitive, the unsigned long type ('Q') returns a signed Long. It should be noted that *the underlying bits are correct* , so the 2's compliment decimal value will not match the unsigned decimal value. If you need the decimcal value you can use the Java 8 function Long.toUnsignedString(n) to convert and store in a BigInteger:

```
BigInteger bi = new BigInteger(Long.toUnsignedString(n));
```

##Annotations (beta)

POJO classes can be packed and unpacked using Annotations. Simply annotate each field you want included in the struct with  ``@StructToken``. The core advantage of using the annotations is that you can work with typed objects and named fields instead of a generic ``List<Object>``. The core disadvantage is that you cannot dynamically create the Struct, it is tied to the annotations in the code. You *can* however generate a Struct based on annotations and then *customize* the returned Struct object at runtime. However this will not alter how the annotated entity is packed and unpacked, so most changes to the Struct will make it incompatible with the original entity.

* The ``order`` property is required, because field order is not consistently preserved during compilation. 
* The ``type`` property is optional, and the types listed above are used to detect the proper type. The only exception is the unsigned long type, which must be manually specified (see example below).
* The ``unsigned`` property defaults to false. Signed types will always be selected unless this is set to true.
* The ``length`` property is optional and only valid for ``String`` and ``byte[]`` types. Currently there is no way to map tokens to a List.
* The ``prefix`` and ``suffix`` properties tell the Struct to ignore a certain number of bytes before or after the token.

Example usage:

```
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

The ``@StructEntity`` Annotation can be used to further customize the packing process.