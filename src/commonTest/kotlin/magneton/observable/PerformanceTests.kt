package magneton.observable

import magneton.testutil.Performance
import kotlin.test.Test
import kotlin.test.assertEquals

class PerformanceTests {
    // Test run by dvb 2019-04-19 JVM, Time taken: 18.7218ms
    // Test run by dvb 2019-04-19 Firefox, Time taken: 30ms
    @Test
    fun a_100_observables_are_referenced_by_100_computed_values() {
        n_observables_are_referenced_by_m_computed_values(100, 100)
    }

    // Test run by dvb 2019-04-19 JVM, Time taken: 6.0682ms
    // Test run by dvb 2019-04-19 Firefox, Time taken: 27ms
    @Test
    fun a_10_observables_are_referenced_by_1000_computed_values() {
        n_observables_are_referenced_by_m_computed_values(10, 1000)
    }

    // Test run by dvb 2019-04-19 JVM, Time taken: 7.3491ms
    // Test run by dvb 2019-04-19 Firefox, Time taken: 41ms
    @Test
    fun a_1000_observables_are_referenced_by_10_computed_values() {
        n_observables_are_referenced_by_m_computed_values(1000, 10)
    }

    private fun n_observables_are_referenced_by_m_computed_values(n: Int, m: Int) {
        val observables = Array(n) { observable(it) }
        val computedValues = Array(m) { computed { it * observables.sumBy { it.get() } } }
        var sum = -1

        reaction {
            sum = computedValues.sumBy { it.get() }
        }

        assertEquals((n * (n - 1)) * (m * (m - 1)) / 4, sum)

        val time = Performance.measureTime {
            action {
                observables.forEach { it.set(it.get() + 1) }
            }
        }

        println("$n Observables are referenced by $m computed values.\nTime taken: ${time}ms")
        assertEquals((n * (n + 1)) * (m * (m - 1)) / 4, sum)
    }
}
