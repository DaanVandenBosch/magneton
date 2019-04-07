package magneton.css

class StyleSheetCssDeclaration(val rule: CssRule) : CssDeclaration() {
    operator fun invoke(block: StyleSheetCssDeclaration.() -> Unit) {
        try {
            doUpdateCss = false
            block(this)
        } finally {
            doUpdateCss = true
            updateCss()
        }
    }
}
