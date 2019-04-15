package magneton.style

class StyleSheetCSSStyleDeclaration(val rule: CSSRule) : CSSStyleDeclaration() {
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
