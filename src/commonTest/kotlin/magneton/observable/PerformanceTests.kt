package magneton.observable

import magneton.testutil.Performance
import kotlin.test.Test
import kotlin.test.assertEquals

class PerformanceTests {
    // Test run by dvb 2019-04-25 Chrome, 10 iterations, Time taken: 94ms
    @Test
    fun a_100_observables_are_referenced_by_100_computed_values() {
        n_observables_are_referenced_by_m_computed_values(100, 100, iterations = 10)
    }

    // Test run by dvb 2019-04-25 Chrome, 10 iterations, Time taken: 98ms
    @Test
    fun a_10_observables_are_referenced_by_1000_computed_values() {
        n_observables_are_referenced_by_m_computed_values(10, 1000, iterations = 10)
    }

    // Test run by dvb 2019-04-25 Chrome, 10 iterations, Time taken: 74ms
    @Test
    fun a_1000_observables_are_referenced_by_10_computed_values() {
        n_observables_are_referenced_by_m_computed_values(1000, 10, iterations = 10)
    }

    private fun n_observables_are_referenced_by_m_computed_values(n: Int, m: Int, iterations: Int) {
        val observables = Array(n) { observable(it) }
        val computedValues = Array(m) { computed { it * observables.sumBy { it.get() } } }
        var sum = -1

        reaction {
            sum = computedValues.sumBy { it.get() }
        }

        // Make sure sum isn't compiled away.
        assertEquals((n * (n - 1)) * (m * (m - 1)) / 4, sum)

        val time = Performance.measureTime {
            for (i in 1..iterations) {
                action {
                    observables.forEach { it.set(it.get() + 1) }
                }
            }
        }

        println("""$n Observables are referenced by $m computed values with $iterations iterations.
Time taken: ${time}ms
Average time: ${time / iterations}ms""")
    }
}
