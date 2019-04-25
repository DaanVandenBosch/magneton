package magneton

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FastSetTests {
    @Test
    fun contains() {
        val set = FastSet<Int>()
        set.add(1)
        set.add(2)
        set.add(3)

        assertTrue(set.contains(1))
        assertTrue(set.contains(2))
        assertTrue(set.contains(3))
        assertFalse(set.contains(4))
        assertFalse(set.contains(5))
        assertFalse(set.contains(6))
    }

    @Test
    fun add() {
        val set = FastSet<Int>()
        set.add(7)

        assertTrue(set.contains(7))
    }

    @Test
    fun remove() {
        val set = FastSet<Int>()
        set.add(7)

        assertTrue(7 in set)

        set.remove(7)

        assertTrue(7 !in set)
    }

    @Test
    fun removeAll_remove_nothing() {
        val set = FastSet<Int>()
        set.add(1)
        set.add(2)
        set.add(3)
        set.add(4)
        set.add(5)

        var x = 0

        set.removeAll { x += it; it % 2 == 0 }

        assertEquals(15, x)
        assertTrue(1 in set)
        assertTrue(2 !in set)
        assertTrue(3 in set)
        assertTrue(4 !in set)
        assertTrue(5 in set)
    }

    @Test
    fun clear() {
        val set = FastSet<Int>()
        set.add(1)
        set.add(2)
        set.add(3)

        assertTrue(1 in set)
        assertTrue(2 in set)
        assertTrue(3 in set)

        set.clear()

        assertFalse(1 in set)
        assertFalse(2 in set)
        assertFalse(3 in set)
    }

    @Test
    fun forEach() {
        val set = FastSet<Int>()
        set.add(1)
        set.add(2)
        set.add(3)

        var x = 0

        set.forEach { x += it }

        assertEquals(6, x)
    }
}
