package magneton.style

actual class StyleSheetRegistry {
    private val internalSheets: MutableSet<StyleSheet> = mutableSetOf()
    actual val sheets: Set<StyleSheet> = internalSheets

    actual fun register(styleSheet: StyleSheet) {
        internalSheets.add(styleSheet)
    }
}
