package magneton.observable.vars

import kotlin.test.Test
import kotlin.test.assertEquals

class SimpleMutableVarTests {
    @Test
    fun oneChangeHandler() {
        val mutVar = SimpleMutableVar(0)
        var changeHandlerRan = 0

        mutVar.onChange {
            changeHandlerRan++
        }

        for (i in 1..10) {
            mutVar.value = 7 * i

            assertEquals(i, changeHandlerRan)
        }
    }

    @Test
    fun unsubscribeChangeHandler() {
        val mutVar = SimpleMutableVar(0)
        var changeHandlerRan = 0

        val sub = mutVar.onChange {
            changeHandlerRan++
        }
        sub.unsubscribe()

        for (i in 1..10) {
            mutVar.value = 7 * i

            assertEquals(0, changeHandlerRan)
        }
    }

    @Test
    fun multipleChangeHandlers() {
        val obs = SimpleMutableVar(0)
        val changeHandlerCount = 10
        var changeHandlerRan = 0

        for (i in 1..changeHandlerCount) {
            obs.onChange {
                changeHandlerRan++
            }
        }

        for (i in 1..10) {
            obs.value = 7 * i

            assertEquals(changeHandlerCount * i, changeHandlerRan)
        }
    }

    @Test
    fun flatMap() {
        // After changing the value of a nested Var, the flatMapped Var should change too.
        val holder1 = VarHolder(1)
        val mutVar = SimpleMutableVar(holder1)
        val flatMapped = mutVar.flatMap { it.xVar }

        holder1.xVar.value = 2

        assertEquals(2, flatMapped.value)

        // After changing the nested Var itself, the flatMapped Var should change again.
        val holder2 = VarHolder(3)
        mutVar.value = holder2

        assertEquals(3, flatMapped.value)

        // Changing the original nested Var at this point should have no effect on the flatMapped Var.
        holder1.xVar.value = 4

        assertEquals(3, flatMapped.value)
    }

    @Test
    fun flatMapOnChange() {
        // After changing the value of a nested Var, the flatMapped Var should change too.
        val holder1 = VarHolder(1)
        val mutVar = SimpleMutableVar(holder1)
        val flatMapped = mutVar.flatMap { it.xVar }
        var retrievedValue = -1

        flatMapped.onChange { retrievedValue = it }

        holder1.xVar.value = 2

        assertEquals(2, retrievedValue)

        // After changing the nested Var itself, the flatMapped Var should change again.
        val holder2 = VarHolder(3)
        mutVar.value = holder2

        assertEquals(3, retrievedValue)

        // Changing the original nested Var at this point should have no effect on the flatMapped Var.
        holder1.xVar.value = 4

        assertEquals(3, retrievedValue)
    }

    private class VarHolder(x: Int) {
        val xVar = SimpleMutableVar(x)
    }
}
