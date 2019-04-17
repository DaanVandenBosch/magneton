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

    private val _attributes: MutableMap<String, Any?> = mutableMapOf()
    actual val attributes: Map<String, Any?> = _attributes

    @Suppress("UNCHECKED_CAST")
    actual open fun <T> getAttribute(key: String): T? =
            attributes[key] as T?

    actual open fun setAttribute(key: String, value: Any?) {
        setAttribute(key, value?.toString() ?: "")
    }

    actual open fun setAttribute(key: String, value: ElementAttributeValue?) {
        setAttribute(key, value?.toStringValue() ?: "")
    }

    private fun setAttribute(key: String, value: String) {
        if (value != attributes[key]) {
            domNode.setAttribute(key, value)
            _attributes[key] = value
        }

        context!!.nodeState.updatedAttributes.add(key)
    }

    @Suppress("UNCHECKED_CAST")
    actual open fun <T> removeAttribute(key: String): T? {
        domNode.removeAttribute(key)
        return _attributes.remove(key) as T?
    }
}

actual open class HTMLElement actual constructor(actual val tagName: String) : Element() {
    override val domNode = document.createElement(tagName) as DomHTMLElement
}

actual class HTMLAnchorElement actual constructor(tagName: String) : HTMLElement(tagName) {
    override val domNode = super.domNode as DomHTMLAnchorElement
}

actual class HTMLImageElement actual constructor(tagName: String) : HTMLElement(tagName) {
    override val domNode = super.domNode as DomHTMLImageElement
}

actual class HTMLInputElement actual constructor(tagName: String) : HTMLElement(tagName) {
    override val domNode = super.domNode as DomHTMLInputElement
}
