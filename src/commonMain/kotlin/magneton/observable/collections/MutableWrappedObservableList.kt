package magneton.observable.collections

import magneton.observable.vars.Var
import magneton.observable.vars.SimpleMutableVar

class MutableWrappedObservableList<T>(
        override val elements: MutableList<T>,
        override val extractor: Extractor<T>?
) : MutableObservableList<T>, AbstractWrappedObservableList<T>() {
    private val mutableSizeObservable by lazy { SimpleMutableVar(elements.size) }

    override val sizeVar: Var<Int> = mutableSizeObservable

    override fun iterator(): MutableIterator<T> = Itr()

    override fun add(element: T): Boolean {
        elements.add(element)
        mutableSizeObservable.value = elements.size
        processChange(ListChange.Addition(listOf(element), elements.size - 1))
        return true
    }

    override fun add(index: Int, element: T) {
        elements.add(index, element)
        mutableSizeObservable.value = elements.size
        processChange(ListChange.Addition(listOf(element), index))
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        if (elements.isEmpty()) return false
        this.elements.addAll(index, elements)
        mutableSizeObservable.value = elements.size
        processChange(ListChange.Addition(elements.toList(), index))
        return true
    }

    override fun addAll(elements: Collection<T>): Boolean {
        if (elements.isEmpty()) return false
        val index = this.elements.size
        this.elements.addAll(elements)
        mutableSizeObservable.value = elements.size
        processChange(ListChange.Addition(elements.toList(), index))
        return true
    }

    override fun clear() {
        val deleted = elements.toList()
        elements.clear()
        mutableSizeObservable.value = elements.size
        processChange(ListChange.Removal(deleted, 0))
    }

    override fun listIterator(): MutableListIterator<T> {
        TODO("fun listIterator(): MutableListIterator<T> not implemented")
    }

    override fun listIterator(index: Int): MutableListIterator<T> {
        TODO("fun listIterator(index: Int): MutableListIterator<T> not implemented")
    }

    override fun remove(element: T): Boolean {
        for ((i, el) in elements.withIndex()) {
            if (el == element) {
                removeAt(i)
                return true
            }
        }

        return false
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        TODO("fun removeAll(elements: Collection<T>): Boolean not implemented")
    }

    override fun removeAt(index: Int): T {
        val removed = elements.removeAt(index)
        mutableSizeObservable.value = elements.size
        processChange(ListChange.Removal(listOf(removed), index))
        return removed
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        TODO("fun retainAll(elements: Collection<T>): Boolean not implemented")
    }

    override fun set(index: Int, element: T): T {
        val deleted = elements.set(index, element)
        processChange(ListChange.Replacement(listOf(deleted), listOf(element), index))
        return deleted
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableObservableList<T> {
        TODO("fun subList(fromIndex: Int, toIndex: Int): MutableObservableList<T> not implemented")
    }

    override fun replaceAll(elements: Collection<T>) {
        val deleted = this.elements.toList()
        this.elements.clear()
        this.elements.addAll(elements)
        mutableSizeObservable.value = elements.size
        processChange(ListChange.Replacement(deleted, elements.toList(), 0))
    }

    private inner class Itr : MutableIterator<T> {
        var cursor = 0

        override fun hasNext(): Boolean = cursor < size

        override fun next(): T {
            return get(cursor++)
        }

        override fun remove() {
            removeAt(--cursor)
        }
    }
}
