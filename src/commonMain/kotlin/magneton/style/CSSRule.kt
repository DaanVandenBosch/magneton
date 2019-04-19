package magneton.style

import kotlin.reflect.KProperty

open class CSSRule(
        internal val styleSheet: StyleSheet,
        open val selector: CSSSelector
) {
    val declaration by lazy { StyleSheetCSSStyleDeclaration(this) }

    operator fun invoke(block: StyleSheetCSSStyleDeclaration.() -> Unit) {
        val newRule = if (styleSheet.currentRule == null) {
            this
        } else {
            styleSheet.getOrPutRule(
                    CSSSelector.Descendant(styleSheet.currentRule!!.selector, selector)
            )
        }
        styleSheet.invokeRuleDeclaration(newRule, block)
    }

    fun toCss(sb: StringBuilder) {
        sb.append(selector.css).append(" {\n")
        sb.append(declaration.css)
        sb.append("}")
    }
}

class CSSElementRule(
        styleSheet: StyleSheet,
        override val selector: CSSSelector.Element
) : CSSRule(styleSheet, selector) {
    /**
     * Dummy operator to get [CSSRule] factories in [StyleSheet] to work.
     */
    operator fun getValue(thisRef: StyleSheet, property: KProperty<*>): CSSElementRule = this
}

class CSSClassRule(
        styleSheet: StyleSheet,
        override val selector: CSSSelector.Class
) : CSSRule(styleSheet, selector) {
    /**
     * Dummy operator to get [CSSRule] factories in [StyleSheet] to work.
     */
    operator fun getValue(thisRef: StyleSheet, property: KProperty<*>): CSSClassRule = this
}

class CSSPseudoClassRule(
        styleSheet: StyleSheet,
        override val selector: CSSSelector.PseudoClass
) : CSSRule(styleSheet, selector) {
    /**
     * Dummy operator to get [CSSRule] factories in [StyleSheet] to work.
     */
    operator fun getValue(thisRef: StyleSheet, property: KProperty<*>): CSSPseudoClassRule = this
}
