package magneton.nodes

private var globalNodeState: GlobalNodeState? = null

internal actual class GlobalNodeState {
    actual var childIndex: Int = 0
    actual val updatedAttributes: MutableSet<String> = mutableSetOf()

    actual companion object {
        actual fun get(): GlobalNodeState = globalNodeState!!

        actual fun set(childIndex: Int): GlobalNodeState? {
            val prev = globalNodeState
            globalNodeState = GlobalNodeState()
            globalNodeState!!.childIndex = childIndex
            return prev
        }

        actual fun restore(state: GlobalNodeState?) {
            globalNodeState = state
        }

        actual fun clear() {
            restore(null)
        }
    }
}
