package magneton.nodes

import magneton.observable.ReactionDisposer
import magneton.observable.reaction
import org.w3c.dom.Node as DomNode

// TODO: share code with JVM implementation.
actual abstract class Component : Parent() {
    override val domNode: DomNode? get() = children.firstOrNull()?.domNode

    internal actual var disposer: ReactionDisposer? = null

    actual abstract fun render(): Node

    override fun addChild(index: Int, child: Node) {
        check(children.isEmpty()) {
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

fun renderToDom(domNode: DomNode, component: Component): ReactionDisposer {
    val disposer = reaction {
        NodeState.Global.set(NodeState())

        try {
            component.render()
        } finally {
            NodeState.Global.clear()
        }
    }
    domNode.appendChild(component.domNode!!)
    notifyDidMount(component)
    return disposer
}
