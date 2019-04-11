package magneton.nodes

import magneton.observable.ReactionDisposer
import magneton.observable.reaction
import kotlin.reflect.KClass

expect abstract class Component() : Node {
    internal var disposer: ReactionDisposer?

    abstract fun render(): Node
}

fun <T : Component> Node.component(
        createComponent: () -> T,
        componentClass: KClass<T>
): T {
    val index = GlobalNodeState.get().childIndex++
    val node = children.getOrNull(index)

    if (node != null && node::class == componentClass) {
        @Suppress("UNCHECKED_CAST")
        return node as T
    } else {
        // TODO: optimize with replace
        if (node != null) {
            (node as? Component)?.disposer?.dispose()
            removeChildAt(index)
        }

        val cmp = createComponent()

        cmp.disposer = reaction {
            val prevState = GlobalNodeState.set(index)

            try {
                cmp.render()
            } finally {
                GlobalNodeState.restore(prevState)
            }
        }

        // Add as child after rendering to make sure the component's DOM node exists.
        addChild(index, cmp)
        return cmp
    }
}

/**
 * Note: When passing in an upcasted [createComponent] the component will always be recreated.
 *
 * e.g.:
 * ```
 * val createComponent: () -> Component = ::MyComponent
 * div { component(createComponent) }
 * ```
 *
 * Pass ::MyComponent in directly or make the type of your [createComponent] function specific to avoid performance degradation:
 * ```
 * div { component(::MyComponent) }
 * val createComponent: () -> MyComponent = ::MyComponent
 * div { component(createComponent) }
 * ```
 */
inline fun <reified T : Component> Node.component(
        noinline createComponent: () -> T
): T =
        component(createComponent, T::class)

// TODO: is only used during tests
fun render(component: Component): ReactionDisposer =
        reaction {
            GlobalNodeState.set()

            try {
                component.render()
            } finally {
                GlobalNodeState.clear()
            }
        }
