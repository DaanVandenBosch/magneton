package magneton.observable

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SubscriptionTests {
    @Test
    fun unsubscribe() {
        val startSubCount = Subscription.totalSubscriptionCount

        var sub1Unsubbed = false
        val sub1 = Subscription { sub1Unsubbed = true }

        assertEquals(startSubCount + 1, Subscription.totalSubscriptionCount)

        var sub2Unsubbed = false
        val sub2 = Subscription { sub2Unsubbed = true }

        assertEquals(startSubCount + 2, Subscription.totalSubscriptionCount)

        sub1.unsubscribe()

        assertTrue(sub1Unsubbed)
        assertEquals(startSubCount + 1, Subscription.totalSubscriptionCount)

        sub2.unsubscribe()

        assertTrue(sub2Unsubbed)
        assertEquals(startSubCount, Subscription.totalSubscriptionCount)
    }
}
