package magneton

actual class FastSet<E> {
    private val jsSet = js("new Set")

    actual operator fun contains(element: E): Boolean = jsSet.has(element).unsafeCast<Boolean>()

    actual fun add(element: E) {
        jsSet.add(element)
    }

    actual fun remove(element: E): Boolean = jsSet.delete(element).unsafeCast<Boolean>()

    actual fun removeAll(predicate: (E) -> Boolean) {
        jsSet.forEach { element: E ->
            if (predicate(element)) {
                remove(element)
            }
        }
    }

    actual fun clear() {
        jsSet.clear()
    }

    actual fun forEach(action: (E) -> Unit) {
        jsSet.forEach(action)
    }
}
