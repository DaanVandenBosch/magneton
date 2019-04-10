package magneton.nodes

@DslMarker
annotation class NodeMarker

@NodeMarker
expect abstract class MNode() {
    val children: List<MNode>

    internal open fun addChild(child: MNode)

    internal open fun addChild(index: Int, child: MNode)

    internal open fun removeChildAt(index: Int)

    internal open fun removeChildrenFrom(index: Int)
}

abstract class MElement : MNode()

abstract class MHTMLElement : MElement()

expect class MHTMLDivElement() : MHTMLElement
expect class MHTMLSpanElement() : MHTMLElement

internal inline fun <reified T : MNode> MNode.addElement(
        create: () -> T,
        block: T.() -> Unit
): T {
    val index = stack.peek().index++
    var node = children.getOrNull(index)

    if (node == null || node !is T) {
        // TODO: optimize with replace
        if (node != null) {
            removeChildAt(index)
        }

        node = create()
        addChild(index, node)
    }

    val frame = Frame()
    stack.push(frame)

    try {
        node.block()
        node.removeChildrenFrom(frame.index)
    } finally {
        stack.pop()
    }

    return node
}

fun MNode.div(block: MHTMLDivElement.() -> Unit): MHTMLDivElement =
        addElement(::MHTMLDivElement, block)

fun MNode.span(block: MHTMLSpanElement.() -> Unit): MHTMLSpanElement =
        addElement(::MHTMLSpanElement, block)
