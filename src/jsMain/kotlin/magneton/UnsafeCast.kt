package magneton

import kotlin.js.unsafeCast as kotlinUnsafeCast

@Suppress("NOTHING_TO_INLINE")
internal actual inline fun <T> Any?.unsafeCast(): T = kotlinUnsafeCast<T>()
