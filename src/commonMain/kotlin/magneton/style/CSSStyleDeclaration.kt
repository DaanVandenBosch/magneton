package magneton.style

abstract class CSSStyleDeclaration {
    internal var doUpdateCss = true
    internal val properties = mutableMapOf<String, CSSProperty<*>>()

    private var _css: String = ""
    val css: String get() = _css

    var position: Position? by prop("position")
    var display: Display? by prop("display")
    var zIndex: Int? by prop("z-index")
    var fontFamily: List<FontFamily>? by prop("font-family")
    var fontSize: Length? by prop("font-size")
    var fontWeight: Int? by prop("font-weight")
    var letterSpacing: Length? by prop("letter-spacing")
    var color: Color? by prop("color")
    var textAlign: TextAlign? by prop("text-align")
    var backgroundColor: Color? by prop("background-color")
    var backgroundImage: Url? by prop("background-image")
    var backgroundPosition: BackgroundPosition? by prop("background-position")
    var top: Length? by prop("top")
    var bottom: Length? by prop("bottom")
    var left: Length? by prop("left")
    var right: Length? by prop("right")
    var width: Length? by prop("width")
    var minWidth: Length? by prop("min-width")
    var height: Length? by prop("height")
    var minHeight: Length? by prop("min-height")
    var paddingTop: Length? by prop("padding-top")
    var paddingBottom: Length? by prop("padding-bottom")
    var paddingLeft: Length? by prop("padding-left")
    var paddingRight: Length? by prop("padding-right")
    var borderRadius: Length? by prop("border-radius")
    var marginTop: Length? by prop("margin-top")
    var marginBottom: Length? by prop("margin-bottom")
    var marginLeft: Length? by prop("margin-left")
    var marginRight: Length? by prop("margin-right")
    var flexGrow: Int? by prop("flex-grow")
    var flexDirection: FlexDirection? by prop("flex-direction")
    var flexWrap: FlexWrap? by prop("flex-wrap")
    var justifyContent: JustifyContent? by prop("justify-content")
    var alignItems: AlignItems? by prop("align-items")
    var userSelect: UserSelect? by prop("user-select", "webkit")
    var cursor: Cursor? by prop("cursor")
    var transform: List<Transform>? by prop("transform")
    var whiteSpace: WhiteSpace? by prop("white-space")
    var opacity: Double? by prop("opacity")
    var overflow: Overflow? by prop("overflow")

    fun padding(all: Length) {
        paddingTop = all
        paddingBottom = all
        paddingLeft = all
        paddingRight = all
    }

    fun padding(vertical: Length, horizontal: Length) {
        paddingTop = vertical
        paddingBottom = vertical
        paddingLeft = horizontal
        paddingRight = horizontal
    }

    fun margin(all: Length) {
        marginTop = all
        marginBottom = all
        marginLeft = all
        marginRight = all
    }

    fun margin(vertical: Length, horizontal: Length) {
        marginTop = vertical
        marginBottom = vertical
        marginLeft = horizontal
        marginRight = horizontal
    }

    private fun <T> prop(name: String): CSSProperty<T> =
            CSSProperty(name, emptyList(), null)

    private fun <T> prop(name: String, vararg vendorPrefixes: String): CSSProperty<T> =
            CSSProperty(name, vendorPrefixes.toList(), null)

    internal fun updateCss() {
        if (!doUpdateCss) return

        val sb = StringBuilder()

        for (p in properties.values) {
            sb.append("    ")
            p.toCss(sb)
            sb.append('\n')
        }

        _css = sb.toString()
    }
}
