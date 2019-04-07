package magneton.observable.collections

import magneton.observable.ChangeListener
import magneton.observable.Subscription

abstract class AbstractObservableList<T> : ObservableList<T> {
    protected val changeListeners = mutableListOf<ChangeListener<List<T>>>()
    protected val listChangeListeners = mutableListOf<ListChangeListener<T>>()

    override val value: List<T> get() = this

    override fun iterator(): Iterator<T> = Itr()

    override fun listIterator(): ListIterator<T> = ListItr(0)

    override fun listIterator(index: Int): ListIterator<T> {
        if (index !in 0..size) throw IndexOutOfBoundsException()
        return ListItr(index)
    }

    override fun <R> map(transform: (T) -> R): ObservableList<R> =
            MappedObservableList(this, transform)

    override fun onChange(listener: ChangeListener<List<T>>): Subscription {
        changeListeners += listener
        return Subscription { changeListeners -= listener }
    }

    override fun onListChange(listener: ListChangeListener<T>): Subscription {
        listChangeListeners += listener
        return Subscription { listChangeListeners -= listener }
    }

    protected open fun processChange(listChange: ListChange<T>) {
        for (cl in changeListeners) {
            cl(this)
        }

        for (cl in listChangeListeners) {
            cl(listChange)
        }
    }

    private inner class Itr : Iterator<T> {
        var cursor = 0

        override fun hasNext(): Boolean = cursor < size

        override fun next(): T {
            return get(cursor++)
        }
    }

    private inner class ListItr(var cursor: Int) : ListIterator<T> {
        override fun hasNext(): Boolean = cursor < size

        override fun next(): T {
            return get(cursor++)
        }

        override fun nextIndex(): Int = cursor

        override fun hasPrevious(): Boolean = cursor > 0

        override fun previous(): T {
            return get(--cursor)
        }

        override fun previousIndex(): Int = cursor - 1
    }
}
