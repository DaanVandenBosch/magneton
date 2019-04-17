package magneton.observable

import kotlin.test.Test
import kotlin.test.assertEquals

class ObservableTests {
    @Test
    fun an_observable_value_should_function_as_a_regular_variable() {
        var x: Int by ObservableValue(7)
        assertEquals(7, x)

        x = 99
        assertEquals(99, x)
    }

    @Test
    fun a_reaction_should_react_to_changing_observable_values() {
        var x: Int by ObservableValue(7)
        var extractedX = -1

        reaction { extractedX = x }
        assertEquals(7, extractedX)

        x = 99
        assertEquals(99, extractedX)
    }

    @Test
    fun a_reaction_should_be_triggered_only_once_per_action_run() {
        var x: Int by ObservableValue(0)
        val values = mutableListOf<Int>()

        reaction { values.add(x) }

        action {
            x = 1
            x = 2
            x = 3
        }
        assertEquals(2, values.size)
        assertEquals(0, values[0])
        assertEquals(3, values[1])
    }

    @Test
    fun reactions_should_be_nestable() {
        var x by ObservableValue(5)
        var y by ObservableValue(-1)
        var z = -1
        var disposer: ReactionDisposer? = null

        reaction {
            y = 2 * x

            disposer?.dispose()
            disposer = reaction {
                z = y + 2
            }
        }
        assertEquals(10, y)
        assertEquals(12, z)

        x = 7
        assertEquals(14, y)
        assertEquals(16, z)
    }

    @Test
    fun actions_should_be_nestable() {
        var x by ObservableValue(-1)

        action {
            x = 5
            action {
                x = 10
            }
        }
        assertEquals(10, x)
    }

    @Test
    fun a_computed_value_should_update_when_its_dependencies_change() {
        var dep1: Int by ObservableValue(2)
        var dep2: Int by ObservableValue(3)
        val product: Int by ComputedValue { dep1 * dep2 }
        assertEquals(6, product)

        dep1 = 3
        assertEquals(9, product)

        dep2 = 4
        assertEquals(12, product)
    }

    @Test
    fun a_reaction_should_update_when_its_computable_dependency_changes() {
        var value by ObservableValue("a")
        var extracted = ""
        val valueUpper by ComputedValue { value.toUpperCase() }

        reaction {
            extracted = valueUpper
        }

        assertEquals("A", valueUpper)
        assertEquals("A", extracted)

        value = "b"

        assertEquals("B", valueUpper)
        assertEquals("B", extracted)
    }
}
