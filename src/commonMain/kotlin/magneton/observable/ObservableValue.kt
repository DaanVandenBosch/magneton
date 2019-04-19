package magneton.observable

import kotlin.reflect.KProperty

class ObservableValue<T>(
        private var value: T
) : Observable<T> {
    override val derivations: MutableList<Derivation> = mutableListOf()
    override var lastActionRunId: Int = -1

    override fun get(): T {
        reportObserved()
        return value
    }

    fun set(value: T) {
        reportObserved()

        if (value != this.value) {
            this.value = value

            enforceInAction {
                reportChanged()
            }
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
            get()

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        set(value)
    }
}
