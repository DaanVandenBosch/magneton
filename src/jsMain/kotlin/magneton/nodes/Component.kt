package magneton.nodes

import magneton.observable.ReactionDisposer
import magneton.observable.reaction
import org.w3c.dom.Node

// TODO: share code with JVM implementation.
actual abstract class Component : MNode() {
    override val domNode: Node? get() = children.firstOrNull()?.domNode

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

fun renderToDom(domNode: Node, component: Component) {
    reaction {
        stack.push(Frame())
        component.render()
        stack.pop()
    }
    domNode.appendChild(component.domNode!!)
}
