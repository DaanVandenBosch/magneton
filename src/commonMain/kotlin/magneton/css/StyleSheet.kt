package magneton.css

import kotlin.reflect.KProperty

open class StyleSheet {
    companion object {
        val cssClasses = mutableMapOf<String, CssSelector.Class>()
    }

    private val rules = mutableMapOf<CssSelector, CssRule>()

    /**
     * Used to track what the current rule is when nesting CSS rules (e.g. ```div { h1 {} }```).
     */
    internal var currentRule: CssRule? = null

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

    fun StyleSheetCssDeclaration.and(rule: CssRule, block: StyleSheetCssDeclaration.() -> Unit) {
        val newRule = getOrPutRule(CssSelector.Union(this.rule.selector, rule.selector)) {
            CssRule(this@StyleSheet, it)
        }

        newRule.declaration.invoke(block)
    }

    fun StyleSheetCssDeclaration.sibling(rule: CssRule, block: StyleSheetCssDeclaration.() -> Unit) {
        val newRule = getOrPutRule(CssSelector.Sibling(this.rule.selector, rule.selector)) {
            CssRule(this@StyleSheet, it)
        }

        newRule.declaration.invoke(block)
    }

    fun StyleSheetCssDeclaration.adjacentSibling(rule: CssRule, block: StyleSheetCssDeclaration.() -> Unit) {
        val newRule = getOrPutRule(CssSelector.AdjacentSibling(this.rule.selector, rule.selector)) {
            CssRule(this@StyleSheet, it)
        }

        newRule.declaration.invoke(block)
    }

    fun StyleSheetCssDeclaration.child(rule: CssRule, block: StyleSheetCssDeclaration.() -> Unit) {
        val newRule = getOrPutRule(CssSelector.Child(this.rule.selector, rule.selector)) {
            CssRule(this@StyleSheet, it)
        }

        newRule.declaration.invoke(block)
    }

    fun StyleSheetCssDeclaration.descendant(rule: CssRule, block: StyleSheetCssDeclaration.() -> Unit) {
        val newRule = getOrPutRule(CssSelector.Descendant(this.rule.selector, rule.selector)) {
            CssRule(this@StyleSheet, it)
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
        operator fun provideDelegate(thisRef: StyleSheet, property: KProperty<*>): CssElementRule {
            return thisRef.getOrPutRule(CssSelector.Element(property.name)) {
                CssElementRule(thisRef, it)
            }
        }
    }

    object CssClassFactory {
        operator fun provideDelegate(thisRef: StyleSheet, property: KProperty<*>): CssClassRule =
                thisRef.getOrPutRule(CssSelector.Class(property.name)) {
                    CssClassRule(thisRef, it)
                }
    }

    object CssPseudoClassFactory {
        operator fun provideDelegate(thisRef: StyleSheet, property: KProperty<*>): CssPseudoClassRule =
                thisRef.getOrPutRule(CssSelector.PseudoClass(property.name)) {
                    CssPseudoClassRule(thisRef, it)
                }
    }

    @Suppress("UNCHECKED_CAST")
    internal fun <S : CssSelector, R : CssRule> getOrPutRule(selector: S, create: (S) -> R): R =
            rules.getOrPut(selector) {
                if (selector is CssSelector.Class) {
                    cssClasses[selector.uniqueName] = selector
                }

                create(selector)
            } as R
}
