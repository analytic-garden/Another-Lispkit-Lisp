/*
 * SExpReader.java
 *
 * author: Bill Thompson
 * license: GPL 3
 * copyright: 2026-01-27
 *
 * A class for reading S expressions
 * Based on the method in Peter Henerson's book
 * This could be made simpler, but I decided to mimic Henderson's method.
 * 
 * The various reader constructors read an s expression into a string buffer.
 */
package LispKit;

import java.io.*;

public class SExpReader extends Object {	
	// token types
	private static final int EOF =-1;
	private static final int NUMERIC = 1;
	private static final int ALPHA = 2;
	private static final int DELIMITER = 3;
	
	private final StringBuffer buffer = new StringBuffer();
	private char currentChar = ' ';
	private int inBufPtr = 0;
	private int inBufEnd = -1;
	private final StringBuffer token = new StringBuffer();
	private int tokenType;

	// GetChar gets the first character.
	// Scan completes reading a single token and gets its type.

	public SExpReader() throws IOException {
		GetChar();
		Scan();
	}
	
	public SExpReader( String s ) throws IOException {
		buffer.insert( 0, s );
		buffer.append( (char) EOF );
		inBufEnd = s.length();
		inBufPtr = 0;	

		GetChar();
		Scan();
	}
	
	public SExpReader( FileInputStream f  ) throws IOException {
		int inByte;
		while( (inByte = f.read() ) != -1 ) {
			buffer.append( (char) inByte );
		}
		
		buffer.append( (char) EOF );
		inBufEnd = buffer.length() - 1;
		inBufPtr = 0;	

		GetChar();
		Scan();
	}
	
	// return a complete S exprssion and scan ahead
	public  SExp GetExp() throws IOException {
		if( token.toString().equals( "(" ) ) {
			Scan();
			SExp e = GetExpList();
			Scan();
			return e;
		}
		else if( tokenType == NUMERIC ) {
			int n = Integer.parseInt( token.toString() );
			SExp e = new NumberAtom( n );
			Scan();
			return e;
		}
		else {
			SExp e = new SymbolAtom( token.toString() );
			Scan();
			return e;
		}	
	}
	
	// return a SExp list.
	public  SExp GetExpList() throws IOException {
		SExp p = GetExp();
            switch (token.toString()) {
                case "." -> {    // dotted pair
                    Scan();
                    SExp p2 = GetExp();
                    SExp e = new Cons( p, p2 );
                    return e;
                }
                case ")" -> {   // attach NIL at the end of the list
                    SExp e = new Cons( p, new SymbolAtom() );
                    return e;
                }
                default -> {
                    SExp p2 = GetExpList();
                    SExp e = new Cons( p, p2);
                    return e;
                }
            }
	}
	
	// get the current character from the buffer and move the buffer pointer.
	private  void GetChar() throws IOException {
		final String promptStr = "> ";

		// this part is mainly for reading from the terminal.
		if( inBufPtr > inBufEnd ) {	
			BufferedReader in = new BufferedReader( new InputStreamReader( System.in ));
			System.out.print( promptStr );
			String text;

			try {
				text = in.readLine();
			} 
			catch( IOException e  ) {
				currentChar = (char) EOF;
				text = "";
			}

			buffer.setLength( 0 );
			if( text == null || text.length() == 0 ) {
				inBufEnd = -1;
			}
			else {
				buffer.append( text ).append( " ");
				inBufEnd = text.length();
			}
			inBufPtr = 0;
		}
		
		if( inBufEnd == -1 )
			currentChar = (char) EOF;
		else	
			currentChar = buffer.charAt( inBufPtr );
		inBufPtr++;
	}
	
	// get the current token and its type
	private  void GetToken() throws IOException {
		token.setLength( 0 );
		tokenType = ALPHA;
		
		while( currentChar != (char) EOF && Character.isWhitespace( currentChar ) ) {
			GetChar();
		}
		
		if( currentChar == (char) EOF )
			tokenType = EOF;
		else if( Character.isDigit( currentChar ) || currentChar == '-' ) {
			tokenType = NUMERIC;
			token.append( currentChar );
			GetChar();
			while( Character.isDigit( currentChar ) ) {
				token.append( currentChar );
				GetChar();
			}
		}
		else if( Character.isLetter( currentChar ) ) {
			tokenType = ALPHA;
			token.append( currentChar );
			GetChar();
			while( Character.isLetter( currentChar ) || Character.isDigit( currentChar ) ) {
				token.append( currentChar );
				GetChar();
			}
		}
		else {
			tokenType = DELIMITER;
			token.append( currentChar );
			GetChar();
		}
	}
	
	// get the next token
	// add a ) if the buffer runs out.
	private   void Scan() throws IOException {
		GetToken();
		if( tokenType == EOF )
			token.replace(0, token.length(), ")");
	}	
}
