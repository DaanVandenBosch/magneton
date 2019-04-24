package magneton

/**
 * Use this method only when you're absolutely sure the cast will succeed.
 * Avoids an expensive type check in JS. It won't throw an exception if the cast is invalid.
 * Simple unchecked cast on JVM, will throw a [ClassCastException] if cast is invalid.
 */
internal expect fun <T> Any?.unsafeCast(): T
