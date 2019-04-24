package magneton

@Suppress("UNCHECKED_CAST")
internal actual fun <T> Any?.unsafeCast(): T = this as T
