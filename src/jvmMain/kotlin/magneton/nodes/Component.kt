package magneton.nodes

import magneton.observable.ReactionDisposer

actual abstract class Component : MNode() {
    internal actual var disposer: ReactionDisposer? = null

    actual abstract fun render(): MNode

    override fun addChild(index: Int, child: MNode) {
        check(children.isEmpty() && index == 0) {
            "A component can have at most one direct child."
        }

        super.addChild(index, child)
    }
}
