package magneton

import magneton.nodes.NodeState
import magneton.routing.Router
import magneton.routing.defaultRouter
import magneton.style.StyleSheetRegistry

class Context(
        val styleSheetRegistry: StyleSheetRegistry = StyleSheetRegistry(),
        var nodeState: NodeState = NodeState(),
        val router: Router = defaultRouter
)
