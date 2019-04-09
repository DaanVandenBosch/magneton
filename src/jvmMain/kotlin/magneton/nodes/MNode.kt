package magneton.nodes

actual abstract class MNode {
    private val _children: MutableList<MNode> = mutableListOf()

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

actual class MHTMLDivElement : MHTMLElement()
actual class MHTMLSpanElement : MHTMLElement()
