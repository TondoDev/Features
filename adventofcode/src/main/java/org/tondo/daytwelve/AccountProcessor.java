package org.tondo.daytwelve;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AccountProcessor {
	
	private static enum PropParts {
		START,
		NAME,
		DELIM,
		VALUE,
		DONE
	}
	
	private static enum ArrState {
		START,
		DELIMETED,
		STANDARD,
		STRING,
	}
	
	
	public long getBalance(InputStream is) throws IOException {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		int c = -1;
		
		// skipping initial whitespaces
		do {
			c = reader.read();
		} while (Character.isWhitespace((char)c));
		
		long sum = 0L;
		if (c == '[') {
			sum = this.getFromArray(false, reader);
		} else if (c == '{') {
			sum = getFromObject(false, reader);
		} else {
			throw new IllegalStateException("Syntax error!");
		}
		
		do {
			c = reader.read();
		} while (Character.isWhitespace((char)c));
		
		// some garbage present after closed root object
		if (c != -1) {
			throw new IllegalStateException("Syntax error!");
		}
		return sum;
	}
	
	private long getFromObject(boolean ignore, BufferedReader reader) throws IOException {
		boolean effIgn = ignore;
		long sum = 0L;
		PropParts state = PropParts.START;
		boolean closed = false;
		int c = -1;
		StringBuilder token = null;
		while ((c = reader.read()) != -1) {
			if (c == '"') {
				if(state == PropParts.DONE || state == PropParts.START) {
					state = PropParts.NAME;
				}
				
				if (state == PropParts.NAME) {
					parseString(reader);
					state = PropParts.DELIM;
				} else if (state == PropParts.VALUE) {
					String str = parseString(reader);
					if (!effIgn) { // when ignoring flag is set it cant be reset
						effIgn = "red".equals(str);
					}
					state = PropParts.DONE;
				}
			} else if (c == '[') {
				if (state != PropParts.VALUE) {
					throw new IllegalStateException("Syntax error!");
				}
				sum += getFromArray(effIgn, reader);
			} else if (c == '{') {
				if (state == PropParts.START) {
					throw new IllegalStateException("Syntax error!");
				}
				sum += getFromObject(effIgn, reader);
			} else if (c == '}') {
				if (state == PropParts.DELIM) {
					throw new IllegalStateException("Syntax error!");
				}
				closed = true;
				sum += tokenToSum(token);
				token = null;
				break;
			} else if (c == ']') {
				throw new IllegalStateException("Syntax error!");
			} else  if (c == ':' && state == PropParts.DELIM) {
				state = PropParts.VALUE;
			} else if (c == ',' && (state == PropParts.DONE || state == PropParts.VALUE)) {
				sum += tokenToSum(token);
				token = null;
				state = PropParts.NAME;
			} else if (!Character.isWhitespace((char)c) ) {
				if (state == PropParts.VALUE) {
					token = tokenKeeper(token).append((char) c);
				}
			}
		}
		
		if (!closed) {
			throw new IllegalStateException("Syntax error!");
		}
		
		return effIgn ? 0L : sum;
	}
	
	private long getFromArray(boolean ignore, BufferedReader reader) throws IOException {
		
		long sum = 0L;
		boolean closed = false;
		int c = -1;
		ArrState state = ArrState.START;
		StringBuilder token = null;
		while ((c = reader.read()) != -1) {
			if (c == '[') {
				if(state != ArrState.START && state != ArrState.DELIMETED) {
					throw new IllegalStateException("Syntax error!");
				}
				state = ArrState.STANDARD;
				long num =  getFromArray(ignore, reader);
				sum += ignore ? 0L : num;
			} else if (c == ']') {
				if (state == ArrState.DELIMETED) {
					throw new IllegalStateException("Syntax error!");
				}
				closed = true;
				long num = tokenToSum(token);
				sum += ignore ? 0L : num;
				token = null;
				break;
			} else if ( c == '{') {
				if(state != ArrState.START && state != ArrState.DELIMETED) {
					throw new IllegalStateException("Syntax error!");
				}
				state = ArrState.STANDARD;
				long num =  getFromObject(ignore, reader);
				sum += ignore ? 0L : num;
			} else if (c == '"') {
				if(state != ArrState.START && state != ArrState.DELIMETED) {
					throw new IllegalStateException("Syntax error!");
				}
				state = ArrState.STRING;
				parseString(reader);
			} else if ( c == ',') {
				if (state == ArrState.START) {
					throw new IllegalStateException("Syntax error!");
				}
				state = ArrState.DELIMETED;
				sum += tokenToSum(token);
				token = null;
			} else if (!Character.isWhitespace((char)c)) {
				if(state == ArrState.STRING) {
					throw new IllegalStateException("Syntax error! " + state);
				}
				state = ArrState.STANDARD;
				token = tokenKeeper(token).append((char) c);
			}
		}
		
		if (!closed) {
			throw new IllegalStateException("Syntax error!");
		}
		
		return sum;
	}
	
	
	private String parseString(BufferedReader reader) throws IOException {
		
		int c = -1;
		StringBuilder sb = new StringBuilder();
		boolean closed = false;
		while ((c = reader.read()) != -1) {
			// expecting that no escaping is used
			if (c == '"') {
				closed = true;
				break;
			}
			
			sb.append((char)c);
		}
		
		if (!closed) {
			throw new IllegalStateException("Syntax error!");
		}
		return sb.toString();
	}
	
	private long tokenToSum(StringBuilder sb) {
		if (sb == null) {
			return 0L;
		}
		
		String str = sb.toString();
		return Long.parseLong(str.trim());
	}
	
	private StringBuilder tokenKeeper(StringBuilder sb) {
		if (sb == null) {
			return new StringBuilder();
		}
		
		return sb;
	}

}
