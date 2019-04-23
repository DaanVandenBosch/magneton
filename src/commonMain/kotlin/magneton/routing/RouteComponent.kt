package magneton.routing

import magneton.nodes.Component
import magneton.nodes.Parent
import magneton.nodes.component
import kotlin.reflect.KClass

class RouteComponent<C : Component>(
        private val path: String,
        private val exact: Boolean,
        private val component: () -> C,
        private val componentClass: KClass<C>
) : Component() {
    override fun render() =
            if (context!!.router.matches(path, exact)) {
                component(component, componentClass)
            } else {
                null
            }
}

fun <C : Component> Parent.route(
        path: String,
        exact: Boolean = false,
        component: () -> C,
        componentClass: KClass<C>
) =
        component { RouteComponent(path, exact, component, componentClass) }

inline fun <reified C : Component> Parent.route(
        path: String,
        exact: Boolean = false,
        noinline component: () -> C
) =
        component { RouteComponent(path, exact, component, C::class) }
