package magneton.nodes

import org.w3c.dom.get
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RenderingTests {
    @Test
    fun render() {
        val cmp = TestComponent()
        val domNode = cmp.domNode

        assertNotNull(domNode)
        assertTrue(domNode is org.w3c.dom.HTMLDivElement)
        assertEquals(1, domNode.childNodes.length)
        assertTrue(domNode.childNodes[0] is org.w3c.dom.HTMLDivElement)
    }

    class TestComponent : Component() {
        override val root = div {
            div { }
        }
    }
}
