package magneton.nodes

expect abstract class Component : Node {
    abstract val root: Node
}

fun Component.div(block: HTMLDivElement.() -> Unit): HTMLDivElement =
        HTMLDivElement().also(block)
