package magneton.style

expect class StyleSheetRegistry() {
    val sheets: Set<StyleSheet>

    fun register(styleSheet: StyleSheet)
}
