package magneton.nodes

import org.w3c.dom.get
import org.w3c.dom.Node as DomNode

// TODO: share code with JVM implementation
actual abstract class Node {
    abstract val domNode: DomNode?

    private var _parent: Node? = null
    val parent: Node? get() = _parent

    private val _children: MutableList<Node> = mutableListOf()
    actual val children: List<Node> = _children

    internal actual open fun addChild(child: Node) {
        addChild(children.size, child)
    }

    internal actual open fun addChild(index: Int, child: Node) {
        if (child.parent != null) {
            child.parent!!._children.remove(child)
            child._parent = this
        }

        _children.add(index, child)

        child.domNode?.let { domAddChild(index, it) }
    }

    internal actual open fun removeChildAt(index: Int) {
        val removed = _children.removeAt(index)
        removed._parent = null

        removed.domNode?.let(::domRemoveChild)
    }

    internal actual open fun removeChildrenFrom(index: Int) {
        for (i in index..children.lastIndex) {
            removeChildAt(index)
        }
    }

    internal open fun domAddChild(index: Int, childDomNode: DomNode) {
        domNode?.let { domNode ->
            domNode.insertBefore(childDomNode, domNode.childNodes[index])
        }
    }

    internal open fun domRemoveChild(childDomNode: DomNode) {
        domNode?.removeChild(childDomNode)
    }
}
