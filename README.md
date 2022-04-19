# JStruct
The python struct library's port to java for reading and writing binary data as in python. Based off the code found here: https://github.com/ronniebasak/JStruct with non backwards compatible changes to support strong typing, strings, and byte arrays.

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

And the code can be used as follows...

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
  * ``h`` : Signed Short (2 bytes) - java.lang.Short
  * ``H`` : Unsigned Short (2 bytes) - java.lang.Integer
  * ``i`` : Signed Integer (4 bytes) - java.lang.Integer
  * ``l`` : Signed Integer (4 bytes) - java.lang.Integer (same as ''i'')
  * ``I`` : Unsigned Integer (4 bytes) - java.lang.Long
  * ``q`` : Signed Long (8 bytes) - java.lang.Long
  * ``Q`` : Unsigned Long (8 bytes) - java.lang.Long
  * ``d`` : Floating Point Double (8 bytes) - java.lang.Double
  * ``s`` : Byte Array - java.lang.Byte[]
  * ``S`` : String - java.lang.String
  
Unlike python, a integer prefix can be present on *any* token. For the byte array and String types ('s' and 'S') the number specified is the count of items in the array or the count of characters in the string. For all other token types the count specifies the *number of times the next token is repeated*. This allows format strings to be more concise and readable e.g. the following two patterns are functionally the same:

```
4i4q4d4s4S
iiiiqqqqdddd4s4S

```

## Flaws

Because java cannot represent an unsigned 8 byte integer with a primitive, the unsigned long type ('Q') returns a signed Long. It should be noted that *the underlying bits are correct* , so the 2's compliment decimal value will not match the unsigned decimal value. If you need the decimcal value you can use the Java 8 function Long.toUnsignedString(n) to convert and store in a BigInteger:

```
BigInteger bi = new BigInteger(Long.toUnsignedString(n));
```
