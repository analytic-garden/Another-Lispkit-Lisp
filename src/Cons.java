/*
 * Cons.java
 * author: Bill Thompson
 * license: GPL 3
 * copyright: 2026-01-27
 *
 * A class for cons cells.
 * Cons are the glue holding Lisp functions together.
 *
 */
package LispKit;

public class Cons extends SExp {
	
	private SExp _car;
	private SExp _cdr;
	
	public Cons( SExp aCar, SExp aCdr ) {
		_type = CONS;
		_car = aCar;
		_cdr = aCdr;
	}
	
    @Override
	public SExp car() {
		return _car;
	}
	
    @Override
	public SExp cdr() {
		return _cdr;
	}
	
	public void SetCar( SExp aCar ) {
		_car = aCar;
	}
	
	public void SetCdr( SExp aCdr ) {
		_cdr = aCdr;
	}
}
