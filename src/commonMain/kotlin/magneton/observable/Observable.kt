package magneton.observable

interface Observable<T> : ActionAware {
    val derivations: MutableList<Derivation>

    fun get(): T
}
