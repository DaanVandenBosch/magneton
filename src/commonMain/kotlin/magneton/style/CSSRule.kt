package magneton.style

import kotlin.reflect.KProperty

open class StyleRule(
        private val styleSheet: StyleSheet,
        open val selector: CSSSelector
) {
    val declaration by lazy { StyleSheetCSSStyleDeclaration(this) }

    operator fun invoke(block: StyleSheetCSSStyleDeclaration.() -> Unit) {
        val prevRule = styleSheet.currentRule
        val newRule = if (prevRule == null) {
            this
        } else {
            styleSheet.getOrPutRule(
                    CSSSelector.Descendant(prevRule.selector, selector)
            ) { StyleRule(styleSheet, it) }
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

class StyleElementRule(
        styleSheet: StyleSheet,
        override val selector: CSSSelector.Element
) : StyleRule(styleSheet, selector) {
    /**
     * Dummy operator to get [StyleRule] factories in [StyleSheet] to work.
     */
    operator fun getValue(thisRef: StyleSheet, property: KProperty<*>): StyleElementRule = this
}

class StyleClassRule(
        styleSheet: StyleSheet,
        override val selector: CSSSelector.Class
) : StyleRule(styleSheet, selector) {
    /**
     * Dummy operator to get [StyleRule] factories in [StyleSheet] to work.
     */
    operator fun getValue(thisRef: StyleSheet, property: KProperty<*>): StyleClassRule = this
}

class StylePseudoClassRule(
        styleSheet: StyleSheet,
        override val selector: CSSSelector.PseudoClass
) : StyleRule(styleSheet, selector) {
    /**
     * Dummy operator to get [StyleRule] factories in [StyleSheet] to work.
     */
    operator fun getValue(thisRef: StyleSheet, property: KProperty<*>): StylePseudoClassRule = this
}
