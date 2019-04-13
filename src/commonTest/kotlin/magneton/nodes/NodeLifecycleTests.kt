package magneton.nodes

import magneton.observable.observable
import kotlin.test.Test
import kotlin.test.assertEquals

class NodeLifecycleTests {
    class InnerCmp(private val outerCmp: OuterCmp) : Component() {
        override fun render(): Node {
            outerCmp.renders++
            return div {}
        }

        override fun didMount() {
            outerCmp.mounts++
        }

        override fun willUnmount() {
            outerCmp.unmounts++
        }
    }

    class OuterCmp : Component() {
        var addCmps by observable(true)
        var renders = 0
        var mounts = 0
        var unmounts = 0

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
        render(cmp)

        assertEquals(3, cmp.renders)
        assertEquals(0, cmp.mounts)
        assertEquals(0, cmp.unmounts)

        notifyDidMount(cmp)

        assertEquals(3, cmp.renders)
        assertEquals(3, cmp.mounts)
        assertEquals(0, cmp.unmounts)

        notifyWillUnmount(cmp)

        assertEquals(3, cmp.renders)
        assertEquals(3, cmp.mounts)
        assertEquals(3, cmp.unmounts)
    }

    @Test
    fun lifecycle_methods_should_not_be_called_on_removed_nodes() {
        val cmp = OuterCmp()
        render(cmp)

        assertEquals(3, cmp.renders)
        assertEquals(0, cmp.mounts)
        assertEquals(0, cmp.unmounts)

        notifyDidMount(cmp)

        assertEquals(3, cmp.renders)
        assertEquals(3, cmp.mounts)
        assertEquals(0, cmp.unmounts)

        cmp.addCmps = false

        assertEquals(3, cmp.renders)
        assertEquals(3, cmp.mounts)
        assertEquals(3, cmp.unmounts)

        // This won't change the number of unmounts.
        notifyWillUnmount(cmp)

        assertEquals(3, cmp.renders)
        assertEquals(3, cmp.mounts)
        assertEquals(3, cmp.unmounts)
    }
}
