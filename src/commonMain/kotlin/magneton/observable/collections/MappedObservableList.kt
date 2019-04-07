package magneton.observable.collections

import magneton.observable.ChangeListener
import magneton.observable.Subscription
import magneton.observable.vars.Var

class MappedObservableList<T, U>(
        private val src: ObservableList<T>,
        private val transform: (T) -> U
) : ObservableList<U>, AbstractObservableList<U>() {
    private var srcChangeSubscription: Subscription? = null
    private val elements = ArrayList<U>()

    override val size: Int get() = src.size
    override val sizeVar: Var<Int> = src.sizeVar

    override fun contains(element: U): Boolean {
        for (el in this)
            if (el == element) return true

        return false
    }

    override fun containsAll(elements: Collection<U>): Boolean =
            elements.all(::contains)

    override fun get(index: Int): U =
    // When we're not subscribed to [src] we need to recompute [transform] each time because the elements of [src] might have changed.
            if (srcChangeSubscription == null) {
                transform(src[index])
            } else {
                elements[index]
            }

    override fun isEmpty(): Boolean = src.isEmpty()

    override fun indexOf(element: U): Int {
        for (i in 0..lastIndex)
            if (elements[i] == element) return i

        return -1
    }

    override fun lastIndexOf(element: U): Int {
        for (i in lastIndex downTo 0)
            if (elements[i] == element) return i

        return -1
    }

    override fun subList(fromIndex: Int, toIndex: Int): ObservableList<U> {
        TODO("fun subList(fromIndex: Int, toIndex: Int): ObservableList<U> not implemented")
    }

    // Lazily bound to [src] to avoid memory leaks.
    // See http://tomasmikula.github.io/blog/2015/02/10/val-a-better-observablevalue.html
    override fun onChange(listener: ChangeListener<List<U>>): Subscription {
        addSrcChangeListener()
        changeListeners += listener

        return Subscription {
            changeListeners -= listener
            removeSrcChangeListener()
        }
    }

    // Lazily bound to [src] to avoid memory leaks.
    // See http://tomasmikula.github.io/blog/2015/02/10/val-a-better-observablevalue.html
    override fun onListChange(listener: ListChangeListener<U>): Subscription {
        addSrcChangeListener()
        listChangeListeners += listener

        return Subscription {
            listChangeListeners -= listener
            removeSrcChangeListener()
        }
    }

    private fun addSrcChangeListener() {
        if (srcChangeSubscription == null) {
            elements.ensureCapacity(src.size)

            for (element in src) {
                elements.add(transform(element))
            }

            srcChangeSubscription = src.onListChange { change ->
                when (change) {
                    is ListChange.Addition -> {
                        val transformed = change.added.map(transform)
                        elements.addAll(change.from, transformed)
                        processChange(ListChange.Addition(transformed, change.from))
                    }
                    is ListChange.Removal -> {
                        val removed = ArrayList<U>(change.removed.size)

                        for (i in change.from..change.to) {
                            removed.add(elements.removeAt(i))
                        }

                        processChange(ListChange.Removal(removed, change.from))
                    }
                    is ListChange.Replacement -> {
                        val removed = ArrayList<U>(change.removed.size)

                        for (i in change.from..change.removedTo) {
                            removed.add(elements.removeAt(i))
                        }

                        val transformed = change.added.map(transform)
                        elements.addAll(change.from, transformed)

                        processChange(ListChange.Replacement(removed, transformed, change.from))
                    }
                    is ListChange.Update -> {
                        val transformed = elements.set(change.index, transform(change.updated))
                        processChange(ListChange.Update(transformed, change.index))
                    }
                }
            }
        }
    }

    private fun removeSrcChangeListener() {
        if (changeListeners.isEmpty() && listChangeListeners.isEmpty()) {
            srcChangeSubscription?.unsubscribe()
            srcChangeSubscription = null
            elements.clear()
        }
    }
}
