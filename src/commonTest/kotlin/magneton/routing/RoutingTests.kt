package magneton.routing

import magneton.Application
import magneton.nodes.Component
import magneton.nodes.Parent
import magneton.nodes.div
import magneton.observable.observable
import kotlin.test.Test
import kotlin.test.assertTrue

class RoutingTests {
    @Test
    fun route_should_render_when_path_matches() {
        class Inner : Component() {
            override fun render() = div { }
        }

        val cmp = object : Component() {
            override fun render() = route("/match", true, ::Inner)
        }

        val router = TestRouter()
        Application(cmp, router).start()

        assertTrue((cmp.children[0] as Parent).children.isEmpty())

        router.path = "/match"

        assertTrue((cmp.children[0] as Parent).children.isNotEmpty())

        router.path = "/no-match"

        assertTrue((cmp.children[0] as Parent).children.isEmpty())
    }

    class TestRouter : Router {
        override var path: String by observable("")

        override fun push(path: String, title: String) {}
    }
}
