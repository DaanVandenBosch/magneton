package magneton.observable

internal var inAction = false
internal val observedObservables: MutableSet<Observable> = mutableSetOf()
internal var changedObservables: MutableSet<Observable> = mutableSetOf()

internal fun updateDerivation(derivation: Derivation) {
    try {
        derivation.update()
    } finally {
        // Update dependencies list.
        val iter = derivation.dependencies.iterator()

        while (iter.hasNext()) {
            val observable = iter.next()

            if (observable !in observedObservables) {
                iter.remove()
                observable.derivations.remove(derivation)
            }
        }

        observedObservables.forEach { observable ->
            if (observable !in derivation.dependencies) {
                derivation.dependencies.add(observable)
                observable.derivations.add(derivation)
            }
        }

        // Reset global state
        observedObservables.clear()
        val changed = changedObservables
        changedObservables = mutableSetOf()

        // Recurse over observers of changed observables.
        changed.forEach { observable ->
            observable.derivations.forEach(::updateDerivation)
        }
    }
}

internal fun <T> runInAction(block: () -> T): T {
    // Avoid updating observers multiple times when an action is fired from inside another action.
    val nested = inAction

    try {
        inAction = true
        return block()
    } finally {
        if (!nested) {
            // Reset global state
            inAction = false
            observedObservables.clear()
            val changed = changedObservables
            changedObservables = mutableSetOf()

            // Update observers of changed observables.
            changed.forEach { observable ->
                observable.derivations.forEach(::updateDerivation)
            }
        }
    }
}
