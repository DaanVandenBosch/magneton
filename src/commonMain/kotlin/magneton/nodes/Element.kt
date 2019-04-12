package magneton.nodes

import magneton.style.InlineCSSStyleDeclaration
import kotlin.reflect.KClass

interface ElementAttributeValue {
    fun toStringValue(): String
}

expect abstract class Element() : Parent {
    val attributes: Map<String, Any?>

    open fun <T> getAttribute(key: String): T?
    open fun setAttribute(key: String, value: Any? = null)
    open fun setAttribute(key: String, value: ElementAttributeValue?)
    open fun <T> removeAttribute(key: String): T?
}

expect open class HTMLElement(tagName: String) : Element

expect class HTMLDivElement() : HTMLElement
expect class HTMLSpanElement() : HTMLElement
expect class HTMLImageElement() : HTMLElement

fun <T : Element> Parent.addElement(
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

inline fun <reified T : Element> Parent.addElement(
        noinline create: () -> T,
        noinline block: T.() -> Unit
): T =
        addElement(create, T::class, block)

fun Parent.div(block: HTMLDivElement.() -> Unit): HTMLDivElement =
        addElement(::HTMLDivElement, block)

fun Parent.span(block: HTMLSpanElement.() -> Unit): HTMLSpanElement =
        addElement(::HTMLSpanElement, block)

fun Parent.header(block: HTMLElement.() -> Unit): HTMLElement =
        addElement({ HTMLElement("header") }, block)

fun Parent.footer(block: HTMLElement.() -> Unit): HTMLElement =
        addElement({ HTMLElement("footer") }, block)

fun Parent.main(block: HTMLElement.() -> Unit): HTMLElement =
        addElement({ HTMLElement("main") }, block)

fun Parent.img(block: HTMLImageElement.() -> Unit): HTMLImageElement =
        addElement(::HTMLImageElement, block)

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

var HTMLImageElement.src: String?
    get() = getAttribute("src")
    set(value) {
        if (value != null) setAttribute("src")
        else removeAttribute<Any>("src")
    }
