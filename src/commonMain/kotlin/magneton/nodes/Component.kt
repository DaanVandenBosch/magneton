package magneton.nodes

import magneton.observable.ReactionDisposer
import magneton.observable.reaction
import kotlin.reflect.KClass

expect abstract class Component() : MNode {
    internal var disposer: ReactionDisposer?

    abstract fun render(): MNode
}

fun Component.div(block: MHTMLDivElement.() -> Unit): MHTMLDivElement =
        addElement(::MHTMLDivElement, block)

fun Component.span(block: MHTMLSpanElement.() -> Unit): MHTMLSpanElement =
        addElement(::MHTMLSpanElement, block)

fun <T : Component> MNode.component(
        createComponent: () -> T,
        componentClass: KClass<T>
): T {
    val index = stack.peek().index++
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
        addChild(index, cmp)

        cmp.disposer = reaction {
            stack.push(Frame(index))

            try {
                cmp.render()
            } finally {
                stack.pop()
            }
        }

        return cmp
    }
}

/**
 * WARNING: When passing in an upcasted [createComponent] the component will always be recreated.
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
inline fun <reified T : Component> MElement.component(
        noinline createComponent: () -> T
): T =
        component(createComponent, T::class)

// TODO: is only used during tests
fun render(component: Component) {
    reaction {
        stack.push(Frame())

        try {
            component.render()
        } finally {
            stack.pop()
        }
    }
}