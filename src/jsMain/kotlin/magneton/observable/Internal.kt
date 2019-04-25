package magneton.observable

@Suppress("NOTHING_TO_INLINE")
internal actual inline fun removeOldDependencies(state: DerivationState, derivation: Derivation) {
    derivation.dependencies.forEach { dependency ->
        if (dependency !in state.observedObservables) {
            derivation.dependencies.remove(dependency)
            dependency.derivations.remove(derivation)
        }
    }
}
