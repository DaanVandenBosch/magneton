package magneton.nodes

@DslMarker
annotation class NodeDslMarker

@NodeDslMarker
expect abstract class Node()

expect abstract class Parent() : Node {
    val children: List<Node>

    internal open fun addChild(child: Node)
    internal open fun addChild(index: Int, child: Node)

    internal open fun removeChildAt(index: Int)
    internal open fun removeChildrenFrom(index: Int)
}

expect class Text(data: String) : Node {
    var data: String
}

fun Parent.text(data: String): Text {
    val index = NodeState.Global.get()!!.childIndex++
    var node = children.getOrNull(index)

    if (node == null || node::class != Text::class) {
        // TODO: is replaceChild faster than removeChild + appendChild in DOM?
        if (node != null) {
            removeChildAt(index)
        }

        node = Text(data)
        addChild(index, node)
    } else {
        node as Text
        node.data = data
    }

    return node
}
