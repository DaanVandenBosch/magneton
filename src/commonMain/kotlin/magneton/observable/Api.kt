package magneton.observable

fun <T> observable(value: T) = ObservableValue(value)

fun <T> computed(block: () -> T): ComputedValue<T> = ComputedValue(block)

fun reaction(block: () -> Unit): ReactionDisposer = Reaction(block)

fun <T> action(block: () -> T): T = runInAction(block)
