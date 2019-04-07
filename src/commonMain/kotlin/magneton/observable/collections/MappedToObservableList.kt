package magneton.observable.collections

import magneton.observable.ChangeListener
import magneton.observable.Subscription
import magneton.observable.vars.SimpleMutableVar
import magneton.observable.vars.Var

// TODO: optimize by delegating to [list] when [list] is an [ObservableList].
class MappedToObservableList<T, U>(
        private val src: Var<T>,
        private val f: (T) -> List<U>
) : ObservableList<U>, AbstractWrappedObservableList<U>() {
    private var srcChangeSubscription: Subscription? = null
    private var list = f(src.value)
    private val mutableSizeObservable = SimpleMutableVar(list.size)

    override val elements
        // When we're not subscribed to [src] we need to recompute [f] each time because the value of [src] might have changed.
        get() = if (srcChangeSubscription == null) {
            f(src.value)
        } else {
            list
        }

    override val extractor: Extractor<U>? = null

    override val sizeVar: Var<Int> = mutableSizeObservable

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
            list = f(src.value)
            srcChangeSubscription = src.onChange { newVal ->
                val removed = list
                list = f(newVal)
                mutableSizeObservable.value = list.size
                processChange(ListChange.Replacement(removed, list, 0))
            }
        }
    }

    private fun removeSrcChangeListener() {
        if (changeListeners.isEmpty() && listChangeListeners.isEmpty()) {
            srcChangeSubscription?.unsubscribe()
            srcChangeSubscription = null
        }
    }
}
