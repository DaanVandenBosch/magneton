package magneton.observable

import kotlin.test.Test
import kotlin.test.assertEquals

class ObservableTests {
    @Test
    fun anObservableValueShouldFunctionAsARegularVariable() {
        var x: Int by ObservableValue(7)
        assertEquals(7, x)

        x = 99
        assertEquals(99, x)
    }

    @Test
    fun aReactionShouldReactToChangingObservableValues() {
        var x: Int by ObservableValue(7)
        var extractedX = -1

        reaction { extractedX = x }
        assertEquals(7, extractedX)

        x = 99
        assertEquals(99, extractedX)
    }

    @Test
    fun aReactionShouldBeTriggeredOnlyOncePerActionRun() {
        var x: Int by ObservableValue(0)
        val values = mutableListOf<Int>()

        reaction { values.add(x) }

        runInAction {
            x = 1
            x = 2
            runInAction {
                x = 3
            }
        }
        assertEquals(2, values.size)
        assertEquals(0, values[0])
        assertEquals(3, values[1])
    }

    @Test
    fun actionsShouldBeNestable() {
        var x by ObservableValue(-1)

        runInAction {
            x = 5
            runInAction {
                x = 10
            }
        }
        assertEquals(10, x)
    }

    @Test
    fun aComputedValueShouldUpdateWhenItsDependenciesChange() {
        var dep1: Int by ObservableValue(2)
        var dep2: Int by ObservableValue(3)
        val product by ComputedValue { dep1 * dep2 }
        assertEquals(6, product)

        dep1 = 3
        assertEquals(9, product)

        dep2 = 4
        assertEquals(12, product)
    }
}
