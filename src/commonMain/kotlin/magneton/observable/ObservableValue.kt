package magneton.observable

import kotlin.reflect.KProperty

class ObservableValue<T>(
        private var value: T
) : Observable {
    override val derivations: MutableList<Derivation> = mutableListOf()

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        reportObserved()
        return value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        reportObserved()

        if (value != this.value) {
            this.value = value

            enforceInAction {
                reportChanged()
            }
        }
    }
}
