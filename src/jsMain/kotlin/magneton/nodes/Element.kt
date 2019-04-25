package magneton.nodes

import kotlin.browser.document
import org.w3c.dom.Element as DomElement
import org.w3c.dom.HTMLAnchorElement as DomHTMLAnchorElement
import org.w3c.dom.HTMLDivElement as DomHTMLDivElement
import org.w3c.dom.HTMLElement as DomHTMLElement
import org.w3c.dom.HTMLImageElement as DomHTMLImageElement
import org.w3c.dom.HTMLInputElement as DomHTMLInputElement
import org.w3c.dom.HTMLSpanElement as DomHTMLSpanElement

actual abstract class Element : Parent() {
    abstract override val domNode: DomElement

    // TODO: use native map.
    private val _attributes: MutableMap<String, Any?> = mutableMapOf()
    actual val attributes: Map<String, Any?> = _attributes

    actual open fun <T> getAttribute(key: String): T? =
            attributes[key].unsafeCast<T?>()

    actual open fun setAttribute(key: String, value: Any?) {
        setAttribute(key, value?.toString() ?: "")
    }

    actual open fun setAttribute(key: String, value: ElementAttributeValue?) {
        setAttribute(key, value?.toStringValue() ?: "")
    }

    protected open fun setAttribute(key: String, value: String) {
        if (value != attributes[key]) {
            domNode.setAttribute(key, value)
            _attributes[key] = value
        }

        context!!.nodeState.updatedAttributes.add(key)
    }

    actual open fun <T> removeAttribute(key: String): T? {
        domNode.removeAttribute(key)
        return _attributes.remove(key).unsafeCast<T?>()
    }
}

actual open class HTMLElement actual constructor(
        actual override val nodeType: NodeType,
        actual val tagName: String
) : Element() {
    override val domNode = document.createElement(tagName).unsafeCast<DomHTMLElement>()
}

actual class HTMLAnchorElement actual constructor() : HTMLElement(HTML_ELEMENT_TYPE_A, "A") {
    override val domNode = super.domNode.unsafeCast<DomHTMLAnchorElement>()
}

actual class HTMLImageElement actual constructor() : HTMLElement(HTML_ELEMENT_TYPE_IMG, "img") {
    override val domNode = super.domNode.unsafeCast<DomHTMLImageElement>()
}

actual class HTMLInputElement actual constructor() : HTMLElement(HTML_ELEMENT_TYPE_INPUT, "input") {
    override val domNode = super.domNode.unsafeCast<DomHTMLInputElement>()

    override fun setAttribute(key: String, value: String) {
        if (key == "checked") {
            domNode.asDynamic().checked = true
        }

        super.setAttribute(key, value)
    }

    override fun <T> removeAttribute(key: String): T? {
        if (key == "checked") {
            domNode.asDynamic().checked = false
        }

        return super.removeAttribute(key)
    }
}
