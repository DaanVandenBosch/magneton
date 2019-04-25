package magneton.style

import magneton.unsafeCast

sealed class CSSSelector {
    abstract val css: String

    abstract class Simple(name: String) : CSSSelector() {
        val uniqueName = "$name-${(i++).toString(16)}"
    }

    class Element(val name: String) : Simple(name) {
        override val css = name
    }

    class Class(val name: String) : Simple(name) {
        override val css = ".$uniqueName"
    }

    class PseudoClass(val name: String) : Simple(name) {
        override val css = ":$name"
    }

    class Intersection(val selector1: CSSSelector, val selector2: CSSSelector) : CSSSelector() {
        override val css = selector1.css + selector2.css
    }

    class Child(val parent: CSSSelector, val child: CSSSelector) : CSSSelector() {
        override val css = "${parent.css} > ${child.css}"
    }

    class Descendant(val parent: CSSSelector, val descendant: CSSSelector) : CSSSelector() {
        override val css = "${parent.css} ${descendant.css}"
    }

    class Sibling(val preceding: CSSSelector, val sibling: CSSSelector) : CSSSelector() {
        override val css = "${preceding.css} ~ ${sibling.css}"
    }

    class AdjacentSibling(val preceding: CSSSelector, val sibling: CSSSelector) : CSSSelector() {
        override val css = "${preceding.css} + ${sibling.css}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        if (css != other.unsafeCast<CSSSelector>().css) return false
        return true
    }

    override fun hashCode(): Int {
        return css.hashCode()
    }

    companion object {
        private var i = 0
    }
}
