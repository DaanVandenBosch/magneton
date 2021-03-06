package magneton.nodes

import magneton.Context
import org.w3c.dom.get
import kotlin.browser.document
import kotlin.reflect.KClass
import org.w3c.dom.Node as DomNode

actual abstract class Node {
    private var _kClass: KClass<*>? = null
    internal actual val kClass: KClass<*>
        get() {
            if (_kClass == null) {
                _kClass = this::class
            }
            return _kClass.unsafeCast<KClass<*>>()
        }
    internal actual var context: Context? = null
    internal actual var isMounted: Boolean = false
    internal actual abstract val nodeType: NodeType

    actual open val isParent: Boolean = false
    abstract val domNode: DomNode?

    internal var internalParent: Parent? = null
    val parent: Parent? get() = internalParent

    actual open fun didMount() {}
    actual open fun willUnmount() {}
    internal actual open fun internalWillUnmount() {}
}

// TODO: share code with JVM implementation
actual abstract class Parent : Node() {
    actual override val isParent: Boolean = true

    private val _children: MutableList<Node> = mutableListOf()
    actual val children: List<Node> = _children

    internal actual open fun addChild(child: Node) {
        addChild(children.size, child)
    }

    internal actual open fun addChild(index: Int, child: Node) {
        if (child.parent != null) {
            child.parent!!._children.remove(child)
        }

        child.internalParent = this
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

        removed.domNode?.let { domNode ->
            domNode.parentNode?.removeChild(domNode)
        }
    }

    internal actual open fun removeChildrenFrom(index: Int) {
        for (i in index..children.lastIndex) {
            removeChildAt(index)
        }
    }
}

actual class Text actual constructor(data: String) : Node() {
    override val nodeType: NodeType = textNodeType
    override val domNode = document.createTextNode(data)

    actual var data: String = data
        set(value) {
            if (value != field) {
                domNode.replaceData(0, field.length, value)
                field = value
            }
        }
}
