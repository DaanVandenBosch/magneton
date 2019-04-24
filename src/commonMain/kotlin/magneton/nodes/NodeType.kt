package magneton.nodes

typealias NodeType = Int

// These properties are encapsulated in an object to avoid a bug in the Kotlin JS compiler which
// would initialize strToType too late.
private object NodeTypes {
    var nextNodeType: NodeType = 0

    val strToType: MutableMap<String, NodeType> = mutableMapOf()
}

fun stringToNodeType(string: String) =
        NodeTypes.strToType.getOrPut(string) { NodeTypes.nextNodeType++ }
