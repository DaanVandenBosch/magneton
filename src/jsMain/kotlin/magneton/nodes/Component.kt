package magneton.nodes

import magneton.observable.collections.ObservableList

actual abstract class Component : Node() {
    actual abstract val root: Node

    override var children: ObservableList<Node>
        get() = root.children
        set(value) {
            root.children = value
        }

    override val domNode: org.w3c.dom.Node get() = root.domNode
}
