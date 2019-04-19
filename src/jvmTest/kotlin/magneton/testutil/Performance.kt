package magneton.testutil

import kotlin.system.measureNanoTime

actual object Performance {
    actual fun measureTime(block: () -> Unit): Double =
            measureNanoTime(block).toDouble() / 1_000_000.0
}
