package magneton.style

import magneton.nodes.Component
import magneton.nodes.HTMLElement
import magneton.nodes.className
import magneton.nodes.div
import magneton.render
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StyleSheetTests {
    @Test
    fun elements_can_reference_stylesheet_rules() {
        val cmp = TestCmp()
        val app = render(cmp)
        val div = cmp.children[0] as HTMLElement

        assertEquals(div.className, TestCmp.Styles.test.selector.uniqueName)
        assertTrue(TestCmp.Styles in app.context.styleSheetRegistry.sheets)
    }

    class TestCmp : Component() {
        override fun render() = div(Styles.test) { }

        object Styles : StyleSheet() {
            val test by cssClass()
        }
    }
}
