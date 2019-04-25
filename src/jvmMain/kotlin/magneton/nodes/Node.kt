package magneton.nodes

import magneton.Context
import kotlin.reflect.KClass

actual abstract class Node {
    internal actual val kClass: KClass<*> = this::class
    internal actual var context: Context? = null
    internal actual var isMounted: Boolean = false
    internal actual abstract val nodeType: NodeType
    actual open val isParent: Boolean = false

    actual open fun didMount() {}
    actual open fun willUnmount() {}
}

// TODO: share code with JS implementation
actual abstract class Parent : Node() {
    actual override val isParent: Boolean = true

    private val _children: MutableList<Node> = mutableListOf()
    actual val children: List<Node> = _children

    internal actual open fun addChild(child: Node) {
        addChild(children.size, child)
    }

    internal actual open fun addChild(index: Int, child: Node) {
        _children.add(index, child)
    }

    internal actual open fun removeChildAt(index: Int) {
        _children.removeAt(index)
    }

    internal actual open fun removeChildrenFrom(index: Int) {
        for (i in index..children.lastIndex) {
            removeChildAt(index)
        }
    }
}

actual class Text actual constructor(actual var data: String) : Node() {
    override val nodeType: NodeType = textNodeType
}
