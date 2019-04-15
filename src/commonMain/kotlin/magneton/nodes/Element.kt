package magneton.nodes

import magneton.style.CSSClassRule
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
        elementClass: KClass<T>,
        create: () -> T,
        cssClass: CSSClassRule? = null,
        block: (T.() -> Unit)? = null
): T {
    val ctx = context!!
    val prevState = ctx.nodeState
    ctx.nodeState = NodeState()
    val index = prevState.childIndex++
    var node = children.getOrNull(index)

    if (node == null || node::class != elementClass) {
        // TODO: is replaceChild faster than removeChild + appendChild in DOM?
        if (node != null) {
            notifyWillUnmount(node)
            removeChildAt(index)
        }

        node = create()
        node.context = ctx
        addChild(index, node)

        if (isMounted) {
            notifyDidMount(node)
        }
    }

    @Suppress("UNCHECKED_CAST")
    node as T

    try {
        cssClass?.let {
            ctx.styleSheetRegistry.register(it.styleSheet)
            node.className = it.selector.uniqueName
        }

        block?.invoke(node)

        // Clean up implicitly removed attributes and child nodes.
        val state = ctx.nodeState

        for (key in node.attributes.keys) {
            if (key !in state.updatedAttributes) {
                node.removeAttribute<Any>(key)
            }
        }

        if (isMounted) {
            for (i in state.childIndex..node.children.lastIndex) {
                notifyWillUnmount(node.children[i])
            }
        }

        node.removeChildrenFrom(state.childIndex)
    } finally {
        ctx.nodeState = prevState
    }

    return node
}

inline fun <reified T : Element> Parent.addElement(
        noinline create: () -> T,
        cssClass: CSSClassRule? = null,
        noinline block: (T.() -> Unit)? = null
): T =
        addElement(T::class, create, cssClass, block)

fun Parent.div(cssClass: CSSClassRule? = null, block: (HTMLDivElement.() -> Unit)? = null): HTMLDivElement =
        addElement(::HTMLDivElement, cssClass, block)

fun Parent.span(cssClass: CSSClassRule? = null, block: (HTMLSpanElement.() -> Unit)? = null): HTMLSpanElement =
        addElement(::HTMLSpanElement, cssClass, block)

fun Parent.header(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement({ HTMLElement("header") }, cssClass, block)

fun Parent.footer(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement({ HTMLElement("footer") }, cssClass, block)

fun Parent.main(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement({ HTMLElement("main") }, cssClass, block)

fun Parent.img(cssClass: CSSClassRule? = null, block: (HTMLImageElement.() -> Unit)? = null): HTMLImageElement =
        addElement(::HTMLImageElement, cssClass, block)

var Element.className: String
    get() = getAttribute("class") ?: ""
    set(value) {
        setAttribute("class", value)
    }

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
