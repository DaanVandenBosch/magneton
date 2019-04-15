package magneton.style

import kotlin.reflect.KProperty

open class CSSRule(
        internal val styleSheet: StyleSheet,
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
            ) { CSSRule(styleSheet, it) }
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
