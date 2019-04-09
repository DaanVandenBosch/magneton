package magneton.nodes

import magneton.observable.observable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ComponentTests {
    @Test
    fun test2() {
        val node = div {
            div { }
            span { }
            div { }
            div { }
        }

        assertEquals(4, node.children.size)

        val node1 = node.children[0]
        val node2 = node.children[1]
        val node3 = node.children[2]

        node.update {
            div { }
            span { }
            div { }
        }

        assertEquals(3, node.children.size)
        assertEquals(node1, node.children[0])
        assertEquals(node2, node.children[1])
        assertEquals(node3, node.children[2])

        node.update {
            div { }
            div { }
            div { }
        }

        assertEquals(3, node.children.size)
        assertEquals(node1, node.children[0])
        assertTrue(node.children[1] is MHTMLDivElement)
        assertEquals(node3, node.children[2])
    }

    @Test
    fun test3() {
        class TestComponent : Component() {
            override fun render() = div {
                span { }
            }
        }

        val cmp = TestComponent()
        render(cmp)

        assertEquals(1, cmp.children.size)
        assertTrue(cmp.children[0] is MHTMLDivElement)
        assertEquals(1, cmp.children[0].children.size)
        assertTrue(cmp.children[0].children[0] is MHTMLSpanElement)
    }

    @Test
    fun test4() {
        class Inner1Component : Component() {
            override fun render() = span {}
        }

        class Inner2Component : Component() {
            override fun render() = div {}
        }

        var use1 = true

        class OuterComponent : Component() {
            override fun render() = div {
                component(if (use1) ::Inner1Component else ::Inner2Component)
            }
        }

        val cmp = OuterComponent()
        render(cmp)

        val node1 = cmp.children[0]

        assertTrue(node1 is MHTMLDivElement)
        assertEquals(1, node1.children.size)
        assertTrue(node1.children[0] is Inner1Component)

        use1 = false
        render(cmp)
        val node2 = cmp.children[0]

        assertTrue(node2 is MHTMLDivElement)
        assertEquals(1, node2.children.size)
        assertTrue(node2.children[0] is Inner2Component)
    }

    @Test
    fun test5() {
        var useSpan by observable(true)

        val cmp = object : Component() {
            override fun render() = div {
                if (useSpan) span { }
                else div { }
            }
        }
        render(cmp)

        val node = cmp.children[0]

        assertTrue(node is MHTMLDivElement)
        assertEquals(1, node.children.size)
        assertTrue(node.children[0] is MHTMLSpanElement)

        useSpan = false

        assertTrue(node.children[0] is MHTMLDivElement)
    }
}
