package magneton.nodes

import magneton.observable.ReactionDisposer
import magneton.observable.reaction
import magneton.unsafeCast
import kotlin.reflect.KClass

internal val componentNodeType: NodeType = stringToNodeType("magneton.nodes.Component")

expect abstract class Component() : Parent {
    internal var disposer: ReactionDisposer?

    override val nodeType: NodeType

    /**
     * Called right before the component is first added to the node tree and after each change to one of its observable dependencies.
     */
    abstract fun render(): Node?
}

fun <T : Component> Parent.component(
        createComponent: () -> T,
        componentClass: KClass<T>
): T {
    val ctx = context!!
    val index = ctx.nodeState.childIndex++
    val node = children.getOrNull(index)

    if (node != null && node.kClass == componentClass) {
        return node.unsafeCast()
    } else {
        // TODO: optimize with replace
        if (node != null) {
            notifyWillUnmount(node)

            if (node.nodeType == componentNodeType) {
                node.unsafeCast<Component>().disposer?.dispose()
            }

            removeChildAt(index)
        }

        val cmp = createComponent()
        cmp.context = ctx

        cmp.disposer = reaction {
            val prevState = ctx.nodeState
            val state = NodeState()
            ctx.nodeState = state

            try {
                cmp.render()

                // Clean up implicitly removed child nodes.
                if (isMounted) {
                    for (i in state.childIndex..cmp.children.lastIndex) {
                        notifyWillUnmount(cmp.children[i])
                    }
                }

                cmp.removeChildrenFrom(state.childIndex)
            } finally {
                ctx.nodeState = prevState
            }
        }

        // Add as child after rendering to make sure the component's DOM node exists and can be appended to the parent's DOM node.
        addChild(index, cmp)

        if (isMounted) {
            notifyDidMount(cmp)
        }

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
inline fun <reified T : Component> Parent.component(
        noinline createComponent: () -> T
): T =
        component(createComponent, T::class)
