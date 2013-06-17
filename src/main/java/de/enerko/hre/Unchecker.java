package de.enerko.hre;

/**
 * @author Michael J. Simons, 2012-02-03
 * http://www.gamlor.info/wordpress/2010/02/throwing-checked-excpetions-like-unchecked-exceptions-in-java/
 */
public final class Unchecker {
	private Unchecker(){}

	// Now this returns an exception, so that you can satisfy the compiler by throwing it.
	// But in reality we throw the given exception!
	public static RuntimeException uncheck(final Exception ex){
		// Now we use the 'generic' method. Normally the type T is inferred
		// from the parameters. However you can specify the type also explicit!
		// Now we du just that! We use the RuntimeException as type!
		// That means the throwsUnchecked throws an unchecked exception!
		// Since the types are erased, no type-information is there to prevent this!
		Unchecker.<RuntimeException>throwsUnchecked(ex);

		// This is here is only to satisfy the compiler. It's actually unreachable code!
		throw new AssertionError("This code should be unreachable. Something went terrible wrong here!");
	}

	/**
	 * Remember, Generics are erased in Java. So this basically throws an Exception. The real
	 * Type of T is lost during the compilation
	 */	
	private static <T extends Exception> void throwsUnchecked(Exception toThrow) throws T{
		// Since the type is erased, this cast actually does nothing!!!
		// we can throw any exception
		// Cannot use @SuppressWarnings("unchecked") due to Oracle Restrictions!
		throw (T) toThrow;
	}
}