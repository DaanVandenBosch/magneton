package magneton.observable

import magneton.FastSet

interface Derivation : ActionAware {
    /**
     * [Observable] objects being observed by this derivation.
     */
    val dependencies: FastSet<Observable<*>>

    fun update()
}
