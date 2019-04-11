package magneton.nodes

internal expect class GlobalNodeState {
    var childIndex: Int
    val updatedAttributes: MutableSet<String>

    companion object {
        /**
         * Retrieves the [GlobalNodeState].
         */
        fun get(): GlobalNodeState

        /**
         * Initializes a new [GlobalNodeState] and returns the previous one.
         */
        fun set(childIndex: Int = 0): GlobalNodeState?

        /**
         * Replaces the [GlobalNodeState].
         */
        fun restore(state: GlobalNodeState?)

        /**
         * Clears the [GlobalNodeState], same as [restore] with null as argument.
         */
        fun clear()
    }
}
