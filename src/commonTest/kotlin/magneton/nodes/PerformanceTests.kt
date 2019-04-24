package magneton.nodes

import magneton.Context
import magneton.testutil.Performance
import kotlin.test.Test

class PerformanceTests {
    // Test run by dvb on 2019-04-24 Chrome, Time taken: 294ms
    // Test run by dvb on 2019-04-24 Firefox, Time taken: 1628ms
    @Test
    fun render_component_with_10_000_child_elements() {
        val cmp = object : Component() {
            override fun render() = div {
                for (i in 1..10_000) {
                    span { }
                }
            }
        }
        cmp.context = Context()

        val time = Performance.measureTime {
            for (i in 1..100) {
                cmp.context!!.nodeState = NodeState()
                cmp.render()
            }
        }

        println("Render component with 10.000 child elements 100 times.\nTime taken: ${time}ms")
    }

    // Test run by dvb on 2019-04-24 Chrome, Time taken: 242ms
    // Test run by dvb on 2019-04-24 Firefox, Time taken: 1042ms
    @Test
    fun render_component_with_10_000_child_components() {
        class Inner : Component() {
            override fun render() = span {}
        }

        val cmp = object : Component() {
            override fun render() = div {
                for (i in 1..10_000) {
                    component(::Inner)
                }
            }
        }
        cmp.context = Context()

        val time = Performance.measureTime {
            for (i in 1..100) {
                cmp.context!!.nodeState = NodeState()
                cmp.render()
            }
        }

        println("Render component with 10.000 child components 100 times.\nTime taken: ${time}ms")
    }
}
