package magneton.style

import magneton.nodes.Component
import magneton.nodes.HTMLElement
import magneton.nodes.div
import magneton.nodes.style
import magneton.render
import kotlin.test.Test
import kotlin.test.assertEquals

class StyleTests {
    @Test
    fun htmlElementsShouldBeStylable() {
        val cmp = object : Component() {
            override fun render() = div {
                style {
                    paddingBottom = 50.em
                    borderRadius = 10.px
                }
            }
        }
        render(cmp)

        val div = cmp.children[0] as HTMLElement
        assertEquals("50em", div.domNode.style.paddingBottom)
        assertEquals("10px", div.domNode.style.borderRadius)
    }
}
