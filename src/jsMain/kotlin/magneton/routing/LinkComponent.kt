package magneton.routing

import magneton.nodes.*
import magneton.style.CSSClassRule
import kotlin.reflect.KClass

actual class LinkComponent<C : Component> actual constructor(
        private val path: String,
        private val title: String,
        private val cssClass: CSSClassRule?,
        private val component: (() -> C)?,
        private val componentClass: KClass<C>?,
        private val build: (HTMLAnchorElement.() -> Unit)?
) : Component() {
    init {
        require(component != null && componentClass != null || build != null) {
            "either component and componentClass should be provided or build should be provided"
        }
    }

    actual override fun render() = a(cssClass) {
        href = path
        val self = this@LinkComponent

        // TODO: use event handling system as soon as we have one.
        domNode.onclick = { e ->
            e.preventDefault()
            context!!.router.push(path, self.title)
        }

        if (self.component != null && self.componentClass != null) {
            component(self.component, self.componentClass)
        } else {
            build!!()
        }
    }
}
