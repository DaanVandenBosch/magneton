package magneton.css

import kotlin.reflect.KProperty

open class CssRule(
        private val styleSheet: StyleSheet,
        open val selector: CssSelector
) {
    val declaration by lazy { StyleSheetCssDeclaration(this) }

    operator fun invoke(block: StyleSheetCssDeclaration.() -> Unit) {
        val prevRule = styleSheet.currentRule
        val newRule = if (prevRule == null) {
            this
        } else {
            styleSheet.getOrPutRule(
                    CssSelector.Descendant(prevRule.selector, selector)
            ) { CssRule(styleSheet, it) }
        }
        styleSheet.currentRule = newRule
        newRule.declaration.invoke(block)
        styleSheet.currentRule = prevRule
    }

    fun toCss(sb: StringBuilder) {
        sb.append(selector.css).append(" {\n")
        sb.append(declaration.css)
        sb.append("}")
    }
}

class CssElementRule(
        styleSheet: StyleSheet,
        override val selector: CssSelector.Element
) : CssRule(styleSheet, selector) {
    /**
     * Dummy operator to get [CssRule] factories in [StyleSheet] to work.
     */
    operator fun getValue(thisRef: StyleSheet, property: KProperty<*>): CssElementRule = this
}

class CssClassRule(
        styleSheet: StyleSheet,
        override val selector: CssSelector.Class
) : CssRule(styleSheet, selector) {
    /**
     * Dummy operator to get [CssRule] factories in [StyleSheet] to work.
     */
    operator fun getValue(thisRef: StyleSheet, property: KProperty<*>): CssClassRule = this
}

class CssPseudoClassRule(
        styleSheet: StyleSheet,
        override val selector: CssSelector.PseudoClass
) : CssRule(styleSheet, selector) {
    /**
     * Dummy operator to get [CssRule] factories in [StyleSheet] to work.
     */
    operator fun getValue(thisRef: StyleSheet, property: KProperty<*>): CssPseudoClassRule = this
}
