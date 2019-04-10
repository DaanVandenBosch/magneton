package magneton.nodes

internal class Frame(
        var index: Int = 0
)

// TODO: optimize stack (don't use an actual data structure)
internal val stack = mutableListOf<Frame>()

internal fun <T> MutableList<T>.push(element: T): Boolean = add(element)
internal fun <T> MutableList<T>.pop(): T = removeAt(lastIndex)
internal fun <T> MutableList<T>.peek(): T = last()
