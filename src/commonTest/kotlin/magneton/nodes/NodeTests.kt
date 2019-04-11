package magneton.nodes

import magneton.observable.observable
import kotlin.test.*

class NodeTests {
    @Test
    fun elements_should_be_added_correctly() {
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
        assertTrue(div1 is HTMLDivElement)
        assertEquals(2, div1.children.size)
        assertTrue(div1.children[0] is HTMLDivElement)
        val span = div1.children[1]
        assertTrue(span is HTMLSpanElement)
        assertEquals(1, span.children.size)
        assertTrue(span.children[0] is HTMLDivElement)
    }

    @Test
    fun elements_should_be_removed_when_necessary() {
        var addThird by observable(true)

        class TestComponent : Component() {
            override fun render() = div {
                div {}
                div {}
                if (addThird) div { }
            }
        }

        val cmp = TestComponent()
        render(cmp)

        assertEquals(3, cmp.children[0].children.size)

        addThird = false

        assertEquals(2, cmp.children[0].children.size)
    }

    @Test
    fun adding_multiple_child_nodes_toA_component_should_throw() {
        val cmp = object : Component() {
            override fun render(): Node {
                div { }
                return div { }
            }
        }

        assertFailsWith(IllegalStateException::class) {
            render(cmp)
        }
    }

    @Test
    fun elements_should_only_be_replaced_when_necessary() {
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
    fun components_should_be_nestable() {
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
        assertTrue(cmp.children[0].children[0] is HTMLDivElement)
        assertTrue(cmp.children[0].children[0].children[0] is InnerCmp)
        assertTrue(cmp.children[0].children[0].children[0].children[0] is HTMLSpanElement)
    }

    @Test
    fun components_should_only_be_replaced_when_necessary() {
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
