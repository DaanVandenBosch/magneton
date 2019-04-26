package magneton.nodes

import magneton.Context
import magneton.unsafeCast
import kotlin.reflect.KClass

@DslMarker
annotation class NodeDslMarker

@NodeDslMarker
expect abstract class Node() {
    internal val kClass: KClass<*>
    internal var context: Context?

    /**
     * Note: this property will be set to false before the node is actually unmounted but after [willUnmount] is called.
     */
    internal var isMounted: Boolean

    /**
     * This is a performance improvement over using KClasses to compare nodes. Looking up an
     * objects KClass is very slow in JS.
     */
    internal abstract val nodeType: NodeType

    /**
     * Performance optimized way of checking whether a node is a subclass of [Parent].
     */
    open val isParent: Boolean

    /**
     * Called after the node is first rendered and added to the DOM.
     */
    open fun didMount()

    /**
     * Called before the node is removed from the DOM.
     */
    open fun willUnmount()

    internal open fun internalWillUnmount()
}

expect abstract class Parent() : Node {
    override val isParent: Boolean

    val children: List<Node>

    internal open fun addChild(child: Node)
    internal open fun addChild(index: Int, child: Node)

    internal open fun removeChildAt(index: Int)
    internal open fun removeChildrenFrom(index: Int)
}

internal val textNodeType: NodeType = stringToNodeType("magneton.nodes.Text")

expect class Text(data: String) : Node {
    var data: String
}

fun Parent.text(data: String): Text {
    val index = context!!.nodeState.childIndex++
    val node = children.getOrNull(index)
    val text: Text

    if (node == null || node.nodeType != textNodeType) {
        // TODO: is replaceChild faster than removeChild + appendChild in DOM?
        if (node != null) {
            removeChildAt(index)
        }

        text = Text(data)
        text.context = context
        addChild(index, text)
    } else {
        text = node.unsafeCast()
        text.data = data
    }

    return text
}

internal fun notifyDidMount(node: Node) {
    if (node.isMounted) return

    node.isMounted = true
    node.didMount()

    if (node.isParent) {
        node.unsafeCast<Parent>().children.forEach(::notifyDidMount)
    }
}

internal fun notifyWillUnmount(node: Node) {
    if (!node.isMounted) return

    node.willUnmount()

    if (node.isParent) {
        node.unsafeCast<Parent>().children.forEach(::notifyWillUnmount)
    }

    node.internalWillUnmount()
    node.isMounted = false
}
