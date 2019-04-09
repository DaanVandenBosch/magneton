package magneton.observableold

class Subscription(val f: () -> Unit) {
    companion object {
        // Used only in tests.
        internal var totalSubscriptionCount = 0
    }

    init {
        ++totalSubscriptionCount
    }

    fun unsubscribe() {
        --totalSubscriptionCount
        f()
    }
}
