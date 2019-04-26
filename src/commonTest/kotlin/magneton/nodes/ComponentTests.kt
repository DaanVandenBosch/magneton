package magneton.nodes

import magneton.observable.observable
import magneton.render
import kotlin.test.*

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

    @Test
    fun component_reactions_should_be_correctly_disposed() {
        // Wrap variables in an object to work around compiler bug.
        val vars = object {
            var x by observable("a")
            var inner1Renders = 0
            var inner2Renders = 0
        }

        class Inner1 : Component() {
            override fun render() = span { text(vars.x); vars.inner1Renders++ }
        }

        class Inner2 : Component() {
            override fun render() = span { text(vars.x); vars.inner2Renders++ }
        }

        var useInner by observable(1)

        val cmp = object : Component() {
            override fun render() = div {
                if (useInner == 1) component(::Inner1)
                else if (useInner == 2) component(::Inner2)
            }
        }
        render(cmp)

        assertEquals(1, vars.inner1Renders)
        assertEquals(0, vars.inner2Renders)

        useInner = 2

        assertEquals(1, vars.inner1Renders)
        assertEquals(1, vars.inner2Renders)

        vars.x = "b"

        assertEquals(1, vars.inner1Renders)
        assertEquals(2, vars.inner2Renders)

        useInner = -1
        vars.x = "c"

        assertEquals(1, vars.inner1Renders)
        assertEquals(2, vars.inner2Renders)
    }

    @Test
    fun component_reactions_should_be_correctly_disposed_when_replaced_with_element() {
        // Wrap variables in an object to work around compiler bug.
        val vars = object {
            var x by observable("a")
            var innerRenders = 0
        }

        class Inner : Component() {
            override fun render() = span { text(vars.x); vars.innerRenders++ }
        }

        var useInner by observable(true)

        val cmp = object : Component() {
            override fun render() = div {
                if (useInner) component(::Inner)
                else span()
            }
        }
        render(cmp)

        assertEquals(1, vars.innerRenders)

        useInner = false
        vars.x = "b"

        assertEquals(1, vars.innerRenders)
    }
}
