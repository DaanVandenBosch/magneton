package magneton.observable.collections

import magneton.observable.Observable
import magneton.observable.Subscription
import magneton.observable.vars.CombinedVar
import magneton.observable.vars.Var

interface ObservableList<T> : Observable<List<T>>, List<T> {
    fun onListChange(listener: ListChangeListener<T>): Subscription

    override fun subList(fromIndex: Int, toIndex: Int): ObservableList<T>

    fun <R> map(transform: (T) -> R): ObservableList<R>

    fun <R> foldVar(initial: R, operation: (acc: R, T) -> R): Var<R> =
            CombinedVar({ fold(initial, operation) }, this)

    fun sumByVar(selector: (T) -> Int): Var<Int> =
            foldVar(0) { acc, element -> acc + selector(element) }

    fun countVar(predicate: (T) -> Boolean): Var<Int> =
            foldVar(0) { acc, element -> if (predicate(element)) acc + 1 else acc }

    val sizeVar: Var<Int>
}
