package magneton

import magneton.nodes.Component
import magneton.observable.ReactionDisposer
import magneton.routing.Router

actual class Application actual constructor(actual val rootComponent: Component, router: Router) {
    actual val context = Context(router = router)
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
