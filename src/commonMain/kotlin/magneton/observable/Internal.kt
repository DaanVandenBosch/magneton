package magneton.observable

import magneton.GlobalState

private class ActionState {
    val changedObservables: MutableSet<Observable> = mutableSetOf()

    companion object {
        val Global = GlobalState<ActionState>()
    }
}

private class DerivationState {
    val observedObservables: MutableSet<Observable> = mutableSetOf()

    companion object {
        val Global = GlobalState<DerivationState>()
    }
}

internal fun Observable.reportObserved() {
    DerivationState.Global.get()?.observedObservables?.add(this)
}

internal fun Observable.reportChanged() {
    ActionState.Global.get()?.changedObservables?.add(this)
}

internal fun updateDerivation(derivation: Derivation) {
    val newState = DerivationState()
    val prevState = DerivationState.Global.set(newState)

    try {
        derivation.update()
    } finally {
        // Restore previous global state.
        DerivationState.Global.restore(prevState)

        // Update dependencies list.
        val iter = derivation.dependencies.iterator()

        while (iter.hasNext()) {
            val dependency = iter.next()

            if (dependency !in newState.observedObservables) {
                iter.remove()
                dependency.derivations.remove(derivation)
            }
        }

        newState.observedObservables.forEach { dependency ->
            if (dependency !in derivation.dependencies) {
                derivation.dependencies.add(dependency)
                dependency.derivations.add(derivation)
            }
        }
    }
}

internal fun <T> enforceInAction(block: () -> T): T {
    return if (ActionState.Global.get() != null) {
        block()
    } else {
        runInAction(block)
    }
}

internal fun <T> runInAction(block: () -> T): T {
    val newState = ActionState()
    val prevState = ActionState.Global.set(newState)

    try {
        return block()
    } finally {
        ActionState.Global.restore(prevState)

        // Update derivations of changed observables.
        // Make a copy of all derivations lists (using flatMap) because they might change when one of the derivations is updated.
        // Flat map to a set to avoid updating the same derivation twice.
        newState.changedObservables
                .flatMapTo(mutableSetOf()) { it.derivations }
                .forEach { derivation ->
                    runInAction {
                        updateDerivation(derivation)
                    }
                }
    }
}
