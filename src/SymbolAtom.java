/*
 * SymbolAtopm.java 
 * author: Bill Thompson
 * license: GPL 3
 * copyright: 2026-01-27
 *
 * A class for holding symbols.
 *
 */
package LispKit;

public class SymbolAtom extends SExp {
	private final String _string;
	
	public SymbolAtom( String s ) {
		_type = SYMBOL;
		_string = s;
	}

	// NIL
	public SymbolAtom() {
		_type = SYMBOL;
		_string = "NIL";
	}
	
	public String GetSymbol() {
		return _string;
	}
}
