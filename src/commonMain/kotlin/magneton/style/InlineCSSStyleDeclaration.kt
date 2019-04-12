package magneton.style

import magneton.nodes.ElementAttributeValue

class InlineCSSStyleDeclaration : CSSStyleDeclaration(), ElementAttributeValue {
    operator fun invoke(block: InlineCSSStyleDeclaration.() -> Unit) {
        try {
            doUpdateCss = false
            block(this)
        } finally {
            doUpdateCss = true
            updateCss()
        }
    }

    override fun toStringValue(): String = css
}
