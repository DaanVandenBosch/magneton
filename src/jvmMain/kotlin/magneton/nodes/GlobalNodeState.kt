package magneton.nodes

private val globalNodeState: ThreadLocal<GlobalNodeState?> = ThreadLocal()

internal actual class GlobalNodeState {
    actual var childIndex: Int = 0
    actual val updatedAttributes: MutableSet<String> = mutableSetOf()

    actual companion object {
        actual fun get(): GlobalNodeState = globalNodeState.get()!!

        actual fun set(childIndex: Int): GlobalNodeState? {
            val prev = globalNodeState.get()
            globalNodeState.set(GlobalNodeState().also { it.childIndex = childIndex })
            return prev
        }

        actual fun restore(state: GlobalNodeState?) {
            if (state == null) {
                globalNodeState.remove()
            } else {
                globalNodeState.set(state)
            }
        }

        actual fun clear() {
            globalNodeState.remove()
        }
    }
}
