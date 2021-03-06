package magneton.style

interface StylePropertyType {
    val css: String
}

enum class Position(override val css: String) : StylePropertyType {
    Absolute("absolute"), Relative("relative"), Fixed("fixed")
}

enum class Display(override val css: String) : StylePropertyType {
    None("none"), Block("block"), RunIn("run-in"), Flex("flex"), Grid("grid"), Inline("inline"),
    InlineBlock("inline-block"), InlineFlex("inline-flex"), InlineGrid("inline-grid")
}

sealed class FontFamily : StylePropertyType {
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

enum class StyleUnit(override val css: String) : StylePropertyType {
    Px("px"), Em("em"), Pt("pt"), Ex("ex"), Percent("%")
}

private val defaultUnit = StyleUnit.Px

class Length private constructor(override val css: String) :
        StylePropertyType, GridTrackValue, GridFixedSize {
    constructor(number: Number, unit: StyleUnit) : this("$number${unit.css}")
    constructor(number: Number) : this(number, defaultUnit)

    companion object {
        val auto = Length("auto")
    }
}

val Number.px get() = Length(this, StyleUnit.Px)
val Number.em get() = Length(this, StyleUnit.Em)
val Number.pt get() = Length(this, StyleUnit.Pt)
val Number.ex get() = Length(this, StyleUnit.Ex)
val Number.percent get() = Length(this, StyleUnit.Percent)

enum class Origin(override val css: String) : StylePropertyType {
    Top("top"), Bottom("bottom"), Left("left"), Right("right"), Center("center")
}

class Coordinate(origin: Origin, length: Length) : StylePropertyType {
    override val css = "${origin.css} ${length.css}"

    companion object {
        fun top(number: Number) = Coordinate(Origin.Top, Length(number))
        fun left(number: Number) = Coordinate(Origin.Left, Length(number))
    }
}

class BackgroundPosition(horizontal: Coordinate, vertical: Coordinate) : StylePropertyType {
    constructor(horizontal: Number, vertical: Number) :
            this(Coordinate.left(horizontal), Coordinate.top(vertical))

