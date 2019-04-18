package magneton.observable

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
    fun a_reaction_should_be_triggered_only_once_per_action_run_2() {
        var x: Int by ObservableValue(0)
        var y: Int by ObservableValue(0)
        val values = mutableListOf<Int>()

        reaction { values.add(x + y) }

        action {
            x = 1
            x = 2
            x = 3
            y = 10
            y = 20
            y = 30
        }
        assertEquals(2, values.size)
        assertEquals(0, values[0])
        assertEquals(33, values[1])
    }

    @Test
    fun a_computed_value_update_should_be_triggered_only_once_per_action_run() {
        var x: Int by ObservableValue(0)
        val values = mutableListOf<Int>()
        val c by ComputedValue {
            values.add(x)
            10 * x
        }
        var extracted = -1

        reaction {
            extracted = c
        }

        action {
            x = 1
            x = 2
            x = 3
        }

        assertEquals(30, c)
        assertEquals(30, extracted)
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

    @Test
    fun it_should_be_possible_to_create_a_reaction_in_an_action() {
        var a by ObservableValue(1)
        val values = mutableListOf<Int>()

        action {
            reaction {
                values.add(a + 10)
            }
        }

        a = 2
        a = 3

        assertEquals(3, values.size)
        assertEquals(11, values[0])
        assertEquals(12, values[1])
        assertEquals(13, values[2])
    }

    @Test
    fun it_should_be_possible_to_change_unobserved_state_in_an_action_called_from_computed() {
        var a by observable(2)

        val c by computed {
            action {
                a = 3
            }
            true
        }

        var b = false

        reaction {
            b = c
        }

        assertTrue(b)
        assertEquals(3, a)
    }
}
