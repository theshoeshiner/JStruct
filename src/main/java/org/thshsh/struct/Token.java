package org.thshsh.struct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Token {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(Token.class);
	
	//the type
	protected TokenType type;
	//the count of instances of this token
	protected int count;
	//the length of a single instance of the token, for array types
	protected int length;
	//cache calculation of byte count
	protected int byteCount;
	
	public Token(TokenType type, int countOrLength) {
		this(type,type.array?1:countOrLength,type.array?countOrLength:1);
	}

	public Token(TokenType type, int count, int size) {
		super();
		this.type = type;
		if(type.array) {
			this.length = size;
		}
		else this.length = type.size;
		this.count = count;
		if (count == 0)
			throw new IllegalArgumentException("Count cannot be zero");
		byteCount = count * length;
		
	}

	public int tokenCount() {
		return count;
	}
	
	public int length() {
		return length;
	}
	
	public int byteCount() {
		return byteCount;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[type=");
		builder.append(type);
		builder.append(", count=");
		builder.append(count);
		builder.append(", length=");
		builder.append(length);
		builder.append(", byteCount=");
		builder.append(byteCount);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + count;
		result = prime * result + length;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Token other = (Token) obj;
		if (count != other.count)
			return false;
		if (length != other.length)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	

}