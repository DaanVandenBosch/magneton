package magneton.observable.vars

import magneton.observable.Observable
import magneton.observable.collections.ObservableList
import kotlin.reflect.KProperty

/**
 * Observable wrapper around a single value. Provides many utility methods on top of [Observable].
 */
interface Var<T> : Observable<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value

    fun <U> map(f: (T) -> U): Var<U>

    fun <U> mapToList(f: (T) -> List<U>): ObservableList<U>

    fun <U> flatMap(f: (T) -> Observable<U>): Var<U>
}
