package magneton

internal actual class GlobalState<T> {
    private val state: ThreadLocal<T?> = ThreadLocal()

    actual fun get(): T? = state.get()

    actual fun set(state: T): T? {
        val prev = this.state.get()
        this.state.set(state)
        return prev
    }

    actual fun restore(state: T?) {
        if (state == null) {
            this.state.remove()
        } else {
            this.state.set(state)
        }
    }

    actual fun clear() {
        state.remove()
    }
}
