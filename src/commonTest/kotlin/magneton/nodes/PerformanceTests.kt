package magneton.nodes

import magneton.Context
import magneton.testutil.Performance
import kotlin.test.Test

class PerformanceTests {
    // Test run by dvb on 2019-04-25 Chrome, 100 iterations, Time taken: 153ms
    @Test
    fun rerender_component_with_10_000_child_elements() {
        val iterations = 10

        val cmp = object : Component() {
            override fun render() = div {
                for (i in 1..10_000) {
                    span { }
                }
            }
        }
        cmp.context = Context()
        // Render once then rerender [iterations] times.
        cmp.render()

        val time = Performance.measureTime {
            for (i in 1..iterations) {
                cmp.context!!.nodeState = NodeState()
                cmp.render()
            }
        }

        println("""Render component with 10.000 child elements with $iterations iterations.
Time taken: ${time}ms
Average time: ${time / iterations}ms""")
    }

    // Test run by dvb on 2019-04-25 Chrome, 100 iterations, Time taken: 105ms
    @Test
    fun rerender_component_with_10_000_child_components() {
        val iterations = 10

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
        // Render once then rerender [iterations] times.
        cmp.render()

        val time = Performance.measureTime {
            for (i in 1..iterations) {
                cmp.context!!.nodeState = NodeState()
                cmp.render()
            }
        }

        println("""Render component with 10.000 child components with $iterations iterations.
Time taken: ${time}ms
Average time: ${time / iterations}ms""")
    }
}
