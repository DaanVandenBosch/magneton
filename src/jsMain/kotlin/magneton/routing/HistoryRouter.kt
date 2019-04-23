package magneton.routing

import magneton.observable.observable
import kotlin.browser.window

class HistoryRouter : Router {
    private var internalPath by observable(window.location.pathname)
    override val path: String get() = internalPath

    init {
        window.addEventListener("popstate", {
            internalPath = window.location.pathname
        })
    }

    override fun push(path: String, title: String) {
        window.history.pushState(Any(), title, path)
        internalPath = path
    }
}
