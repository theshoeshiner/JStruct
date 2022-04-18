# JStruct
The python struct library's port to java for reading and writing binary data as in python. Based off the code found here: https://github.com/ronniebasak/JStruct with non backwards compatible changes to support strong typing, strings, and byte arrays.

## Classes
The Struct class contains static methods for constructing a reuseable Struct object from a format string

## USAGE:
To use this, either add the maven dependency:

```
	<dependency>
		<groupId>org.thshsh</groupId>
		<artifactId>struct</artifactId>
		<version>1.0.0</version>
	</dependency>
```

This library makes all attempts to correctly type the arguments, but some arguments must be up-cast to allow them to correctly represent unsigned decimal values

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
  
Unlike python, a count can be specified for *any* token type. For the array and String types ('s' and 'S') the count specified is the number of items in the array or the number of characters in the string. For all other token types the count specifies the *number of times the next token is repeated*. This is for conciseness only. e.g. the following two patterns are functionally the same:

```
4i4q4d
iiiiqqqqdddd

```

## Flaws

Because java cannot represent an unsigned 8 byte integer with a primitive, the unsigned long type ('Q') returns a signed Long. It should be noted that *the underlying bits are correct* , so the 2's compliment decimal value will not match the unsigned decimal value. If you need the decimcal value you can use the Java 8 function Long.toUnsignedString(n) to convert and store in a BigInteger:

```
BigInteger bi = new BigInteger(Long.toUnsignedString(n));
```
