package magneton.observable

interface Observable {
    val derivations: MutableList<Derivation>
}

internal fun Observable.reportObserved() {
    observedObservables.add(this)
}

internal fun Observable.reportChanged() {
    changedObservables.add(this)
}
