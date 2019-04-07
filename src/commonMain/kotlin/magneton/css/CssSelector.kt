package magneton.css

sealed class CssSelector {
    abstract val css: String

    abstract class Simple(name: String) : CssSelector() {
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

    class Union(val selector1: CssSelector, val selector2: CssSelector) : CssSelector() {
        override val css = selector1.css + selector2.css
    }

    class Child(val parent: CssSelector, val child: CssSelector) : CssSelector() {
        override val css = "${parent.css} > ${child.css}"
    }

    class Descendant(val parent: CssSelector, val descendant: CssSelector) : CssSelector() {
        override val css = "${parent.css} ${descendant.css}"
    }

    class Sibling(val preceding: CssSelector, val sibling: CssSelector) : CssSelector() {
        override val css = "${preceding.css} ~ ${sibling.css}"
    }

    class AdjacentSibling(val preceding: CssSelector, val sibling: CssSelector) : CssSelector() {
        override val css = "${preceding.css} + ${sibling.css}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as CssSelector
        if (css != other.css) return false
        return true
    }

    override fun hashCode(): Int {
        return css.hashCode()
    }

    companion object {
        private var i = 0
    }
}
