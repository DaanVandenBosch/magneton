package magneton

import magneton.nodes.Component
import magneton.nodes.NodeState
import magneton.nodes.notifyDidMount
import magneton.nodes.notifyWillUnmount
import magneton.observable.ReactionDisposer
import magneton.observable.reaction

expect class Application(rootComponent: Component) {
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
            NodeState.Global.set(NodeState())

            try {
                rootComponent.render()
                mount()
                notifyDidMount(rootComponent)
            } finally {
                NodeState.Global.clear()
            }
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
    val app = Application(component)
    app.start()
    return app
}
