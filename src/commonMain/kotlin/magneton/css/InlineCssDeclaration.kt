package magneton.css

import magneton.observableold.Observable
import magneton.observableold.Subscription

/**
 * When you manually instantiate this class and bind observables to it via methods such as [bindWidth] you must call [unsubscribe] when you're done with it.
 */
class InlineCssDeclaration : CssDeclaration() {
    private val propertySubscriptions = mutableMapOf<String, Subscription>()

    fun unsubscribe() {
        propertySubscriptions.values.forEach { it.unsubscribe() }
    }

    operator fun invoke(block: InlineCssDeclaration.() -> Unit) {
        try {
            doUpdateCss = false
            block(this)
        } finally {
            doUpdateCss = true
            updateCss()
        }
    }

    fun bindWidth(observable: Observable<Length>) {
        propertySubscriptions
                .put("width", observable.nowAndOnChange { width = it })
                ?.unsubscribe()
    }
}
