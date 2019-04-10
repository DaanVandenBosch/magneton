package magneton.observable

private object GlobalActionState {
    var inAction = false
    var changedObservables: MutableSet<Observable>? = null
}

private object GlobalDerivationState {
    var observedObservables: MutableSet<Observable>? = null
}

internal fun Observable.reportObserved() {
    GlobalDerivationState.observedObservables?.add(this)
}

internal fun Observable.reportChanged() {
    GlobalActionState.changedObservables?.add(this)
}

internal fun updateDerivation(derivation: Derivation) {
    // Save global state.
    val prevObservedObservables = GlobalDerivationState.observedObservables

    // Set new global state.
    val currentObservedObservables = mutableSetOf<Observable>()
    GlobalDerivationState.observedObservables = currentObservedObservables

    try {
        derivation.update()
    } finally {
        // Restore previous global state.
        GlobalDerivationState.observedObservables = prevObservedObservables

        // Update dependencies list.
        val iter = derivation.dependencies.iterator()

        while (iter.hasNext()) {
            val dependency = iter.next()

            if (dependency !in currentObservedObservables) {
                iter.remove()
                dependency.derivations.remove(derivation)
            }
        }

        currentObservedObservables.forEach { dependency ->
            if (dependency !in derivation.dependencies) {
                derivation.dependencies.add(dependency)
                dependency.derivations.add(derivation)
            }
        }
    }
}

internal fun <T> enforceInAction(block: () -> T): T {
    return if (GlobalActionState.inAction) {
        block()
    } else {
        runInAction(block)
    }
}

internal fun <T> runInAction(block: () -> T): T {
    // Save global state.
    val prevInAction = GlobalActionState.inAction
    val prevChangedObservables = GlobalActionState.changedObservables

    // Set new global state.
    GlobalActionState.inAction = true
    val currentChangedObservables = mutableSetOf<Observable>()
    GlobalActionState.changedObservables = currentChangedObservables

    try {
        return block()
    } finally {
        // Restore previous global state.
        GlobalActionState.inAction = prevInAction
        GlobalActionState.changedObservables = prevChangedObservables

        // Update observers of changed observables.
        currentChangedObservables.forEach { observable ->
            observable.derivations.forEach(::updateDerivation)
        }
    }
}
