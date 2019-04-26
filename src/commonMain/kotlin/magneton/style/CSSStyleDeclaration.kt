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
    var textDecorationLine: List<TextDecorationLine>? by prop("text-decoration-line")
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
    var padding: Length? by prop("padding")
    var borderRadius: Length? by prop("border-radius")
    var marginTop: Length? by prop("margin-top")
    var marginBottom: Length? by prop("margin-bottom")
    var marginLeft: Length? by prop("margin-left")
    var marginRight: Length? by prop("margin-right")
    var margin: Length? by prop("margin")
    var flexGrow: Int? by prop("flex-grow")
    var flexDirection: FlexDirection? by prop("flex-direction")
    var flexWrap: FlexWrap? by prop("flex-wrap")
    var gridTemplateColumns: List<GridTrackValue>? by prop("grid-template-columns")
    var gridTemplateRows: List<GridTrackValue>? by prop("grid-template-rows")
    var columnGap: Length? by prop("grid-column-gap")
    var rowGap: Length? by prop("grid-row-gap")
    var gap: Length? by prop("grid-gap")
    var justifyContent: JustifyContent? by prop("justify-content")
    var alignItems: AlignItems? by prop("align-items")
    var userSelect: UserSelect? by prop("user-select", "webkit")
    var cursor: Cursor? by prop("cursor")
    var transform: List<Transform>? by prop("transform")
    var whiteSpace: WhiteSpace? by prop("white-space")
    var opacity: Double? by prop("opacity")
    var overflow: Overflow? by prop("overflow")
    var overflowX: Overflow? by prop("overflow-x")
    var overflowY: Overflow? by prop("overflow-y")
    var animationName: String? by prop("animation-name")
    var animationTimingFunction: AnimationTimingFunction? by prop("animation-timing-function")
    var animationDuration: Duration? by prop("animation-duration")
    var animationDelay: Duration? by prop("animation-delay")
    var animationIterationCount: Int? by prop("animation-iteration-count")
    var animationFillMode: AnimationFillMode? by prop("animation-fill-mode")
    var animationDirection: AnimationDirection? by prop("animation-direction")
    var animationPlayState: AnimationPlayState? by prop("animation-play-state")
    var fill: Color? by prop("fill")

    fun padding(vertical: Length, horizontal: Length) {
        paddingTop = vertical
        paddingBottom = vertical
        paddingLeft = horizontal
        paddingRight = horizontal
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
