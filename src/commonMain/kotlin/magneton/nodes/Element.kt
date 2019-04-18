package magneton.nodes

import magneton.style.CSSClassRule
import magneton.style.InlineCSSStyleDeclaration
import kotlin.reflect.KClass

interface ElementAttributeValue {
    fun toStringValue(): String
}

expect abstract class Element internal constructor() : Parent {
    val attributes: Map<String, Any?>

    open fun <T> getAttribute(key: String): T?
    open fun setAttribute(key: String, value: Any? = null)
    open fun setAttribute(key: String, value: ElementAttributeValue?)
    open fun <T> removeAttribute(key: String): T?
}

expect open class HTMLElement internal constructor(tagName: String) : Element {
    val tagName: String
}

expect class HTMLAnchorElement internal constructor(tagName: String) : HTMLElement
expect class HTMLImageElement internal constructor(tagName: String) : HTMLElement
expect class HTMLInputElement internal constructor(tagName: String) : HTMLElement

fun <T : Element> Parent.addElement(
        elementClass: KClass<T>,
        create: (String) -> T,
        tagName: String,
        cssClass: CSSClassRule? = null,
        block: (T.() -> Unit)? = null
): T {
    val ctx = context!!
    val prevState = ctx.nodeState
    ctx.nodeState = NodeState()
    val index = prevState.childIndex++
    var node = children.getOrNull(index)
    val tagNameUpper = tagName.toUpperCase()

    if (node == null || node::class != elementClass || (node is HTMLElement && node.tagName != tagNameUpper)) {
        // TODO: is replaceChild faster than removeChild + appendChild in DOM?
        if (node != null) {
            notifyWillUnmount(node)
            removeChildAt(index)
        }

        node = create(tagNameUpper)
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
        noinline create: (String) -> T,
        tagName: String,
        cssClass: CSSClassRule? = null,
        noinline block: (T.() -> Unit)? = null
): T =
        addElement(T::class, create, tagName, cssClass, block)

fun Parent.div(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(::HTMLElement, "div", cssClass, block)

fun Parent.span(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(::HTMLElement, "span", cssClass, block)

fun Parent.header(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(::HTMLElement, "header", cssClass, block)

fun Parent.footer(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(::HTMLElement, "footer", cssClass, block)

fun Parent.main(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(::HTMLElement, "main", cssClass, block)

fun Parent.section(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(::HTMLElement, "section", cssClass, block)

fun Parent.a(cssClass: CSSClassRule? = null, block: (HTMLAnchorElement.() -> Unit)? = null): HTMLAnchorElement =
        addElement(::HTMLAnchorElement, "a", cssClass, block)

fun Parent.strong(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(::HTMLElement, "strong", cssClass, block)

fun Parent.em(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(::HTMLElement, "em", cssClass, block)

fun Parent.h1(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(::HTMLElement, "h1", cssClass, block)

fun Parent.h2(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(::HTMLElement, "h2", cssClass, block)

fun Parent.h3(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(::HTMLElement, "h3", cssClass, block)

fun Parent.h4(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(::HTMLElement, "h4", cssClass, block)

fun Parent.h5(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(::HTMLElement, "h5", cssClass, block)

fun Parent.h6(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(::HTMLElement, "h6", cssClass, block)

fun Parent.img(cssClass: CSSClassRule? = null, block: (HTMLImageElement.() -> Unit)? = null): HTMLImageElement =
        addElement(::HTMLImageElement, "img", cssClass, block)

fun Parent.table(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(::HTMLElement, "table", cssClass, block)

fun Parent.thead(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(::HTMLElement, "thead", cssClass, block)

fun Parent.tbody(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(::HTMLElement, "tbody", cssClass, block)

fun Parent.tfoot(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(::HTMLElement, "tfoot", cssClass, block)

fun Parent.tr(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(::HTMLElement, "tr", cssClass, block)

fun Parent.td(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(::HTMLElement, "td", cssClass, block)

fun Parent.th(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(::HTMLElement, "th", cssClass, block)

fun Parent.input(cssClass: CSSClassRule? = null, block: (HTMLInputElement.() -> Unit)? = null): HTMLInputElement =
        addElement(::HTMLInputElement, "input", cssClass, block)

fun Parent.label(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(::HTMLElement, "label", cssClass, block)

var Element.className: String
    get() = getAttribute("class") ?: ""
    set(value) {
        setAttribute("class", value)
    }

fun Element.classList(classes: List<CSSClassRule>) {
    className = classes.joinToString(" ") { it.selector.uniqueName }
}

fun Element.classList(vararg classes: CSSClassRule) {
    className = classes.joinToString(" ") { it.selector.uniqueName }
}

var HTMLElement.title: String?
    get() = getAttribute("title")
    set(value) {
        if (value != null) setAttribute("title", value)
        else removeAttribute<Any>("title")
    }

var HTMLElement.hidden: Boolean
    get() = getAttribute("hidden") ?: false
    set(value) {
        if (value) setAttribute("hidden")
        else removeAttribute<Any>("hidden")
    }

var HTMLElement.tabIndex: Int?
    get() = getAttribute("tabIndex")
    set(value) {
        if (value != null) setAttribute("tabIndex", value)
        else removeAttribute<Any>("tabIndex")
    }

fun HTMLElement.style(block: InlineCSSStyleDeclaration.() -> Unit) {
    val decl = InlineCSSStyleDeclaration()
    decl.invoke(block)
    setAttribute("style", decl)
}

var HTMLAnchorElement.href: String?
    get() = getAttribute("href")
    set(value) {
        if (value != null) setAttribute("href", value)
        else removeAttribute<Any>("href")
    }

var HTMLImageElement.src: String?
    get() = getAttribute("src")
    set(value) {
        if (value != null) setAttribute("src", value)
        else removeAttribute<Any>("src")
    }

var HTMLImageElement.width: Int?
    get() = getAttribute("width")
    set(value) {
        if (value != null) setAttribute("width", value)
        else removeAttribute<Any>("width")
    }

var HTMLImageElement.height: Int?
    get() = getAttribute("height")
    set(value) {
        if (value != null) setAttribute("height", value)
        else removeAttribute<Any>("height")
    }

var HTMLInputElement.checked: Boolean
    get() = getAttribute("checked") ?: false
    set(value) {
        if (value) setAttribute("checked")
        else removeAttribute<Any>("checked")
    }

var HTMLInputElement.type: String?
    get() = getAttribute("type")
    set(value) {
        if (value != null) setAttribute("type", value)
        else removeAttribute<Any>("type")
    }
