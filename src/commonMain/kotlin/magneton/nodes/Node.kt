package magneton.nodes

import magneton.observable.collections.ObservableList

@DslMarker
annotation class NodeMarker

@NodeMarker
expect abstract class Node() {
    val parent: Node?
    open var children: ObservableList<Node>

    fun addChild(child: Node)
    fun removeChild(child: Node)
}

abstract class Element : Node()

abstract class HTMLElement : Element()

expect class HTMLDivElement() : HTMLElement

fun HTMLElement.div(block: HTMLDivElement.() -> Unit): HTMLDivElement =
        HTMLDivElement().also(::addChild).also(block)
