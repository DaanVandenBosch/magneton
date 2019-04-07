package magneton.css

interface CssPropertyType {
    val css: String
}

enum class Position(override val css: String) : CssPropertyType {
    Absolute("absolute"), Relative("relative"), Fixed("fixed")
}

enum class Display(override val css: String) : CssPropertyType {
    None("none"), Block("block"), RunIn("run-in"), Flex("flex"), Inline("inline"), InlineBlock("inline-block"), InlineFlex("inline-flex")
}

sealed class FontFamily : CssPropertyType {
    object Serif : FontFamily() {
        override val css = "serif"
    }

    object SansSerif : FontFamily() {
        override val css = "sans-serif"
    }

    object Monospace : FontFamily() {
        override val css = "monospace"
    }

    class Any(name: String) : FontFamily() {
        override val css = "\"$name\""
    }
}

enum class CssUnit(override val css: String) : CssPropertyType {
    Px("px"), Em("em"), Pt("pt"), Ex("ex"), Percent("%")
}

private val defaultUnit = CssUnit.Px

class Length private constructor(override val css: String) : CssPropertyType {
    constructor(number: Number, unit: CssUnit) : this("$number${unit.css}")
    constructor(number: Number) : this(number, defaultUnit)

    companion object {
        val auto = Length("auto")
    }
}

val Number.px get() = Length(this, CssUnit.Px)
val Number.em get() = Length(this, CssUnit.Em)
val Number.pt get() = Length(this, CssUnit.Pt)
val Number.ex get() = Length(this, CssUnit.Ex)
val Number.percent get() = Length(this, CssUnit.Percent)

enum class Origin(override val css: String) : CssPropertyType {
    Top("top"), Bottom("bottom"), Left("left"), Right("right"), Center("center")
}

class Coordinate(origin: Origin, length: Length) : CssPropertyType {
    override val css = "${origin.css} ${length.css}"

    companion object {
        fun top(number: Number) = Coordinate(Origin.Top, Length(number))
        fun left(number: Number) = Coordinate(Origin.Left, Length(number))
    }
}

class BackgroundPosition(horizontal: Coordinate, vertical: Coordinate) : CssPropertyType {
    constructor(horizontal: Number, vertical: Number) :
            this(Coordinate.left(horizontal), Coordinate.top(vertical))

    override val css = "${horizontal.css} ${vertical.css}"
}

class Color private constructor(r: Int, g: Int, b: Int, a: Double) : CssPropertyType {
    init {
        require(r in 0..255) { "r should be between 0 and 255" }
        require(g in 0..255) { "g should be between 0 and 255" }
        require(b in 0..255) { "b should be between 0 and 255" }
        require(a in 0.0..1.0) { "a should be between 0 and 1" }
    }

    override val css = "rgba($r, $g, $b, $a)"

    companion object {
        fun rgb(r: Int, g: Int, b: Int): Color = Color(r, g, b, 1.0)
        fun rgba(r: Int, g: Int, b: Int, a: Double): Color = Color(r, g, b, a)
    }
}

enum class TextAlign(override val css: String) : CssPropertyType {
    Left("left"), Right("right"), Center("center"), Justify("justify"), JustifyAll("justify-all"), Start("start"), End("end"), MatchParent("match-parent")
}

class Url(url: String) : CssPropertyType {
    override val css = "url($url)"
}

enum class FlexDirection(override val css: String) : CssPropertyType {
    Row("row"), RowReverse("row-reverse"), Column("column"), ColumnReverse("column-reverse")
}

enum class FlexWrap(override val css: String) : CssPropertyType {
    Nowrap("nowrap"), Wrap("wrap"), WrapReverse("wrap-reverse")
}

enum class JustifyContent(override val css: String) : CssPropertyType {
    Center("center"), Start("start"), End("end")
}

enum class AlignItems(override val css: String) : CssPropertyType {
    Center("center"), Stretch("stretch")
}

enum class UserSelect(override val css: String) : CssPropertyType {
    None("none"), Auto("auto"), Text("text"), Contain("contain"), All("all")
}

enum class Cursor(override val css: String) : CssPropertyType {
    Pointer("pointer"), Auto("auto"), Default("default"), None("none"), Help("help"), Progress("progress"), Wait("wait"), ContextMenu("context-menu"), Cell("cell"), Crosshair("crosshair"), Text("text"), VerticalText("vertical-text"), Alias("alias"), Copy("copy"), Move("move"), NoDrop("no-drop"), NotAllowed("not-allowed"), Grab("grab"), Grabbing("grabbing")
}

class Transform private constructor(override val css: String) : CssPropertyType {
    companion object {
        fun translate(x: Length, y: Length) = Transform("translate(${x.css}, ${y.css})")
    }
}

enum class WhiteSpace(override val css: String) : CssPropertyType {
    Normal("normal"), Pre("pre"), Nowrap("nowrap"), PreWrap("pre-wrap"), PreLine("pre-line")
}

enum class Overflow(override val css: String) : CssPropertyType {
    Visible("visible"), Hidden("hidden"), Clip("clip"), Scroll("scroll"), Auto("auto")
}
