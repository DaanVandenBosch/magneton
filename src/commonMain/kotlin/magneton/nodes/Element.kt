package magneton.nodes

import magneton.style.CSSClassRule
import magneton.style.InlineCSSStyleDeclaration

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

expect open class HTMLElement internal constructor(nodeType: NodeType, tagName: String) : Element {
    override val nodeType: NodeType
    val tagName: String
}

expect class HTMLAnchorElement internal constructor() : HTMLElement
expect class HTMLImageElement internal constructor() : HTMLElement
expect class HTMLInputElement internal constructor() : HTMLElement

private fun <T : Element> Parent.addElement(
        nodeType: NodeType,
        create: (NodeType, String) -> T,
        tagName: String,
        cssClass: CSSClassRule? = null,
        block: (T.() -> Unit)? = null
): T {
    val ctx = context!!
    val prevState = ctx.nodeState
    ctx.nodeState = NodeState()
    val index = prevState.childIndex++
    var node = children.getOrNull(index)

    if (node == null || node.nodeType != nodeType) {
        // TODO: is replaceChild faster than removeChild + appendChild in DOM?
        if (node != null) {
            notifyWillUnmount(node)
            removeChildAt(index)
        }

        node = create(nodeType, tagName.toUpperCase())
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

internal val HTML_ELEMENT_TYPE_DIV = stringToNodeType("magneton.nodes.HTMLElement#DIV")
internal val HTML_ELEMENT_TYPE_SPAN = stringToNodeType("magneton.nodes.HTMLElement#SPAN")
internal val HTML_ELEMENT_TYPE_HEADER = stringToNodeType("magneton.nodes.HTMLElement#HEADER")
internal val HTML_ELEMENT_TYPE_FOOTER = stringToNodeType("magneton.nodes.HTMLElement#FOOTER")
internal val HTML_ELEMENT_TYPE_MAIN = stringToNodeType("magneton.nodes.HTMLElement#MAIN")
internal val HTML_ELEMENT_TYPE_SECTION = stringToNodeType("magneton.nodes.HTMLElement#SECTION")
internal val HTML_ELEMENT_TYPE_A = stringToNodeType("magneton.nodes.HTMLElement#A")
internal val HTML_ELEMENT_TYPE_STRONG = stringToNodeType("magneton.nodes.HTMLElement#STRONG")
internal val HTML_ELEMENT_TYPE_EM = stringToNodeType("magneton.nodes.HTMLElement#EM")
internal val HTML_ELEMENT_TYPE_H1 = stringToNodeType("magneton.nodes.HTMLElement#H1")
internal val HTML_ELEMENT_TYPE_H2 = stringToNodeType("magneton.nodes.HTMLElement#H2")
internal val HTML_ELEMENT_TYPE_H3 = stringToNodeType("magneton.nodes.HTMLElement#H3")
internal val HTML_ELEMENT_TYPE_H4 = stringToNodeType("magneton.nodes.HTMLElement#H4")
internal val HTML_ELEMENT_TYPE_H5 = stringToNodeType("magneton.nodes.HTMLElement#H5")
internal val HTML_ELEMENT_TYPE_H6 = stringToNodeType("magneton.nodes.HTMLElement#H6")
internal val HTML_ELEMENT_TYPE_IMG = stringToNodeType("magneton.nodes.HTMLElement#IMG")
internal val HTML_ELEMENT_TYPE_TABLE = stringToNodeType("magneton.nodes.HTMLElement#TABLE")
internal val HTML_ELEMENT_TYPE_THEAD = stringToNodeType("magneton.nodes.HTMLElement#THEAD")
internal val HTML_ELEMENT_TYPE_TBODY = stringToNodeType("magneton.nodes.HTMLElement#TBODY")
internal val HTML_ELEMENT_TYPE_TFOOT = stringToNodeType("magneton.nodes.HTMLElement#TFOOT")
internal val HTML_ELEMENT_TYPE_TR = stringToNodeType("magneton.nodes.HTMLElement#TR")
internal val HTML_ELEMENT_TYPE_TD = stringToNodeType("magneton.nodes.HTMLElement#TD")
internal val HTML_ELEMENT_TYPE_TH = stringToNodeType("magneton.nodes.HTMLElement#TH")
internal val HTML_ELEMENT_TYPE_INPUT = stringToNodeType("magneton.nodes.HTMLElement#INPUT")
internal val HTML_ELEMENT_TYPE_LABEL = stringToNodeType("magneton.nodes.HTMLElement#LABEL")

fun Parent.div(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(HTML_ELEMENT_TYPE_DIV, ::HTMLElement, "div", cssClass, block)

fun Parent.span(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(HTML_ELEMENT_TYPE_SPAN, ::HTMLElement, "span", cssClass, block)

fun Parent.header(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(HTML_ELEMENT_TYPE_HEADER, ::HTMLElement, "header", cssClass, block)

fun Parent.footer(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(HTML_ELEMENT_TYPE_FOOTER, ::HTMLElement, "footer", cssClass, block)

fun Parent.main(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(HTML_ELEMENT_TYPE_MAIN, ::HTMLElement, "main", cssClass, block)

fun Parent.section(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(HTML_ELEMENT_TYPE_SECTION, ::HTMLElement, "section", cssClass, block)

fun Parent.a(cssClass: CSSClassRule? = null, block: (HTMLAnchorElement.() -> Unit)? = null): HTMLAnchorElement =
        addElement(HTML_ELEMENT_TYPE_A, { _, _ -> HTMLAnchorElement() }, "a", cssClass, block)

fun Parent.strong(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(HTML_ELEMENT_TYPE_STRONG, ::HTMLElement, "strong", cssClass, block)

fun Parent.em(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(HTML_ELEMENT_TYPE_EM, ::HTMLElement, "em", cssClass, block)

fun Parent.h1(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(HTML_ELEMENT_TYPE_H1, ::HTMLElement, "h1", cssClass, block)

fun Parent.h2(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(HTML_ELEMENT_TYPE_H2, ::HTMLElement, "h2", cssClass, block)

fun Parent.h3(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(HTML_ELEMENT_TYPE_H3, ::HTMLElement, "h3", cssClass, block)

fun Parent.h4(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(HTML_ELEMENT_TYPE_H4, ::HTMLElement, "h4", cssClass, block)

fun Parent.h5(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(HTML_ELEMENT_TYPE_H5, ::HTMLElement, "h5", cssClass, block)

fun Parent.h6(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(HTML_ELEMENT_TYPE_H6, ::HTMLElement, "h6", cssClass, block)

fun Parent.img(cssClass: CSSClassRule? = null, block: (HTMLImageElement.() -> Unit)? = null): HTMLImageElement =
        addElement(HTML_ELEMENT_TYPE_IMG, { _, _ -> HTMLImageElement() }, "img", cssClass, block)

fun Parent.table(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(HTML_ELEMENT_TYPE_TABLE, ::HTMLElement, "table", cssClass, block)

fun Parent.thead(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(HTML_ELEMENT_TYPE_THEAD, ::HTMLElement, "thead", cssClass, block)

fun Parent.tbody(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(HTML_ELEMENT_TYPE_TBODY, ::HTMLElement, "tbody", cssClass, block)

fun Parent.tfoot(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(HTML_ELEMENT_TYPE_TFOOT, ::HTMLElement, "tfoot", cssClass, block)

fun Parent.tr(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(HTML_ELEMENT_TYPE_TR, ::HTMLElement, "tr", cssClass, block)

fun Parent.td(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(HTML_ELEMENT_TYPE_TD, ::HTMLElement, "td", cssClass, block)

fun Parent.th(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(HTML_ELEMENT_TYPE_TH, ::HTMLElement, "th", cssClass, block)

fun Parent.input(cssClass: CSSClassRule? = null, block: (HTMLInputElement.() -> Unit)? = null): HTMLInputElement =
        addElement(HTML_ELEMENT_TYPE_INPUT, { _, _ -> HTMLInputElement() }, "input", cssClass, block)

fun Parent.label(cssClass: CSSClassRule? = null, block: (HTMLElement.() -> Unit)? = null): HTMLElement =
        addElement(HTML_ELEMENT_TYPE_LABEL, ::HTMLElement, "label", cssClass, block)

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
