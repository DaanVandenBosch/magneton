package magneton

actual class FastSet<E> {
    private val set = HashSet<E>()

    actual operator fun contains(element: E): Boolean = set.contains(element)

    actual fun add(element: E) {
        set.add(element)
    }

    actual fun remove(element: E): Boolean = set.remove(element)

    actual fun removeAll(predicate: (E) -> Boolean) {
        val iter = set.iterator()

        while (iter.hasNext()) {
            if (predicate(iter.next())) {
                iter.remove()
            }
        }
    }

    actual fun clear() {
        set.clear()
    }

    actual fun forEach(action: (E) -> Unit) {
        set.forEach(action)
    }
}
