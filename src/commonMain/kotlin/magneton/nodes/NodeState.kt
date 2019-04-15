package magneton.nodes

class NodeState(
        var childIndex: Int = 0,
        val updatedAttributes: MutableSet<String> = mutableSetOf()
)
