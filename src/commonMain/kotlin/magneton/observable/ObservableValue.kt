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
            if (!inAction) {
                runInAction {
                    this.value = value
                    reportChanged()
                }
            } else {
                this.value = value
                reportChanged()
            }
        }
    }
}
