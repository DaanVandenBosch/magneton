package magneton.observable

interface Derivation : ActionAware {
    /**
     * [Observable] objects being observed by this derivation.
     */
    val dependencies: MutableList<Observable<*>>

    fun update()
}
