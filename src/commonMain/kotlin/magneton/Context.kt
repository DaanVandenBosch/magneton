package magneton

import magneton.nodes.NodeState
import magneton.style.StyleSheetRegistry

class Context {
    val styleSheetRegistry = StyleSheetRegistry()
    var nodeState = NodeState()
}
