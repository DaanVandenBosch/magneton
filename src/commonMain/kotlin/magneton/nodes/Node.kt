package magneton.nodes

@DslMarker
annotation class NodeMarker

@NodeMarker
expect abstract class Node() {
    val children: List<Node>

    internal open fun addChild(child: Node)
    internal open fun addChild(index: Int, child: Node)

    internal open fun removeChildAt(index: Int)
    internal open fun removeChildrenFrom(index: Int)
}
