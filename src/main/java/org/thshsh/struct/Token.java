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
	//prefix to ignore
	protected int prefix;
	//suffix to ignore
	protected int suffix;
	//constant value to use
	protected Object constant;
	
	public Token(TokenType type, int countOrLength) {
		this(type,type.array?1:countOrLength,type.array?countOrLength:0,0,0,null);
	}

	public Token(TokenType type, int count, int len, int pre, int suf,Object constant) {
		super();
		this.type = type;
		if(!type.array && len > 0) throw new MappingException("Length cannot be specified for Struct Token type: "+type);
		if(type.array) {
			this.length = len;
		}
		else this.length = type.size;
		this.count = count;
		if (count == 0)
			throw new MappingException("Count cannot be zero");
		this.prefix = pre;
		this.suffix = suf;
		this.constant = constant;
		
		byteCount = count * length + prefix + suffix;
		
	}

	public int tokenCount() {
		return count;
	}
	
	public int length() {
		return length;
	}
	
	public int prefix() {
		return prefix;
	}
	
	public int suffix() {
		return suffix;
	}
	
	public int byteCount() {
		return byteCount;
	}
	
	public boolean isConstant() {
		return constant != null;
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
		builder.append(", prefix=");
		builder.append(prefix);
		builder.append(", suffix=");
		builder.append(suffix);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + count;
		result = prime * result + length;
		result = prime * result + prefix;
		result = prime * result + suffix;
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
		if (prefix != other.prefix)
			return false;
		if (suffix != other.suffix)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	

}