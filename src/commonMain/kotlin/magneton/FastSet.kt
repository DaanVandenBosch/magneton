package magneton

/**
 * Does not use Kotlin hashCode and equals in JS.
 * Delegates to a native Set in JS.
 */
expect class FastSet<E>() {
    operator fun contains(element: E): Boolean

    fun add(element: E)

    fun remove(element: E): Boolean

    fun removeAll(predicate: (E) -> Boolean)

    fun clear()

    fun forEach(action: (E) -> Unit)
}
