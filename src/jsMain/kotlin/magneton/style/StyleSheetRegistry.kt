package magneton.style

import kotlin.browser.document
import kotlin.dom.appendText
import org.w3c.dom.Node as DomNode

actual class StyleSheetRegistry {
    private val internalSheets: MutableSet<StyleSheet> = mutableSetOf()
    actual val sheets: Set<StyleSheet> = internalSheets

    private var domNode: DomNode? = null
    private val styleNodes = mutableListOf<DomNode>()

    actual fun register(styleSheet: StyleSheet) {
        val added = internalSheets.add(styleSheet)

        if (added && domNode != null) {
            render(styleSheet)
        }
    }

    fun appendToDom(domNode: DomNode) {
        if (this.domNode == null) {
            this.domNode = domNode
            sheets.forEach(::render)
        }
    }

    fun removeFromDom() {
        styleNodes.forEach {
            this.domNode?.removeChild(it)
        }
    }

    private fun render(styleSheet: StyleSheet) {
        val style = document.createElement("style")
        style.appendText(styleSheet.toCss())
        styleNodes.add(style)
        domNode!!.appendChild(style)
    }
}
