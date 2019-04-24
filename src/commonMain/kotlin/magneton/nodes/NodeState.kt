package magneton.nodes

class NodeState(
        var childIndex: Int = 0,

        /**
         * Multiple updates result in multiple entries in this list.
         * In general much faster than using a set.
         */
        val updatedAttributes: MutableList<String> = mutableListOf()
)
