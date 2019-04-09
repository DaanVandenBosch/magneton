package magneton.observableold.vars

import magneton.observableold.ChangeListener
import magneton.observableold.Observable
import magneton.observableold.Subscription

open class FlatMappedVar<T, U>(
        private val dependency: Observable<T>,
        private val f: (T) -> Observable<U>
) : AbstractVar<U>() {
    override val value
        // When we're not subscribed to [dependency] we need to recompute [f] each time because the value of [dependency] might have changed.
        get() = if (dependencySubscription == null) {
            f(dependency.value).value
        } else {
            computedVar!!.value
        }

    private var dependencySubscription: Subscription? = null
    private var computedVar: Observable<U>? = null
    private var computedVarSubscription: Subscription? = null

    // Lazily bound to [dependency] to avoid memory leaks.
    // See http://tomasmikula.github.io/blog/2015/02/10/val-a-better-observablevalue.html
    override fun onChange(listener: ChangeListener<U>): Subscription {
        if (dependencySubscription == null) {
            dependencySubscription = dependency.onChange {
                computeVarAndSubscribe()
                callListeners()
            }

            computeVarAndSubscribe()
        }

        changeListeners += listener

        return Subscription {
            changeListeners -= listener

            if (changeListeners.isEmpty()) {
                dependencySubscription?.unsubscribe()
                dependencySubscription = null
                computedVarSubscription?.unsubscribe()
                computedVarSubscription = null
                computedVar = null
            }
        }
    }

    private fun computeVarAndSubscribe() {
        computedVarSubscription?.unsubscribe()
        computedVar = f(dependency.value)
        computedVarSubscription = computedVar!!.onChange {
            callListeners()
        }
    }
}
