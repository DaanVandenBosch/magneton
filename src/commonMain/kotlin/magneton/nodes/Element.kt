package magneton.nodes

import magneton.style.InlineCSSStyleDeclaration
import kotlin.reflect.KClass

interface ElementAttributeValue {
    fun toStringValue(): String
}

expect abstract class Element() : Node {
    val attributes: Map<String, Any?>

    open fun <T> getAttribute(key: String): T?
    open fun setAttribute(key: String, value: Any? = null)
    open fun setAttribute(key: String, value: ElementAttributeValue?)
    open fun <T> removeAttribute(key: String): T?
}

abstract class HTMLElement : Element()
expect class HTMLDivElement() : HTMLElement
expect class HTMLSpanElement() : HTMLElement

fun <T : Element> Node.addElement(
        create: () -> T,
        elementClass: KClass<T>,
        block: T.() -> Unit
): T {
    val prevState = NodeState.Global.set(NodeState())!!
    val index = prevState.childIndex++
    var node = children.getOrNull(index)

    if (node == null || node::class != elementClass) {
        // TODO: is replaceChild faster than removeChild + appendChild in DOM?
        if (node != null) {
            removeChildAt(index)
        }

        node = create()
        addChild(index, node)
    }

    @Suppress("UNCHECKED_CAST")
    node as T

    try {
        node.block()

        // Clean up implicitly removed attributes and child nodes.
        val state = NodeState.Global.get()!!

        for (key in node.attributes.keys) {
            if (key !in state.updatedAttributes) {
                node.removeAttribute<Any>(key)
            }
        }

        node.removeChildrenFrom(state.childIndex)
    } finally {
        NodeState.Global.restore(prevState)
    }

    return node
}

inline fun <reified T : Element> Node.addElement(
        noinline create: () -> T,
        noinline block: T.() -> Unit
): T =
        addElement(create, T::class, block)

fun Node.div(block: HTMLDivElement.() -> Unit): HTMLDivElement =
        addElement(::HTMLDivElement, block)

fun Node.span(block: HTMLSpanElement.() -> Unit): HTMLSpanElement =
        addElement(::HTMLSpanElement, block)

var HTMLElement.hidden: Boolean
    get() = getAttribute("hidden") ?: false
    set(value) {
        if (value) setAttribute("hidden")
        else removeAttribute<Any>("hidden")
    }

fun HTMLElement.style(block: InlineCSSStyleDeclaration.() -> Unit) {
    val decl = InlineCSSStyleDeclaration()
    decl.invoke(block)
    setAttribute("style", decl)
}
