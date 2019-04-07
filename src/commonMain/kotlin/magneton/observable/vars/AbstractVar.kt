package magneton.observable.vars

import magneton.observable.ChangeListener
import magneton.observable.Observable
import magneton.observable.Subscription
import magneton.observable.collections.MappedToObservableList
import magneton.observable.collections.ObservableList

abstract class AbstractVar<T> : Var<T> {
    protected val changeListeners = mutableListOf<ChangeListener<T>>()

    override fun onChange(listener: ChangeListener<T>): Subscription {
        changeListeners += listener
        return Subscription { changeListeners -= listener }
    }

    protected fun callListeners() {
        for (cl in changeListeners) {
            cl(value)
        }
    }

    override fun <U> map(f: (T) -> U): Var<U> =
            CombinedVar({ f(value) }, this)

    override fun <U> mapToList(f: (T) -> List<U>): ObservableList<U> =
            MappedToObservableList(this, f)

    override fun <U> flatMap(f: (T) -> Observable<U>): Var<U> =
            FlatMappedVar(this, f)
}
