package magneton.observable.vars

import kotlin.reflect.KProperty

/**
 * Observable wrapper around a single mutable value.
 */
interface MutableVar<T> : Var<T> {
    override var value: T

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}
