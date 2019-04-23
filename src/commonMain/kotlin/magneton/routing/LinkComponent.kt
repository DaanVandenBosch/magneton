package magneton.routing

import magneton.nodes.*
import magneton.style.CSSClassRule
import kotlin.reflect.KClass

expect class LinkComponent<C : Component>(
        path: String,
        title: String,
        cssClass: CSSClassRule?,
        component: (() -> C)?,
        componentClass: KClass<C>?,
        build: (HTMLAnchorElement.() -> Unit)?
) : Component {
    override fun render(): HTMLAnchorElement
}

fun <C : Component> Parent.link(
        path: String,
        title: String,
        cssClass: CSSClassRule? = null,
        component: () -> C,
        componentClass: KClass<C>
) =
        component { LinkComponent(path, title, cssClass, component, componentClass, null) }

inline fun <reified C : Component> Parent.link(
        path: String,
        title: String,
        cssClass: CSSClassRule? = null,
        noinline component: () -> C
) =
        component { LinkComponent(path, title, cssClass, component, C::class, null) }

fun Parent.link(
        path: String,
        title: String,
        cssClass: CSSClassRule? = null,
        build: HTMLAnchorElement.() -> Unit
) =
        component { LinkComponent<Component>(path, title, cssClass, null, null, build) }
