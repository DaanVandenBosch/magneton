package magneton.nodes

// TODO: share code with JS implementation
actual abstract class Node {
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
