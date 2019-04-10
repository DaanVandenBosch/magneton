package magneton.observable

import kotlin.reflect.KProperty

class ComputedValue<T>(
        private val block: () -> T
) : Observable, Derivation {
    private var initialized = false
    private var value: T? = null

    override val dependencies: MutableList<Observable> = mutableListOf()
    override val derivations: MutableList<Derivation> = mutableListOf()

    override fun update() {
        val value = block()

        if (value != this.value) {
            this.value = value
            reportChanged()
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        reportObserved()

        if (!initialized) {
            updateDerivation(this)
        }

        @Suppress("UNCHECKED_CAST")
        return value as T
    }
}
