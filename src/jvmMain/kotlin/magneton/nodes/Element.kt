package magneton.nodes

actual abstract class Element : Node() {
    private val _attributes: MutableMap<String, Any?> = mutableMapOf()
    actual val attributes: Map<String, Any?> = _attributes

    @Suppress("UNCHECKED_CAST")
    actual open fun <T> getAttribute(key: String): T? =
            attributes[key] as T?

    actual open fun setAttribute(key: String, value: Any?) {
        _attributes[key] = value
        stack.peek().setAttributes.add(key)
    }

    @Suppress("UNCHECKED_CAST")
    actual open fun <T> removeAttribute(key: String): T? =
            _attributes.remove(key) as T?
}

actual class HTMLDivElement : HTMLElement()
actual class HTMLSpanElement : HTMLElement()
