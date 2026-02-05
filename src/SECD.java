/*
 * SECD.java
 *
 * author: Bill Thompson
 * license: GPL 3
 * copyright: 2026-01-27
 *
 * The SECD virtual machine
 * 
 * The code for each operation follows the description in Henderon's book 
 *
 */

package LispKit;

import java.io.IOException;

public class SECD {
	private final SymbolAtom t = new SymbolAtom( "t" );
	private final SymbolAtom f = new SymbolAtom( "f" );
	private final SExp nil = new SymbolAtom();

	private SExp s;				// stack
	private SExp e = nil;		// environment
	private SExp c;				// code
	private SExp d = nil;		// dump

	private void print_registers() throws IOException {
		SExpWriter sw = new SExpWriter();

		System.out.println( "s:" );
		sw.PutSExp(s);
		sw.ForceLineOut(); 

		System.out.println( "e:" );
		sw.PutSExp( e );
		sw.ForceLineOut(); 

		System.out.println( "c:" );
		sw.PutSExp(c);
		sw.ForceLineOut(); 

		System.out.println( "d:" );
		sw.PutSExp(d);
		sw.ForceLineOut(); 
	}

	public SExp exec( SExp fn, SExp args ) throws IOException{
		return exec( fn, args, false);
	}
	
	public SExp exec( SExp fn, SExp args, boolean dump_regs ) throws IOException {
		s = new Cons( args, nil );
		c = fn;
				
		boolean done = false;
		while( ! done ) {
			int op = GetOp();

			if ( dump_regs ) {
				System.out.println( "op: " + op );
				print_registers();
			}

			// these could be moved into separate functions
			// I don't like all of the cast in these functions.
			switch( op ) {
				case 1 -> {		// LD
					SExp w = (Cons) e;
					
					Cons c2 = (Cons) c;
					Cons c3 = (Cons) c2.cdr();   // cdr(c)
					Cons c4 = (Cons) c3.car();   // car(cdr(c))
					NumberAtom np = (NumberAtom) c4.car();  // car(car(cdr(c)))
					int n = np.GetInt();
					for( int i = 1; i <= n; i++ ) {
						// w = (Cons) w.cdr();
						w = w.cdr();
					}
					// w = (Cons) w.car();
					w = w.car();					
					np = (NumberAtom) c4.cdr();  // cdr(car(cdr(c)))
					n = np.GetInt();
					for( int i = 1; i <= n; i++ ) {
						// w = (Cons) w.cdr();
						w = w.cdr();
					}
					s = new Cons( w.car(), s );
					c = c3.cdr();    // cdr(cdr(c))		
				}
				
				case 2 -> {  // LDC 
					Cons c2 = (Cons) c;
					Cons c3 = (Cons) c2.cdr();  // cdr(c)
					SExp c4 = c3.car();         // car(cdr(c))
					s = new Cons( c4, s);       // cons(car(cdr(c)), s)
					c = c3.cdr();               // c =  cdr(cdr(c))
				}
				
				case 3 -> {   // LDF
					Cons c2 = (Cons) c;
					Cons c3 = (Cons) c2.cdr();   // cdr(c)
					SExp c4 = c3.car();   // car(cdr(c))
					SExp c5 = (SExp) new Cons( c4, e);
					s = new Cons( c5, s);
					c = c3.cdr();  // cdr(cdr(c))
				}
				
				case 4 -> {   // AP
					Cons s2 = (Cons) s;
					Cons s3 = (Cons) s2.cdr();  // cdr(s)
					SExp s4 = s3.cdr();  // cdr(cdr(s))
					Cons c2 = (Cons) c;
					SExp c3 = c2.cdr();  // cdr(c)
					SExp cn1 = new Cons( c3, d );  // cons(cdr(c), d)
					SExp cn2 = new Cons( e, cn1 ); // cons(e, cons(cdr(c), d))
					d = new Cons( s4, cn2 );       // cons(cdr(s), cons(e, cons(cdr(c), d)))

					SExp s5 = s3.car();  // car(cdr(s))

					SExp s6 =  s2.car();  // car(s)
					SExp s7 = s6.cdr();   // cdr(car(s))
					e = new Cons( s5, s7);    // cons(car(cdr(s)), cdr(car(s)))
					c = s6.car();    // car(car(s))

					s = nil;
				}
				
				case 5 -> {   // RTN
					Cons s2 = (Cons) s;
					SExp s3 = s2.car();  // car(s);
					Cons d2 = (Cons) d;
					SExp d3 = d2.car();  // car(d);
					s = new Cons( s3, d3 );
					Cons d4 = (Cons) d2.cdr();  // cdr(d);
					e = d4.car();  // car(cdr(d))
					Cons d5 = (Cons) d4.cdr();  // cdr(cdr(d))
					c = d5.car();   // car(cdr(cdr(d)))
					d = d5.cdr();  // cdr(cdr(cdr(d)))
				}
				
				case 6 -> {   // DUM
					e = new Cons( nil, e);
					Cons c2 = (Cons) c;
					c = c2.cdr();	// cdr(c)
				}
				
				case 7 -> {   // RAP
					Cons c2 = (Cons) c;
					Cons c3 = (Cons) c2.cdr();   // cdr(c)
					SExp c4 = new Cons( c3, d ); 
					Cons e2 = (Cons) e;
					SExp e3 = e2.cdr();   // cdr(e)
					SExp c5 = new Cons( e3, c4 );
					Cons s2 = (Cons) s;
					Cons s3 = (Cons) s2.cdr();   // cdr(s)
					SExp s4 = s3.cdr();   // cdr(cdr(s))
					d = new Cons( s4, c5 );
					Cons s5 = (Cons) s2.car();   // car(s)
					e = s5.cdr();    // cdr(car(s));
					Cons e4 = (Cons) e;
					SExp s6 = s3.car();  // car(cdr(s))
					e4.SetCar( s6 );
					c = s5.car();  // car(car(s))
					s = nil;
				}
				
				case 8 -> {   // SEL
					Cons c2 = (Cons) c;
					Cons c3 = (Cons) c2.cdr();  // cdr(c)
					Cons c4 = (Cons) c3.cdr();  // cdr(cdr(c))
					SExp c5 = c4.cdr();   // cdr(cdr(cdr(c))) 
					d = new Cons( c5, d );
					Cons s2 = (Cons) s;
					SymbolAtom s3 = (SymbolAtom) s2.car();  // car(s) 
					if( s3.GetSymbol().toUpperCase().equals( "T" ) ) 
						c = c3.car();  // car(cdr(c))
					else
						c = c4.car();  // car(cdr(cdr(c)))
					s = s2.cdr();		
				}
				
				case 9 -> {   // JOIN
					Cons d2 = (Cons) d;
					c = d2.car();   // car(d);
					d = d2.cdr();  // cdr(d)
				}
				
				case 10 -> {   // CAR
					Cons s2 = (Cons) s;
					Cons s3 = (Cons) s2.car();  // car(s)
					SExp s4 = s3.car();         // car(car(s))
					SExp s5 = s2.cdr();         // cdr(s)
					s = new Cons( s4, s5);      // cons(car(car(s)), cdr(s))
					Cons c2 = (Cons) c;
					c = c2.cdr();               // cdr(c) 
				}
				
				case 11 -> {   // CDR
					Cons s2 = (Cons) s;         
					Cons s3 = (Cons) s2.car();   // car(s)
					SExp s4 = s3.cdr();          // cdr(car(s))
					SExp s5 = s2.cdr();          // cdr(s)
					s = new Cons( s4, s5);       // cons(cdr(car(s)), cdr(s))
					Cons c2 = (Cons) c;
					c = c2.cdr();	            // cdr(c)
				}

				case 12 -> {  // ATOM
					Cons s2 = (Cons) s;
					SExp s3 = s2.car();   // car(s)
					if( s3.isNumber() || s3.isSymbol() ) 
						s = new Cons( (SExp) t, s2.cdr() );  // cons(t, cdr(s))
					else 
						s = new Cons( (SExp) f, s2.cdr() );  // cons(f, cdr(s))
					Cons c2 = (Cons) c;
					c = c2.cdr();	    // cdr(c)
				}
				
				case 13 -> {  // CONS
					Cons s2 = (Cons) s;
					SExp s3 = s2.car();   // car(s)
					Cons s4 = (Cons) s2.cdr();  // cdr(s)
					SExp s5 = s4.car();         // car(cdr(s))
					Cons s6 = new Cons( s3, s5);  // cons(car(s), car(cdr(s)))
					SExp s7 = s4.cdr();           // cdr(cdr(s))
					s = new Cons( s6, s7 );       // cons(cons(car(s), car(cdr(s))), cdr(cdr(s)))
					Cons c2 = (Cons) c;
					c = c2.cdr();			     // cdr(c)
				}
				
				case 14 -> {  // EQ
					Cons s2 = (Cons) s;
					SExp s3 = s2.car();  // car(s)
					Cons s4 = (Cons) s2.cdr();  // cdr(s)
					SExp s5 = s4.car(); // car(cdr(s))
					SExp s6 = s4.cdr(); // cdr(cdr(s))
					if( s3 == null || s5 == null ) {
						if( s3 != null )
							s = new Cons( f, s6 );  // cons(f, cdr(cdr(s)))
						else if( s5 != null  ) 
							s = new Cons( f, s6 );  // cons(f, cdr(cdr(s)))
						else
							s = new Cons( t, s6 );  // cons(t, cdr(cdr(s)))				
					}
					else if( s3.isSymbol() && s5.isSymbol() ) {
						SymbolAtom sa1 = (SymbolAtom) s3;  // car(s)
						String sv1 = sa1.GetSymbol();
						SymbolAtom sa2 = (SymbolAtom) s5;  // cdr(car(s))
						String sv2 = sa2.GetSymbol();
						if( sv1.equals( sv2 ) ) 
							s = new Cons( t, s6 );    // cons(t, cdr(cdr(s)))	
						else 
							s = new Cons( f, s6 );	  // cons(f, cdr(cdr(s)))	
					}
					else if( s3.isNumber() && s5.isNumber() ) {
						NumberAtom sa1 = (NumberAtom) s3;  // car(s)
						int sv1 = sa1.GetInt();
						NumberAtom sa2 = (NumberAtom) s5;  // cdr(car(s))
						int sv2 = sa2.GetInt();
						if( sv1 == sv2 ) 
							s = new Cons( t, s6 );  // cons(t, cdr(cdr(s)))	
						else
							s = new Cons( f, s6 );	 // cons(f, cdr(cdr(s)))
					}
					else
						s = new Cons( f, s6 );    // cons(f, cdr(cdr(s)))
					Cons c2 = (Cons) c;
					c = c2.cdr();			// cdr(c)				   
				}
				
			case 15 -> {  // ADD
					Cons s2 = (Cons) s;   // cdr(s)
					Cons s3 = (Cons) s2.cdr();
					NumberAtom  np = (NumberAtom) s3.car(); // number(car(cdr(s)))
					int n = np.GetInt();
					NumberAtom  np2 = (NumberAtom) s2.car(); // number(car(s))
					int n2 = np2.GetInt();
					NumberAtom np3 = new NumberAtom( n + n2 ); // number(n + n2)
					SExp s4 = s3.cdr();  // cdr(cdr(s))
					s = new Cons( np3, s4);  // cons(number(car(cdr(s)) + number(car(s))), cdr(cdr(s)))
					Cons c2 = (Cons) c;   // cdr(c)
					c = c2.cdr();						
				}

			case 16 -> {  // SUB
					Cons s2 = (Cons) s;
					Cons s3 = (Cons) s2.cdr();   // cdr(s)
					NumberAtom  np = (NumberAtom) s3.car();  // number(car(cdr(s)))
					int n = np.GetInt();
					NumberAtom  np2 = (NumberAtom) s2.car(); // number(car(s))
					int n2 = np2.GetInt();
					NumberAtom np3 = new NumberAtom( n - n2 );  // number(n - n2)
					SExp s4 = s3.cdr();      // cdr(cdr(s))
					s = new Cons( np3, s4);  // cons(number(car(cdr(s)) - number(car(s))), cdr(cdr(s)))
					Cons c2 = (Cons) c;
					c = c2.cdr();				
				}
				
			case 17 -> {  // MUL
					Cons s2 = (Cons) s;
					Cons s3 = (Cons) s2.cdr();  // cdr(s)
					NumberAtom  np = (NumberAtom) s3.car();  // number(car(cdr(s)))
					int n = np.GetInt();
					NumberAtom  np2 = (NumberAtom) s2.car();   // number(car(cdr(s)))
					int n2 = np2.GetInt();
					NumberAtom np3 = new NumberAtom( n * n2 ); // number(n * n2)
					SExp s4 = s3.cdr();    // cdr(cdr(s))
					s = new Cons( np3, s4);  // cons(number(car(cdr(s)) * number(car(s))), cdr(cdr(s)))
					Cons c2 = (Cons) c;
					c = c2.cdr();		// cdr(c)						
				}

			case 18 -> {  // DIV
					Cons s2 = (Cons) s;
					Cons s3 = (Cons) s2.cdr();    
					NumberAtom  np = (NumberAtom) s3.car();  // number(car(cdr(s)))
					int n = np.GetInt();
					NumberAtom  np2 = (NumberAtom) s2.car();  // number(car(cdr(s)))
					int n2 = np2.GetInt();
					NumberAtom np3 = new NumberAtom( n / n2 ); // number(n / n2)
					SExp s4 = s3.cdr();  // cdr(cdr(s))
					s = new Cons( np3, s4);  // cons(number(car(cdr(s)) / number(car(s))), cdr(cdr(s)))
					Cons c2 = (Cons) c;
					c = c2.cdr();  // cdr(c)				
				}

			case 19 -> {  // REM
					Cons s2 = (Cons) s;
					Cons s3 = (Cons) s2.cdr();  // cdr(s)
					NumberAtom  np = (NumberAtom) s3.car();  // number(car(cdr(s)))
					int n = np.GetInt();
					NumberAtom  np2 = (NumberAtom) s2.car(); // number(car(cdr(s)))
					int n2 = np2.GetInt();
					NumberAtom np3 = new NumberAtom( n % n2 );  // number(n % n2)
					SExp s4 = s3.cdr();   // cdr(cdr(s))
					s = new Cons( np3, s4);   // cons(number(car(cdr(s)) % number(car(s))), cdr(cdr(s)))
					Cons c2 = (Cons) c;
					c = c2.cdr();		// cdr(c)				
				}
				
			case 20 -> {   // LEQ
					Cons s2 = (Cons) s;
					Cons s3 = (Cons) s2.cdr();   // cdr(s)
					NumberAtom  np = (NumberAtom) s3.car();  // number(car(cdr(s)))
					int n = np.GetInt();
					NumberAtom  np2 = (NumberAtom) s2.car(); // number(car(cdr(s)))
					int n2 = np2.GetInt();
					SExp s4 = s3.cdr();   // cdr(cdr(s))
					if( n <= n2) 
						s = new Cons( t, s4 );   // cons(t, cdr(cdr(s)))
					else
						s = new Cons( f, s4 );   // cons(f, cdr(cdr(s)))
					Cons c2 = (Cons) c;
					c = c2.cdr();		// cdr(c)				
				}

			case 21 -> // STOP
				done = true;
			}
		}
		
		return ((Cons)s).car();
	}
	
	private int GetOp() {
		Cons cx = (Cons) c;
		NumberAtom np = (NumberAtom) cx.car();   // car(c)
		int op = np.GetInt();
		return op;
	}
}
