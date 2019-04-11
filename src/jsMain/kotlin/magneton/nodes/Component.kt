package magneton.nodes

import magneton.observable.ReactionDisposer
import magneton.observable.reaction
import org.w3c.dom.Node as DomNode

// TODO: share code with JVM implementation.
actual abstract class Component : Node() {
    override val domNode: DomNode? get() = children.firstOrNull()?.domNode

    internal actual var disposer: ReactionDisposer? = null

    actual abstract fun render(): Node

    override fun addChild(index: Int, child: Node) {
        check(children.isEmpty() && index == 0) {
            "A component can have at most one direct child."
        }

        super.addChild(index, child)
    }

    override fun domAddChild(index: Int, childDomNode: DomNode) {
        parent?.let { parent ->
            // TODO: optimize indexOf
            parent.domAddChild(parent.children.indexOf(this), childDomNode)
        }
    }
}

fun renderToDom(domNode: DomNode, component: Component) {
    reaction {
        stack.push(Frame())

        try {
            component.render()
        } finally {
            stack.pop()
        }
    }
    domNode.appendChild(component.domNode!!)
}
