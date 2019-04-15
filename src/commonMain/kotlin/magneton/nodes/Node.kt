package magneton.nodes

import magneton.Context

@DslMarker
annotation class NodeDslMarker

@NodeDslMarker
expect abstract class Node() {
    internal var context: Context?

    /**
     * Note: this property will be set to false before the node is actually unmounted but after [willUnmount] is called.
     */
    internal var isMounted: Boolean

    /**
     * Called after the node is first rendered and added to the DOM.
     */
    open fun didMount()

    /**
     * Called before the node is removed from the DOM.
     */
    open fun willUnmount()
}

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
    val index = context!!.nodeState.childIndex++
    var node = children.getOrNull(index)

    if (node == null || node::class != Text::class) {
        // TODO: is replaceChild faster than removeChild + appendChild in DOM?
        if (node != null) {
            removeChildAt(index)
        }

        node = Text(data)
        node.context = context
        addChild(index, node)
    } else {
        node as Text
        node.data = data
    }

    return node
}

internal fun notifyDidMount(node: Node) {
    if (node.isMounted) return

    node.isMounted = true
    node.didMount()

    if (node is Parent) {
        node.children.forEach(::notifyDidMount)
    }
}

internal fun notifyWillUnmount(node: Node) {
    if (!node.isMounted) return

    node.willUnmount()

    if (node is Parent) {
        node.children.forEach(::notifyWillUnmount)
    }

    node.isMounted = false
}
