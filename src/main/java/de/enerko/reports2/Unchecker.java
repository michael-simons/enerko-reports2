package de.enerko.reports2;

/**
 * @author Michael J. Simons, 2012-02-03
 * http://www.gamlor.info/wordpress/2010/02/throwing-checked-excpetions-like-unchecked-exceptions-in-java/
 */
public final class Unchecker {
	private Unchecker(){}

	/**
	 * Unchecks  the {@link Throwable} ex and turns it into a {@link RuntimeException}
	 * without adding to the stacktrace
	 * @param ex
	 * @return
	 */
	public static RuntimeException uncheck(final Throwable ex){
		// Now this returns an exception, so that you can satisfy the compiler by throwing it.
		// But in reality we throw the given exception!
		
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
	private static <T extends Exception> void throwsUnchecked(Throwable toThrow) throws T{
		// Since the type is erased, this cast actually does nothing!!!
		// we can throw any exception
		// Cannot use @SuppressWarnings("unchecked") due to Oracle Restrictions!
		throw (T) toThrow;
	}
}