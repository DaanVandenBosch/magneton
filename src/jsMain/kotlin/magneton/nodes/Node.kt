package magneton.nodes

import org.w3c.dom.get
import kotlin.browser.document
import org.w3c.dom.Node as DomNode

actual abstract class Node {
    internal actual var isMounted: Boolean = false

    abstract val domNode: DomNode?

    internal var internalParent: Parent? = null
    val parent: Parent? get() = internalParent

    actual open fun didMount() {}
    actual open fun willUnmount() {}
}

// TODO: share code with JVM implementation
actual abstract class Parent : Node() {
    private val _children: MutableList<Node> = mutableListOf()
    actual val children: List<Node> = _children

    internal actual open fun addChild(child: Node) {
        addChild(children.size, child)
    }

    internal actual open fun addChild(index: Int, child: Node) {
        if (child.parent != null) {
            child.parent!!._children.remove(child)
            child.internalParent = this
        }

        _children.add(index, child)

        child.domNode?.let { domAddChild(index, it) }
    }

    internal open fun domAddChild(index: Int, childDomNode: DomNode) {
        domNode?.let { domNode ->
            domNode.insertBefore(childDomNode, domNode.childNodes[index])
        }
    }

    internal actual open fun removeChildAt(index: Int) {
        val removed = _children.removeAt(index)
        removed.internalParent = null

        removed.domNode?.let(::domRemoveChild)
    }

    internal actual open fun removeChildrenFrom(index: Int) {
        for (i in index..children.lastIndex) {
            removeChildAt(index)
        }
    }

    internal open fun domRemoveChild(childDomNode: DomNode) {
        domNode?.removeChild(childDomNode)
    }
}

actual class Text actual constructor(data: String) : Node() {
    override val domNode = document.createTextNode(data)

    actual var data: String = data
        set(value) {
            domNode.replaceData(0, field.length, value)
            field = value
        }
}
