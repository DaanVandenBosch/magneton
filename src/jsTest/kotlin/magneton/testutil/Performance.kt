package magneton.testutil

import kotlin.browser.window

actual object Performance {
    actual fun measureTime(block: () -> Unit): Double {
        val start = window.performance.now()
        block()
        return window.performance.now() - start
    }
}
