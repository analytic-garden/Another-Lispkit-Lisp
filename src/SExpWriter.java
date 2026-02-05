/*
 * SExpWriter.java
 *
 * author: Bill Thompson
 * license: GPL 3
 * copyright: 2026-01-27
 *
 * A class for outputting S expressions
 */

package LispKit;

import java.io.*;

public class SExpWriter extends Object {
	
	private static final int BUF_LEN = 60;
	
	private final StringBuffer buffer = new StringBuffer();
	private int outBufPtr = -1;
	
	// write an S expression
	public  void PutSExp( SExp s ) throws IOException {
		if( s.isNumber() ) {
			NumberAtom s2 = (NumberAtom) s;
			int n = s2.GetInt();
			PutToken( n + "" );
		}
		else if( s.isSymbol() ) {
			SymbolAtom s2 = (SymbolAtom) s;
			PutToken( s2.GetSymbol() );
		}
		else if( s.isNIL() || s.isEmpty() ) {
			PutChar( ' ');
		} 
		else {
			PutChar( '(' );
			PutChar( ' ' );
			SExp s2 = s;
			while( (! s2.isNIL() ) && s2.isCons() ) {
				Cons s3 = (Cons) s2;
				PutSExp( s3.car() );
				s2 = s3.cdr();
			}
			if( ! s2.isNIL() ) {
				PutToken( "." );
				PutSExp( s2 );
			}
			PutToken( ")" );
		}
	}

	public  void PutToken( String str ) throws IOException {
		for( int i = 0; i < str.length(); i++) {
			PutChar( str.charAt( i ) );
		}
		PutChar( ' ');
	}
	
	private  void PutChar( char ch ) throws IOException {
		if( outBufPtr == BUF_LEN ) 
			ForceLineOut();
		outBufPtr++;
		buffer.append( ch );	
	}
	
	// write the buffer to stdout
	public  void ForceLineOut() throws IOException {
		System.out.println( buffer );
		buffer.setLength( 0 );
		outBufPtr = -1;
	}
}
