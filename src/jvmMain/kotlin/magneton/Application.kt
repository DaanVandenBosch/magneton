package magneton

import magneton.nodes.Component
import magneton.observable.ReactionDisposer

actual class Application actual constructor(actual val rootComponent: Component) {
    actual val context = Context()
    actual val started get() = disposer != null
    actual var disposer: ReactionDisposer? = null

    actual fun start() {
        internalStart()
    }

    actual fun stop() {
        internalStop()
    }

    internal actual fun mount() {
        // Do nothing.
    }

    internal actual fun unmount() {
        // Do nothing.
    }
}
