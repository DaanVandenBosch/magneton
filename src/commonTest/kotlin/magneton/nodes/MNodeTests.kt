package magneton.nodes

import magneton.observable.observable
import kotlin.test.*

class MNodeTests {
    @Test
    fun elementsShouldBeAddedCorrectly() {
        class TestComponent : Component() {
            override fun render() = div {
                div {}
                span {
                    div {}
                }
            }
        }

        val cmp = TestComponent()
        render(cmp)

        assertEquals(1, cmp.children.size)
        val div1 = cmp.children[0]
        assertTrue(div1 is MHTMLDivElement)
        assertEquals(2, div1.children.size)
        assertTrue(div1.children[0] is MHTMLDivElement)
        val span = div1.children[1]
        assertTrue(span is MHTMLSpanElement)
        assertEquals(1, span.children.size)
        assertTrue(span.children[0] is MHTMLDivElement)
    }

    @Test
    fun addingMultipleChildNodesToAComponentShouldThrow() {
        val cmp = object : Component() {
            override fun render(): MNode {
                div { }
                return div { }
            }
        }

        assertFailsWith(IllegalStateException::class) {
            render(cmp)
        }
    }

    @Test
    fun elementsShouldOnlyBeReplacedWhenNecessary() {
        var spanInMiddle by observable(true)

        val cmp = object : Component() {
            override fun render() = div {
                div { }
                if (spanInMiddle) span { } else div { }
                div { }
            }
        }
        val handle = render(cmp)

        val node1 = cmp.children[0].children[0]
        val node2 = cmp.children[0].children[1]
        val node3 = cmp.children[0].children[2]

        // Force rerender.
        handle.dispose()
        render(cmp)

        assertSame(node1, cmp.children[0].children[0])
        assertSame(node2, cmp.children[0].children[1])
        assertSame(node3, cmp.children[0].children[2])

        spanInMiddle = false

        assertSame(node1, cmp.children[0].children[0])
        assertNotSame(node2, cmp.children[0].children[1])
        assertSame(node3, cmp.children[0].children[2])
    }

    @Test
    fun componentsShouldBeNestable() {
        class InnerCmp : Component() {
            override fun render() = span {}
        }

        class CenterCmp : Component() {
            override fun render() = div { component(::InnerCmp) }
        }

        class OuterCmp : Component() {
            override fun render() = component(::CenterCmp)
        }

        val cmp = OuterCmp()
        render(cmp)

        assertTrue(cmp.children[0] is CenterCmp)
        assertTrue(cmp.children[0].children[0] is MHTMLDivElement)
        assertTrue(cmp.children[0].children[0].children[0] is InnerCmp)
        assertTrue(cmp.children[0].children[0].children[0].children[0] is MHTMLSpanElement)
    }

    @Test
    fun componentsShouldOnlyBeReplacedWhenNecessary() {
        class Inner1Component : Component() {
            override fun render() = span {}
        }

        class Inner2Component : Component() {
            override fun render() = div {}
        }

        var useInner1 by observable(true)

        val cmp = object : Component() {
            override fun render() = div {
                if (useInner1) component(::Inner1Component)
                else component(::Inner2Component)
            }
        }
        val handle = render(cmp)

        val innerCmp = cmp.children[0].children[0]
        assertTrue(innerCmp is Inner1Component)

        // Force rerender.
        handle.dispose()
        render(cmp)

        assertSame(innerCmp, cmp.children[0].children[0])

        useInner1 = false

        assertTrue(cmp.children[0].children[0] is Inner2Component)
        assertNotSame(innerCmp, cmp.children[0].children[0])
    }
}
