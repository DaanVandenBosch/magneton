package magneton.observable

import magneton.FastSet

interface ReactionDisposer {
    fun dispose()
}

class Reaction(private val f: () -> Unit) : ReactionDisposer, Derivation {
    override val dependencies: FastSet<Observable<*>> = FastSet()
    override var lastActionRunId: Int = -1

    init {
        updateDerivation(this)
    }

    override fun update() {
        f()
    }

    override fun dispose() {
        dependencies.forEach { it.derivations.remove(this) }
        dependencies.clear()
    }
}
