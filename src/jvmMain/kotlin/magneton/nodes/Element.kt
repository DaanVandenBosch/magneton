package magneton.nodes

actual abstract class Element : Parent() {
    private val _attributes: MutableMap<String, Any?> = mutableMapOf()
    actual val attributes: Map<String, Any?> = _attributes

    @Suppress("UNCHECKED_CAST")
    actual open fun <T> getAttribute(key: String): T? =
            attributes[key] as T?

    actual open fun setAttribute(key: String, value: Any?) {
        _attributes[key] = value
        context!!.nodeState.updatedAttributes.add(key)
    }

    actual open fun setAttribute(key: String, value: ElementAttributeValue?) {
        setAttribute(key, value as Any?)
    }

    @Suppress("UNCHECKED_CAST")
    actual open fun <T> removeAttribute(key: String): T? =
            _attributes.remove(key) as T?
}

actual open class HTMLElement actual constructor(actual val tagName: String) : Element()

actual class HTMLAnchorElement actual constructor(tagName: String) : HTMLElement(tagName)
actual class HTMLImageElement actual constructor(tagName: String) : HTMLElement(tagName)
actual class HTMLInputElement actual constructor(tagName: String) : HTMLElement(tagName)
