/*
 * SExp.java
 *
 * author: Bill Thompson
 * license: GPL 3
 * copyright: 2026-01-27
 *
 * A class for S expressions.
 * 
 * Cons, SymbolAtom, and NumberAtom are subclasses.
 *
 */
package LispKit;

public class SExp {
    protected static final int EMPTY = -1;
	protected static final int CONS = 0;
	protected static final int INT = 1;
	protected static final int SYMBOL = 2;
	
	protected int _type;
	
	public SExp() {
		_type = EMPTY;
	}
	
	public boolean isNumber() {
		 return (_type == INT);
	 }
    
	 public boolean isSymbol() {
		 return (_type == SYMBOL );
	 }
    
	 public boolean isCons() {
		 return (_type == CONS);
	 }
    
	 public boolean isEmpty() {
		 return (_type == EMPTY);
	 }
	 
	 // NIL is a SymbolAtom
	 // NIL should be made a singleton.
	 public boolean isNIL() {
	 	if( _type == SYMBOL ) 
	 		return ((SymbolAtom) this).GetSymbol().equals( "NIL");
	 	else
	 		return false;	
	 }

	 // added car and cdr functions, so that calling them on a non-cons, returs NIL 
	 public SExp car() {
		return new SymbolAtom();
	 }

	  public SExp cdr() {
		return new SymbolAtom();
	 }
}
	