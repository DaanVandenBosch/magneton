package magneton.observable.collections

import magneton.observable.ChangeListener
import magneton.observable.Subscription

abstract class AbstractWrappedObservableList<T>
    : ObservableList<T>, AbstractObservableList<T>() {

    private var elementSubscriptions: MutableList<Array<Subscription>>? = null

    protected abstract val extractor: Extractor<T>?

    protected abstract val elements: List<T>

    override val size: Int get() = elements.size

    override fun contains(element: T): Boolean = elements.contains(element)

    override fun containsAll(elements: Collection<T>): Boolean =
            this.elements.containsAll(elements)

    override fun get(index: Int): T = elements[index]

    override fun indexOf(element: T): Int = elements.indexOf(element)

    override fun isEmpty(): Boolean = elements.isEmpty()

    override fun lastIndexOf(element: T): Int = elements.lastIndexOf(element)

    override fun subList(fromIndex: Int, toIndex: Int): ObservableList<T> {
        TODO("fun subList(fromIndex: Int, toIndex: Int): ObservableList<T> not implemented")
    }

    override fun <R> map(transform: (T) -> R): ObservableList<R> {
        TODO("fun <R> map(transform: (T) -> R): ObservableList<R> not implemented")
    }

    // Lazily bound to observables extracted by [extractor] to avoid memory leaks.
    // See http://tomasmikula.github.io/blog/2015/02/10/val-a-better-observablevalue.html
    override fun onChange(listener: ChangeListener<List<T>>): Subscription {
        startElementSubs()
        changeListeners += listener

        return Subscription {
            changeListeners -= listener
            stopElementSubs()
        }
    }

    // Lazily bound to observables extracted by [extractor] to avoid memory leaks.
    // See http://tomasmikula.github.io/blog/2015/02/10/val-a-better-observablevalue.html
    override fun onListChange(listener: ListChangeListener<T>): Subscription {
        startElementSubs()
        listChangeListeners += listener

        return Subscription {
            listChangeListeners -= listener
            stopElementSubs()
        }
    }

    override fun processChange(listChange: ListChange<T>) {
        extractor?.let { ext ->
            elementSubscriptions?.let { subs ->
                when (listChange) {
                    is ListChange.Addition -> {
                        addElementSubs(subs, listChange.addedWithIndex, ext)
                    }
                    is ListChange.Removal -> {
                        removeElementSubs(subs, listChange.from, listChange.to)
                    }
                    is ListChange.Replacement -> {
                        removeElementSubs(subs, listChange.from, listChange.removedTo)
                        addElementSubs(subs, listChange.addedWithIndex, ext)
                    }
                    is ListChange.Update -> {
                        // Do nothing, because updates don't add or remove elements.
                    }
                }
            }
        }

        super.processChange(listChange)
    }

    private fun startElementSubs() {
        extractor?.let { ext ->
            if (elementSubscriptions == null) {
                val subs = mutableListOf<Array<Subscription>>()
                elementSubscriptions = subs
                addElementSubs(subs, elements.withIndex(), ext)
            }
        }
    }

    private fun stopElementSubs() {
        if (changeListeners.isEmpty() && listChangeListeners.isEmpty()) {
            elementSubscriptions?.forEach { it.forEach { sub -> sub.unsubscribe() } }
            elementSubscriptions = null
        }
    }

    private fun addElementSubs(
            elementSubscriptions: MutableList<Array<Subscription>>,
            addedWithIndex: Iterable<IndexedValue<T>>,
            extractor: Extractor<T>
    ) {
        for ((i, element) in addedWithIndex) {
            val elementListener = { _: Any? ->
                processChange(ListChange.Update(element, i))
            }

            val observables = extractor(element)
            elementSubscriptions.add(
                    i,
                    Array(observables.size) { observables[it].onChange(elementListener) }
            )
        }
    }

    private fun removeElementSubs(
            elementSubscriptions: MutableList<Array<Subscription>>,
            from: Int,
            to: Int
    ) {
        for (i in from until to) {
            elementSubscriptions.removeAt(from).forEach { it.unsubscribe() }
        }
    }
}
