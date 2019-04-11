package magneton.nodes

import org.w3c.dom.get
import kotlin.browser.document
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RenderingTests {
    @Test
    fun render() {
        val cmp = TestComponent()
        render(cmp)
        val domNode = cmp.domNode

        assertNotNull(domNode)
        assertEquals("DIV", domNode.nodeName)
        assertEquals(1, domNode.childNodes.length)
        assertEquals("DIV", domNode.childNodes[0]!!.nodeName)
    }

    @Test
    fun render_to_dom() {
        val cmp = TestComponent()
        val document = document.createDocumentFragment()
        renderToDom(document, cmp)

        assertEquals(2, document.querySelectorAll("div").length)
    }

    private class TestComponent : Component() {
        override fun render() = div {
            div { }
        }
    }
}
