package magneton

import magneton.nodes.Component
import magneton.observable.ReactionDisposer
import org.w3c.dom.Node as DomNode

actual class Application(
        actual val rootComponent: Component,
        private val domNode: DomNode?
) {
    actual constructor(rootComponent: Component) : this(rootComponent, null)

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
        if (domNode != null) {
            context.styleSheetRegistry.appendToDom(domNode)
            domNode.appendChild(rootComponent.domNode!!)
        }
    }

    internal actual fun unmount() {
        if (domNode != null) {
            context.styleSheetRegistry.removeFromDom()
            domNode.removeChild(rootComponent.domNode!!)
        }
    }
}

fun renderToDom(domNode: DomNode, component: Component): Application {
    val app = Application(component, domNode)
    app.start()
    return app
}
