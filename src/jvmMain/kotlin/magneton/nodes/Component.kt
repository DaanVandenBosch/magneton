package magneton.nodes

import magneton.observable.ReactionDisposer

actual abstract class Component : Parent() {
    actual override val nodeType: NodeType = componentNodeType

    internal actual var disposer: ReactionDisposer? = null

    actual abstract fun render(): Node?

    override fun addChild(index: Int, child: Node) {
        check(children.isEmpty()) {
            "A component can have at most one direct child."
        }

        super.addChild(index, child)
    }
}
