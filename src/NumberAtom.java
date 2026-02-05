/*
 * NumberAtom.java
 * author: Bill Thompson
 * license: GPL 3
 * copyright: 2026-01-27
 *
 * A class for holding integers.
 * This class could be changed to hold floats of BigNums.
 *
 */
package LispKit;

public class NumberAtom extends SExp {
	private final int _number;

	public NumberAtom( int n ) {
		_type = INT;
		_number = n;
	}
	
	public int GetInt() {
		return _number;
	}
}
