package magneton.observable

import magneton.GlobalState

private class ActionState {
    val runId = nextRunId++
    val changedObservables: MutableSet<Observable<*>> = mutableSetOf()

    companion object {
        var nextRunId = 0
        val Global = GlobalState<ActionState>()
    }
}

internal class DerivationState {
    val observedObservables: MutableSet<Observable<*>> = mutableSetOf()

    companion object {
        val Global = GlobalState<DerivationState>()
    }
}

internal fun Observable<*>.reportObserved() {
    DerivationState.Global.get()?.observedObservables?.add(this)
}

internal fun Observable<*>.reportChanged() {
    ActionState.Global.get()?.let { state ->
        if (state.runId != lastActionRunId) {
            state.changedObservables.add(this)
            lastActionRunId = state.runId
        }
    }
}

internal expect inline fun removeOldDependencies(state: DerivationState, derivation: Derivation)

internal fun updateDerivation(derivation: Derivation) {
    val newState = DerivationState()
    val prevState = DerivationState.Global.set(newState)

    try {
        derivation.update()
    } finally {
        // Restore previous global state.
        DerivationState.Global.restore(prevState)

        // Update dependencies list.
        removeOldDependencies(newState, derivation)

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

    val retVal = try {
        block()
    } finally {
        ActionState.Global.restore(prevState)
    }

    // Update derivations of changed observables.
    if (newState.changedObservables.isNotEmpty()) {
        runInAction {
            val runId = ActionState.Global.get()!!.runId

            newState.changedObservables.forEach { observable ->
                // We can't use an iterator to loop over observable.derivations because the
                // derivation we're updating inside the loop might remove itself from
                // observable.derivations and thus cause a ConcurrentModificationException.
                var size = observable.derivations.size
                var i = 0

                while (i < size) {
                    val derivation = observable.derivations[i]

                    if (derivation.lastActionRunId != runId) {
                        updateDerivation(derivation)
                        derivation.lastActionRunId = runId

                        // Check whether the derivation no longer depends on observable.
                        if (observable.derivations.size < size) {
                            size = observable.derivations.size
                        } else {
                            i++
                        }
                    } else {
                        i++
                    }
                }
            }
        }
    }

    return retVal
}
