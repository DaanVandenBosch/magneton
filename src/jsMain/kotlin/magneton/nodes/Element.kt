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
        if (value != attributes[key]) {
            if (value == Unit) {
                domNode?.setAttribute(key, "")
            } else {
                domNode?.setAttribute(key, value.toString())
            }

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
