package magneton

internal actual class GlobalState<T> {
    private var state: T? = null

    actual fun get(): T? = state

    actual fun set(state: T): T? {
        val prev = this.state
        this.state = state
        return prev
    }

    actual fun restore(state: T?) {
        this.state = state
    }

    actual fun clear() {
        restore(null)
    }
}
