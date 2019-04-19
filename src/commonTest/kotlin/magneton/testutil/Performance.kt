package magneton.testutil

expect object Performance {
    /**
     * Returns the running time of [block] in ms.
     */
    fun measureTime(block: () -> Unit): Double
}
