package magneton.routing

interface Router {
    val path: String

    fun matches(path: String, exact: Boolean): Boolean =
            if (exact) this.path == path
            else this.path.startsWith(path)

    fun push(path: String, title: String)
}

expect val defaultRouter: Router
