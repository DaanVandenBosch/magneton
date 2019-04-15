package magneton.style

import kotlin.reflect.KProperty

open class StyleSheet {
    private val rules = mutableMapOf<CSSSelector, CSSRule>()

    /**
     * Used to track what the current rule is when nesting CSS rules (e.g. ```div { h1 {} }```).
     */
    internal var currentRule: CSSRule? = null

    val body by cssElement()
    val header by cssElement()
    val footer by cssElement()
    val main by cssElement()
    val section by cssElement()
    val div by cssElement()
    val span by cssElement()
    val h1 by cssElement()
    val h2 by cssElement()
    val h3 by cssElement()
    val h4 by cssElement()
    val h5 by cssElement()
    val h6 by cssElement()
    val input by cssElement()

    val hover by cssPseudoClass()
    val active by cssPseudoClass()
    val checked by cssPseudoClass()

    fun cssElement() = CssElementFactory
    fun cssClass() = CssClassFactory
    fun cssPseudoClass() = CssPseudoClassFactory

    fun StyleSheetCSSStyleDeclaration.and(rule: CSSRule, block: StyleSheetCSSStyleDeclaration.() -> Unit) {
        val newRule = getOrPutRule(CSSSelector.Union(this.rule.selector, rule.selector)) {
            CSSRule(this@StyleSheet, it)
        }

        newRule.declaration.invoke(block)
    }

    fun StyleSheetCSSStyleDeclaration.sibling(rule: CSSRule, block: StyleSheetCSSStyleDeclaration.() -> Unit) {
        val newRule = getOrPutRule(CSSSelector.Sibling(this.rule.selector, rule.selector)) {
            CSSRule(this@StyleSheet, it)
        }

        newRule.declaration.invoke(block)
    }

    fun StyleSheetCSSStyleDeclaration.adjacentSibling(rule: CSSRule, block: StyleSheetCSSStyleDeclaration.() -> Unit) {
        val newRule = getOrPutRule(CSSSelector.AdjacentSibling(this.rule.selector, rule.selector)) {
            CSSRule(this@StyleSheet, it)
        }

        newRule.declaration.invoke(block)
    }

    fun StyleSheetCSSStyleDeclaration.child(rule: CSSRule, block: StyleSheetCSSStyleDeclaration.() -> Unit) {
        val newRule = getOrPutRule(CSSSelector.Child(this.rule.selector, rule.selector)) {
            CSSRule(this@StyleSheet, it)
        }

        newRule.declaration.invoke(block)
    }

    fun StyleSheetCSSStyleDeclaration.descendant(rule: CSSRule, block: StyleSheetCSSStyleDeclaration.() -> Unit) {
        val newRule = getOrPutRule(CSSSelector.Descendant(this.rule.selector, rule.selector)) {
            CSSRule(this@StyleSheet, it)
        }

        newRule.declaration.invoke(block)
    }

    fun toCss(): String {
        val sb = StringBuilder()

        for (rule in rules.values) {
            if (rule.declaration.properties.isNotEmpty()) {
                rule.toCss(sb)
                sb.append('\n')
            }
        }

        return sb.toString()
    }

    object CssElementFactory {
        operator fun provideDelegate(thisRef: StyleSheet, property: KProperty<*>): CSSElementRule {
            return thisRef.getOrPutRule(CSSSelector.Element(property.name)) {
                CSSElementRule(thisRef, it)
            }
        }
    }

    object CssClassFactory {
        operator fun provideDelegate(thisRef: StyleSheet, property: KProperty<*>): CSSClassRule =
                thisRef.getOrPutRule(CSSSelector.Class(property.name)) {
                    CSSClassRule(thisRef, it)
                }
    }

    object CssPseudoClassFactory {
        operator fun provideDelegate(thisRef: StyleSheet, property: KProperty<*>): CSSPseudoClassRule =
                thisRef.getOrPutRule(CSSSelector.PseudoClass(property.name)) {
                    CSSPseudoClassRule(thisRef, it)
                }
    }

    @Suppress("UNCHECKED_CAST")
    internal fun <S : CSSSelector, R : CSSRule> getOrPutRule(
            selector: S,
            create: (S) -> R
    ): R =
            rules.getOrPut(selector) { create(selector) } as R
}