    override val css = "${horizontal.css} ${vertical.css}"
}

class Color private constructor(override val css: String) : StylePropertyType {
    companion object {
        fun hsl(h: Int, s: Double, l: Double): Color = hsla(h, s, l, 1.0)

        fun hsla(h: Int, s: Double, l: Double, a: Double): Color {
            require(s in 0.0..100.0) { "s should be between 0 and 100" }
            require(l in 0.0..100.0) { "l should be between 0 and 100" }
            require(a in 0.0..1.0) { "a should be between 0 and 1" }
            val hn = h % 360
            return Color(if (a == 1.0) "hsl($hn, $s%, $l%)" else "hsla($hn, $s%, $l%, $a)")
        }

        fun rgb(r: Int, g: Int, b: Int): Color = rgba(r, g, b, 1.0)

        fun rgba(r: Int, g: Int, b: Int, a: Double): Color {
            require(r in 0..255) { "r should be between 0 and 255" }
            require(g in 0..255) { "g should be between 0 and 255" }
            require(b in 0..255) { "b should be between 0 and 255" }
            require(a in 0.0..1.0) { "a should be between 0 and 1" }
            return Color(if (a == 1.0) "rgb($r, $g, $b)" else "rgba($r, $g, $b, $a)")
        }
    }
}

enum class TextAlign(override val css: String) : StylePropertyType {
    Left("left"), Right("right"), Center("center"), Justify("justify"), JustifyAll("justify-all"),
    Start("start"), End("end"), MatchParent("match-parent")
}

enum class TextDecorationLine(override val css: String) : StylePropertyType {
    None("none"), Underline("underline"), Overline("overline"), LineThrough("line-through"),
    Blink("blink")
}

class Url(url: String) : StylePropertyType {
    override val css = "url($url)"
}

enum class FlexDirection(override val css: String) : StylePropertyType {
    Row("row"), RowReverse("row-reverse"), Column("column"), ColumnReverse("column-reverse")
}

enum class FlexWrap(override val css: String) : StylePropertyType {
    Nowrap("nowrap"), Wrap("wrap"), WrapReverse("wrap-reverse")
}

interface GridTrackValue : StylePropertyType {
    companion object {
        fun repeat(columns: Int, vararg sizes: GridFixedSize) =
                RepeatedGridTrackValue(RepeatedGridTrackValueType.Fixed, columns, *sizes)

        fun repeatAutoFill(vararg sizes: GridFixedSize) =
                RepeatedGridTrackValue(RepeatedGridTrackValueType.AutoFill, -1, *sizes)

        fun repeatAutoFit(vararg sizes: GridFixedSize) =
                RepeatedGridTrackValue(RepeatedGridTrackValueType.AutoFit, -1, *sizes)
    }
}

enum class RepeatedGridTrackValueType {
    Fixed, AutoFill, AutoFit
}

class RepeatedGridTrackValue(
        val type: RepeatedGridTrackValueType,
        val columns: Int,
        vararg val sizes: GridFixedSize
) : GridTrackValue {
    override val css = "repeat(" + when (type) {
        RepeatedGridTrackValueType.Fixed -> columns.toString()
        RepeatedGridTrackValueType.AutoFill -> "auto-fill"
        RepeatedGridTrackValueType.AutoFit -> "auto-fit"
    } + ", " + sizes.joinToString(", ") { it.css } + ")"
}

interface GridFixedSize : StylePropertyType

enum class JustifyContent(override val css: String) : StylePropertyType {
    Center("center"), Start("start"), End("end")
}

enum class AlignItems(override val css: String) : StylePropertyType {
    Center("center"), Stretch("stretch")
}

enum class UserSelect(override val css: String) : StylePropertyType {
    None("none"), Auto("auto"), Text("text"), Contain("contain"), All("all")
}

enum class Cursor(override val css: String) : StylePropertyType {
    Pointer("pointer"), Auto("auto"), Default("default"), None("none"), Help("help"),
    Progress("progress"), Wait("wait"), ContextMenu("context-menu"), Cell("cell"),
    Crosshair("crosshair"), Text("text"), VerticalText("vertical-text"), Alias("alias"),
    Copy("copy"), Move("move"), NoDrop("no-drop"), NotAllowed("not-allowed"), Grab("grab"),
    Grabbing("grabbing")
}

class Transform private constructor(override val css: String) : StylePropertyType {
    companion object {
        fun translate(x: Length, y: Length) = Transform("translate(${x.css}, ${y.css})")
    }
}

enum class WhiteSpace(override val css: String) : StylePropertyType {
    Normal("normal"), Pre("pre"), Nowrap("nowrap"), PreWrap("pre-wrap"), PreLine("pre-line")
}

enum class Overflow(override val css: String) : StylePropertyType {
    Visible("visible"), Hidden("hidden"), Clip("clip"), Scroll("scroll"), Auto("auto")
}

enum class TimeUnit(override val css: String) : StylePropertyType {
    Seconds("s"), MilliSeconds("ms")
}

class Duration private constructor(override val css: String) : StylePropertyType {
    constructor(number: Number, unit: TimeUnit) : this("$number${unit.css}")
}

class AnimationTimingFunction private constructor(override val css: String) : StylePropertyType {
    companion object {
        fun ease() = AnimationTimingFunction("ease")
        fun easeOut() = AnimationTimingFunction("ease-out")
        fun easeIn() = AnimationTimingFunction("ease-in")
        fun faseInOut() = AnimationTimingFunction("ease-in-out")
        fun linear() = AnimationTimingFunction("linear")
        fun cubicBezier(x1: Double, y1: Double, x2: Double, y2: Double) =
                AnimationTimingFunction("cubic-bezier($x1, $y1, $x2, $y2)")
    }
}

enum class AnimationFillMode(override val css: String) : StylePropertyType {
    Forwards("forwards"), Backwards("backwards"), Both("both"), None("none")
}

enum class AnimationDirection(override val css: String) : StylePropertyType {
    Normal("normal"), Reverse("reverse"), Alternate("alternate"),
    AlternateReverse("alternate-reverse")
}

enum class AnimationPlayState(override val css: String) : StylePropertyType {
    Paused("paused"), Running("running")
}
