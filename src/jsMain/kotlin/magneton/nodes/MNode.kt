package magneton.nodes

import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLSpanElement
import org.w3c.dom.Node
import kotlin.browser.document

// TODO: share code with JVM implementation
// TODO: make add and remove methods update DOM
actual abstract class MNode {
    private val _children: MutableList<MNode> = mutableListOf()

    abstract val domNode: Node?
    actual val children: List<MNode> = _children

    internal actual open fun addChild(child: MNode) {
        _children.add(child)
    }

    internal actual open fun addChild(index: Int, child: MNode) {
        _children.add(index, child)
    }

    internal actual fun removeChildAt(index: Int) {
        _children.removeAt(index)
    }

    internal actual fun removeChildrenFrom(index: Int) {
        for (i in index..children.lastIndex) {
            removeChildAt(i)
        }
    }
}

actual class MHTMLDivElement : MHTMLElement() {
    override val domNode = document.createElement("div") as HTMLDivElement
}

actual class MHTMLSpanElement : MHTMLElement() {
    override val domNode = document.createElement("span") as HTMLSpanElement
}
