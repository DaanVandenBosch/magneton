package magneton

import magneton.nodes.Component
import magneton.nodes.NodeState
import magneton.nodes.notifyDidMount
import magneton.nodes.notifyWillUnmount
import magneton.observable.ReactionDisposer
import magneton.observable.reaction
import magneton.routing.Router
import magneton.routing.defaultRouter

expect class Application(rootComponent: Component, router: Router) {
    val rootComponent: Component
    val started: Boolean
    val context: Context

    internal var disposer: ReactionDisposer?

    fun start()

    fun stop()

    internal fun mount()

    internal fun unmount()
}

/**
 * This method should be called by all actual versions of [Application.start].
 */
internal fun Application.internalStart() {
    if (!started) {
        rootComponent.context = context

        disposer = reaction {
            context.nodeState = NodeState()
            rootComponent.render()
            mount()
            notifyDidMount(rootComponent)
        }
    }
}

/**
 * This method should be called by all actual versions of [Application.stop].
 */
internal fun Application.internalStop() {
    if (started) {
        disposer?.dispose()
        disposer = null
        notifyWillUnmount(rootComponent)
        unmount()
    }
}

fun render(component: Component): Application {
    val app = Application(component, defaultRouter)
    app.start()
    return app
}
