package magneton.style

import kotlin.reflect.KProperty

open class StyleSheet {
    companion object {
        val cssClasses = mutableMapOf<String, CSSSelector.Class>()
    }

    private val rules = mutableMapOf<CSSSelector, StyleRule>()

    /**
     * Used to track what the current rule is when nesting CSS rules (e.g. ```div { h1 {} }```).
     */
    internal var currentRule: StyleRule? = null

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

    fun StyleSheetCSSStyleDeclaration.and(rule: StyleRule, block: StyleSheetCSSStyleDeclaration.() -> Unit) {
        val newRule = getOrPutRule(CSSSelector.Union(this.rule.selector, rule.selector)) {
            StyleRule(this@StyleSheet, it)
        }

        newRule.declaration.invoke(block)
    }

    fun StyleSheetCSSStyleDeclaration.sibling(rule: StyleRule, block: StyleSheetCSSStyleDeclaration.() -> Unit) {
        val newRule = getOrPutRule(CSSSelector.Sibling(this.rule.selector, rule.selector)) {
            StyleRule(this@StyleSheet, it)
        }

        newRule.declaration.invoke(block)
    }

    fun StyleSheetCSSStyleDeclaration.adjacentSibling(rule: StyleRule, block: StyleSheetCSSStyleDeclaration.() -> Unit) {
        val newRule = getOrPutRule(CSSSelector.AdjacentSibling(this.rule.selector, rule.selector)) {
            StyleRule(this@StyleSheet, it)
        }

        newRule.declaration.invoke(block)
    }

    fun StyleSheetCSSStyleDeclaration.child(rule: StyleRule, block: StyleSheetCSSStyleDeclaration.() -> Unit) {
        val newRule = getOrPutRule(CSSSelector.Child(this.rule.selector, rule.selector)) {
            StyleRule(this@StyleSheet, it)
        }

        newRule.declaration.invoke(block)
    }

    fun StyleSheetCSSStyleDeclaration.descendant(rule: StyleRule, block: StyleSheetCSSStyleDeclaration.() -> Unit) {
        val newRule = getOrPutRule(CSSSelector.Descendant(this.rule.selector, rule.selector)) {
            StyleRule(this@StyleSheet, it)
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
        operator fun provideDelegate(thisRef: StyleSheet, property: KProperty<*>): StyleElementRule {
            return thisRef.getOrPutRule(CSSSelector.Element(property.name)) {
                StyleElementRule(thisRef, it)
            }
        }
    }

    object CssClassFactory {
        operator fun provideDelegate(thisRef: StyleSheet, property: KProperty<*>): StyleClassRule =
                thisRef.getOrPutRule(CSSSelector.Class(property.name)) {
                    StyleClassRule(thisRef, it)
                }
    }

    object CssPseudoClassFactory {
        operator fun provideDelegate(thisRef: StyleSheet, property: KProperty<*>): StylePseudoClassRule =
                thisRef.getOrPutRule(CSSSelector.PseudoClass(property.name)) {
                    StylePseudoClassRule(thisRef, it)
                }
    }

    @Suppress("UNCHECKED_CAST")
    internal fun <S : CSSSelector, R : StyleRule> getOrPutRule(selector: S, create: (S) -> R): R =
            rules.getOrPut(selector) {
                if (selector is CSSSelector.Class) {
                    cssClasses[selector.uniqueName] = selector
                }

                create(selector)
            } as R
}
