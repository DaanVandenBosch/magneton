package magneton.observable

interface ReactionDisposer {
    fun dispose()
}

class Reaction(private val f: () -> Unit) : ReactionDisposer, Derivation {
    override val dependencies: MutableList<Observable> = mutableListOf()

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