package magneton.nodes

import magneton.observable.ReactionDisposer
import magneton.observable.reaction
import org.w3c.dom.Node

// TODO: share code with JVM implementation.
actual abstract class Component : MNode() {
    override val domNode: Node? get() = children.firstOrNull()?.domNode

    internal actual var disposer: ReactionDisposer? = null

    actual abstract fun render(): MNode

    override fun addChild(index: Int, child: MNode) {
        check(children.isEmpty() && index == 0) {
            "A component can have at most one direct child."
        }

        super.addChild(index, child)
    }

    override fun domAddChild(index: Int, childDomNode: Node) {
        parent?.let { parent ->
            // TODO: optimize indexOf
            parent.domAddChild(parent.children.indexOf(this), childDomNode)
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
