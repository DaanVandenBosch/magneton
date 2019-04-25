@file:JvmName("RemoveOldDependencies")

package magneton.observable

@Suppress("NOTHING_TO_INLINE")
internal actual inline fun removeOldDependencies(state: DerivationState, derivation: Derivation) {
    derivation.dependencies.removeAll { dependency ->
        if (dependency !in state.observedObservables) {
            dependency.derivations.remove(derivation)
            true
        } else {
            false
        }
    }
}
