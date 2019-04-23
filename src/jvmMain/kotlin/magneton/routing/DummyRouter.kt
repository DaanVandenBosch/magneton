package magneton.routing

// TODO implement JVM Router
class DummyRouter : Router {
    override val path: String = ""

    override fun push(path: String, title: String) {
    }
}
