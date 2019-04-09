package magneton.observableold.vars

import magneton.observableold.ChangeListener
import magneton.observableold.Observable
import magneton.observableold.Subscription

open class CombinedVar<T>(
        private val computeValue: () -> T,
        private vararg val dependencies: Observable<*>
) : AbstractVar<T>() {
    @Suppress("UNCHECKED_CAST")
    override val value
        // When we're not subscribed to [dependencies] we need to call [computeValue] each time because the values of [dependencies] might have changed.
        get() = if (dependencySubscriptions == null) {
            computeValue()
        } else {
            mutableValue as T
        }

    private var dependencySubscriptions: Array<Subscription>? = null
    private var mutableValue: T? = null

    // Lazily bound to [dependencies] to avoid memory leaks.
    // See http://tomasmikula.github.io/blog/2015/02/10/val-a-better-observablevalue.html
    override fun onChange(listener: ChangeListener<T>): Subscription {
        if (dependencySubscriptions == null) {
            mutableValue = computeValue()
            dependencySubscriptions = Array(dependencies.size) {
                dependencies[it].onChange {
                    mutableValue = computeValue()
                    callListeners()
                }
            }
        }

        changeListeners += listener

        return Subscription {
            changeListeners -= listener

            if (changeListeners.isEmpty()) {
                dependencySubscriptions?.forEach { it.unsubscribe() }
                dependencySubscriptions = null
            }
        }
    }
}
