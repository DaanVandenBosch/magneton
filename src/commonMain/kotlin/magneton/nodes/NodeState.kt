package magneton.nodes

import magneton.GlobalState

internal class NodeState(
        var childIndex: Int = 0,
        val updatedAttributes: MutableSet<String> = mutableSetOf()
) {
    companion object {
        val Global = GlobalState<NodeState>()
    }
}
