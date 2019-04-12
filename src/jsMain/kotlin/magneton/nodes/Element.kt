package magneton.nodes

import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLSpanElement
import kotlin.browser.document

actual abstract class Element : Node() {
    abstract override val domNode: Element?

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
            domNode?.setAttribute(key, value)
            _attributes[key] = value
        }

        NodeState.Global.get()!!.updatedAttributes.add(key)
    }

    @Suppress("UNCHECKED_CAST")
    actual open fun <T> removeAttribute(key: String): T? {
        domNode?.removeAttribute(key)
        return _attributes.remove(key) as T?
    }
}

actual class HTMLDivElement : HTMLElement() {
    override val domNode = document.createElement("div") as HTMLDivElement
}

actual class HTMLSpanElement : HTMLElement() {
    override val domNode = document.createElement("span") as HTMLSpanElement
}
