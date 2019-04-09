package magneton.observableold.collections

import magneton.observableold.mutableVar
import magneton.observableold.vars.MutableVar
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MutableWrappedObservableListTests {
    private val random = Random(113)

    @Test
    fun addWithExtractor() {
        // Create a list that propagates changes to the obs observable property in its contained ObsHolders.
        val list: MutableWrappedObservableList<ObsHolder> =
                MutableWrappedObservableList(
                        mutableListOf()
                ) { holder: ObsHolder -> arrayOf(holder.obs) }

        // Record all changes.
        val changes = mutableListOf<ListChange<*>>()
        list.onListChange { changes.add(it) }

        // Add some ObsHolders.
        val obsHolders = (1..10).map { ObsHolder("test_$it") }
        list.addAll(obsHolders)

        // Change ObsHolder values in random order.
        val updateOrder = (0..obsHolders.lastIndex).toList().shuffled(random)

        updateOrder.forEach {
            obsHolders[it].obs.value = "updated_$it"
        }

        // Check whether all updates came through in the right order.
        assertEquals(1 + updateOrder.size, changes.size)
        assertTrue(changes[0] is ListChange.Addition)

        for (i in (0..obsHolders.lastIndex)) {
            val obsHolderI = updateOrder[i]
            val obsHolder = obsHolders[obsHolderI]
            val changeI = i + 1
            val update = changes[changeI] as? ListChange.Update

            assertNotNull(update) { "change $changeI was not a ListChange.Update" }
            assertEquals(obsHolderI, update.index)
            assertEquals(obsHolder, update.updated)
        }
    }

    @Test
    fun removeWithExtractor() {
        // Create a list that propagates changes to the obs observable property in its contained ObsHolders.
        val list: MutableWrappedObservableList<ObsHolder> =
                MutableWrappedObservableList(
                        mutableListOf()
                ) { holder: ObsHolder -> arrayOf(holder.obs) }

        // Record all changes.
        val changes = mutableListOf<ListChange<*>>()
        list.onListChange { changes.add(it) }

        // Add some ObsHolders.
        val obsHolders = (1..10).map { ObsHolder("test_$it") }
        list.addAll(obsHolders)

        // Remove ObsHolders in random order.
        obsHolders.shuffled(random).forEach { list.remove(it) }

        assertTrue(list.isEmpty())

        // Change all ObsHolder values.
        obsHolders.forEachIndexed { i, holder -> holder.obs.value = "updated_$i" }

        // Check that no updates where fired.
        assertEquals(1 + obsHolders.size, changes.size)
        assertTrue(changes[0] is ListChange.Addition)

        for (change in changes.drop(1)) {
            assertTrue(change is ListChange.Removal<*>)
        }
    }

    @Test
    fun replaceAllWithExtractor() {
        // Create a list that propagates changes to the obs observable property in its contained ObsHolders.
        val list: MutableWrappedObservableList<ObsHolder> =
                MutableWrappedObservableList(
                        mutableListOf()
                ) { holder: ObsHolder -> arrayOf(holder.obs) }

        // Record all changes.
        val changes = mutableListOf<ListChange<*>>()
        list.onListChange { changes.add(it) }

        // Add some ObsHolders.
        val oldObsHolders = (1..10).map { ObsHolder("test_$it") }
        list.addAll(oldObsHolders)

        // Replace ObsHolders.
        val newObsHolders = (1..5).map { ObsHolder("new_$it") }
        list.replaceAll(newObsHolders)

        assertEquals(newObsHolders.size, list.size)

        // Change all ObsHolder values.
        oldObsHolders.forEachIndexed { i, holder -> holder.obs.value = "updated_$i" }
        newObsHolders.forEachIndexed { i, holder -> holder.obs.value = "new_updated_$i" }

        // Check that the correct ListChanges where fired.
        assertEquals(2 + newObsHolders.size, changes.size)
        assertTrue(changes[0] is ListChange.Addition)

        val replacement = changes[1] as? ListChange.Replacement

        assertNotNull(replacement)
        assertEquals(oldObsHolders.size, replacement.removed.size)
        assertTrue(replacement.removed.all { it in oldObsHolders })
        assertEquals(newObsHolders.size, replacement.added.size)
        assertTrue(replacement.added.all { it in newObsHolders })

        // Check that updates where only fired for changes to the replacement ObsHolders.
        for (change in changes.drop(2)) {
            val update = change as? ListChange.Update<*>
            assertNotNull(update)
            assertTrue(update.updated in newObsHolders)
        }
    }

    class ObsHolder(value: String) {
        val obs: MutableVar<String> = mutableVar(value)
    }
}
