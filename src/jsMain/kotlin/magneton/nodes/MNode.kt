package magneton.nodes

import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLSpanElement
import org.w3c.dom.Node
import org.w3c.dom.get
import kotlin.browser.document

// TODO: share code with JVM implementation
actual abstract class MNode {
    private var _parent: MNode? = null
    private val _children: MutableList<MNode> = mutableListOf()

    abstract val domNode: Node?
    val parent: MNode? get() = _parent
    actual val children: List<MNode> = _children

    internal actual open fun addChild(child: MNode) {
        addChild(children.size, child)
    }

    internal actual open fun addChild(index: Int, child: MNode) {
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

    internal open fun domAddChild(index: Int, childDomNode: Node) {
        domNode?.let { domNode ->
            domNode.insertBefore(childDomNode, domNode.childNodes[index])
        }
    }

    internal open fun domRemoveChild(childDomNode: Node) {
        domNode?.removeChild(childDomNode)
    }
}

actual class MHTMLDivElement : MHTMLElement() {
    override val domNode = document.createElement("div") as HTMLDivElement
}

actual class MHTMLSpanElement : MHTMLElement() {
    override val domNode = document.createElement("span") as HTMLSpanElement
}
