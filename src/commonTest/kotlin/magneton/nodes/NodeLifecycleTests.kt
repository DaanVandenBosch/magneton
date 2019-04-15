package magneton.nodes

import magneton.observable.observable
import magneton.render
import kotlin.test.Test
import kotlin.test.assertEquals

class NodeLifecycleTests {
    class InnerCmp(private val outerCmp: OuterCmp) : Component() {
        override fun render(): Node {
            outerCmp.events.add("render")
            return div {}
        }

        override fun didMount() {
            outerCmp.events.add("mount")
        }

        override fun willUnmount() {
            outerCmp.events.add("unmount")
        }
    }

    class OuterCmp : Component() {
        var addCmps by observable(true)
        var events = mutableListOf<String>()

        override fun render() = div {
            if (this@OuterCmp.addCmps) {
                component { InnerCmp(this@OuterCmp) }
                span {
                    component { InnerCmp(this@OuterCmp) }
                    span {
                        component { InnerCmp(this@OuterCmp) }
                    }
                }
            }
        }
    }

    @Test
    fun lifecycle_methods_should_be_called_at_the_right_time() {
        val cmp = OuterCmp()
        val app = render(cmp)

        assertEquals(6, cmp.events.size)
        for (i in 0..2)
            assertEquals("render", cmp.events[i], "event $i should be render")
        for (i in 3..5)
            assertEquals("mount", cmp.events[i], "event $i should be mount")

        app.stop()

        assertEquals(9, cmp.events.size)
        for (i in 6..8)
            assertEquals("unmount", cmp.events[i], "event $i should be unmount")
    }

    @Test
    fun removed_nodes_should_be_unmounted() {
        val cmp = OuterCmp()
        render(cmp)

        assertEquals(6, cmp.events.size)
        for (i in 0..2)
            assertEquals("render", cmp.events[i], "event $i should be render")
        for (i in 3..5)
            assertEquals("mount", cmp.events[i], "event $i should be mount")

        cmp.addCmps = false

        assertEquals(9, cmp.events.size)
        for (i in 6..8)
            assertEquals("unmount", cmp.events[i], "event $i should be unmount")
    }

    @Test
    fun lifecycle_methods_should_not_be_called_on_removed_nodes() {
        val cmp = OuterCmp()
        val app = render(cmp)

        assertEquals(6, cmp.events.size)
        for (i in 0..2)
            assertEquals("render", cmp.events[i], "event $i should be render")
        for (i in 3..5)
            assertEquals("mount", cmp.events[i], "event $i should be mount")

        cmp.addCmps = false

        assertEquals(9, cmp.events.size)
        for (i in 6..8)
            assertEquals("unmount", cmp.events[i], "event $i should be unmount")

        // This won't trigger any inner component unmounts.
        app.stop()

        assertEquals(9, cmp.events.size)
    }
}
