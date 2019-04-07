package magneton.nodes

import magneton.observable.Subscription
import magneton.observable.collections.ListChange
import magneton.observable.collections.MutableObservableList
import magneton.observable.collections.ObservableList
import magneton.observable.observableListOf
import org.w3c.dom.get
import kotlin.browser.document

actual abstract class Node {
    private var childrenSub: Subscription? = null
    private var mutableParent: Node? = null

    actual val parent: Node? get() = mutableParent

    actual open var children: ObservableList<Node> = observeChildren(observableListOf())
        set(value) {
            if (field != value) {
                processRemovals(field)
                field = value
                observeChildren(field)
            }
        }

    abstract val domNode: org.w3c.dom.Node

    actual fun addChild(child: Node) {
        (children as? MutableObservableList<Node>)?.add(child)
    }

    actual fun removeChild(child: Node) {
        (children as? MutableObservableList<Node>)?.remove(child)
    }

    private fun remove() {
        domNode.parentNode?.removeChild(domNode)
        childrenSub?.unsubscribe()
    }

    private fun observeChildren(children: ObservableList<Node>): ObservableList<Node> {
        childrenSub?.unsubscribe()
        childrenSub = children.onListChange { change ->
            when (change) {
                is ListChange.Addition -> {
                    processAdditions(change.addedWithIndex)
                }
                is ListChange.Removal -> {
                    processRemovals(change.removed)
                }
                is ListChange.Replacement -> {
                    processRemovals(change.removed)
                    processAdditions(change.addedWithIndex)
                }
                is ListChange.Update -> {
                    // Updates should be handled by child components.
                }
            }
        }

        return children
    }

    private fun processAdditions(addedWithIndex: Iterable<IndexedValue<Node>>) {
        for ((i, child) in addedWithIndex) {
            domNode.insertBefore(child.domNode, domNode.childNodes[i])
            val oldParent = child.parent

            if (oldParent != this) {
                // We set the child's parent first to avoid the child's remove method being called when it is removed from its previous parent's children list.
                child.mutableParent = this
                oldParent?.removeChild(child)
            }
        }
    }

    private fun processRemovals(removed: Iterable<Node>) {
        for (child in removed) {
            if (child.parent == this) {
                child.remove()
            }
        }
    }
}

actual class HTMLDivElement : HTMLElement() {
    override val domNode: org.w3c.dom.HTMLDivElement =
            document.createElement("div") as org.w3c.dom.HTMLDivElement
}
