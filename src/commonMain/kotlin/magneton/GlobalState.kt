package magneton

/**
 * Intended to be used with code that needs to keep state around while passing control to client code which might give control back to the library. This class is thread-safe.
 *
 * Typical usage:
 * ```
 * val prevState = MyGlobalState.set(newState)
 *
 * try {
 *     // The current function might be called recursively by the client code.
 *     callClientCode()
 *     useCurrentState(MyGlobalState.get())
 * } finally {
 *     MyGlobalState.restore(prevState)
 * }
 * ```
 *
 * Note: make sure to eventually call [clear] to avoid memory leaks.
 */
expect class GlobalState<T>() {
    /**
     * Retrieves the state object.
     */
    fun get(): T?

    /**
     * Initializes a new state object and returns the previous one.
     */
    fun set(state: T): T?

    /**
     * Replaces the state object.
     */
    fun restore(state: T?)

    /**
     * Clears the global state object, same as [restore] with null as argument.
     */
    fun clear()
}
