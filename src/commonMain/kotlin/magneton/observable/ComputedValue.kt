package magneton.observable

import magneton.FastSet
import magneton.unsafeCast
import kotlin.reflect.KProperty

class ComputedValue<T>(
        private val block: () -> T
) : Observable<T>, Derivation {
    private var initialized = false
    private var value: T? = null

    override val derivations: MutableList<Derivation> = ArrayList()
    override var lastActionRunId: Int = -1
    override val dependencies: FastSet<Observable<*>> = FastSet()

    override fun update() {
        val value = block()

        if (!initialized || value != this.value) {
            this.value = value
            reportChanged()
        }
    }

    override fun get(): T {
        reportObserved()

        if (!initialized) {
            updateDerivation(this)
            initialized = true
        }

        return value.unsafeCast()
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
            get()
}
