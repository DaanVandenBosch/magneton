package magneton.style

class StyleSheetCSSStyleDeclaration(val rule: StyleRule) : CSSStyleDeclaration() {
    operator fun invoke(block: StyleSheetCSSStyleDeclaration.() -> Unit) {
        try {
            doUpdateCss = false
            block(this)
        } finally {
            doUpdateCss = true
            updateCss()
        }
    }
}
