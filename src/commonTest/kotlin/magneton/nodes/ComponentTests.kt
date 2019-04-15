package magneton.nodes

import magneton.observable.observable
import magneton.render
import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ComponentTests {
    @Test
    fun an_element_can_have_multiple_component_children() {
        class InnerCmp : Component() {
            override fun render() = span {}
        }

        class OuterCmp : Component() {
            override fun render() = div {
                component(::InnerCmp)
                component(::InnerCmp)
                component(::InnerCmp)
            }
        }

        val cmp = OuterCmp()
        render(cmp)

        val c0 = cmp.children[0] as Parent
        assertTrue(c0.children[0] is InnerCmp)
        assertTrue(c0.children[1] is InnerCmp)
        assertTrue(c0.children[2] is InnerCmp)
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

        val c1 = cmp.children[0] as Parent
        assertTrue(c1 is CenterCmp)
        val c2 = c1.children[0] as Parent
        val c3 = c2.children[0] as Parent
        assertTrue(c3 is InnerCmp)
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
        val app = render(cmp)

        val innerCmp = (cmp.children[0] as Parent).children[0]
        assertTrue(innerCmp is Inner1Component)

        // Force rerender.
        app.stop()
        app.start()

        assertSame(innerCmp, (cmp.children[0] as Parent).children[0])

        useInner1 = false

        assertTrue((cmp.children[0] as Parent).children[0] is Inner2Component)
        assertNotSame(innerCmp, (cmp.children[0] as Parent).children[0])
    }
}
