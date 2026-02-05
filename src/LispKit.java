/*
 * Lispkit.java
 *
 * author: Bill Thompson
 * license: GPL 3
 * copyright: 2026-01-27
 *
 * An SECD virtual machine for a functional version of Lisp
 * Based on Peter Henerson's book
 * Functional Programming Application and Implementation
 * ISBN 0-13-331579-7 
 *
 *  I don't implement garbage collection. Java takes care of that.
 */
package LispKit;

import java.io.*;

public class LispKit {
	/**
	 * Main class for the Lispkit System
	 */

	// compiler.secd is a compiled Lisp compiler for SECD from Henderson's book
	// the compiler.secd file is from https://github.com/carld/lispkit
	private static final String COMPILER_FILE = "compiler.secd";

	public static void main(String[] args) throws FileNotFoundException, IOException {
		String cwd = GetCwd();
	
		/**
		 * Load the compiler
		 * 
		 * Compiler file should be in same directory Lispkit class is run from
		 */
		SExp comp;
		try (FileInputStream f = new FileInputStream( cwd + COMPILER_FILE )) {
			SExpReader compReader = new SExpReader( f );
			comp = compReader.GetExp();
		}

		/**
		 * Read the Lisp function
		 * 
		 * If an arg is passed on commandline, it contains the Lisp function,
		 * otherwise read the function from the terminal.
		 */
		SExpReader s;
		if( args.length > 0 ) {
			try (FileInputStream f2 = new FileInputStream( args[0] )) {
				s = new SExpReader( f2 );
			}
		} 
		else {
			s = new SExpReader();
		}
		SExp fn = s.GetExp();

		// display the function
		SExpWriter w = new SExpWriter();
		w.PutSExp( fn );
		w.ForceLineOut(); 
		
		// compile the Lisp function
		SECD secd = new SECD();
		SExp fn2 = new Cons( fn, new SymbolAtom() );
		SExp compFn = secd.exec( comp, fn2 );

		// print the compled function
		w.PutSExp( compFn );
		w.ForceLineOut(); 

		/**
		 * Read the function arguments
		 * 
		 * If a second arg is passed on commandline, it contains the arguments for the Lisp function,
		 * otherwise read the arguments from the terminal.
		 */
		
		SExpReader s2;
		if( args.length > 1 ) {
			try (FileInputStream f3 = new FileInputStream( args[1] )) {
				s2 = new SExpReader( f3 );
			}
		} 
		else {
			s2 = new SExpReader();
		}
		// SExp fnArgs = s2.GetExpList();
		SExp fnArgs = s2.GetExp();
	
		// display the function arguments
		w.PutSExp( fnArgs );
		w.ForceLineOut(); 

		// execute the function
		secd = new SECD();
		SExp result = secd.exec( compFn, fnArgs );

		// disply results
		w.PutSExp( result );
		w.ForceLineOut(); 		
	}
	
	private static String GetCwd() {
		String cwd = System.getProperty( "user.dir" );
		String dir;
		
		if( ! cwd.endsWith( "/" ) )
			dir = cwd + "/";
		else
			dir = cwd;	
		
		return dir;
	}
}
