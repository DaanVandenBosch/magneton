package magneton.nodes

import magneton.observable.ReactionDisposer

actual abstract class Component : MNode() {
    internal actual var disposer: ReactionDisposer? = null

    actual abstract fun render(): MNode

    actual override fun addChild(child: MNode) {
        checkChildrenEmpty()
        super.addChild(child)
    }

    actual override fun addChild(index: Int, child: MNode) {
        checkChildrenEmpty()
        super.addChild(index, child)
    }

    private fun checkChildrenEmpty() {
        require(children.isEmpty()) {
            "A component can have at most one direct child."
        }
    }
}
