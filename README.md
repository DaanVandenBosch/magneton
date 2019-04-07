# Magneton

Magneton is a Kotlin frontend web framework inspired by JavaFX and TornadoFX. It is in a very early stage of development.

## Example Code

```kotlin
object Store {
    val numbers = observableListOf(1, 2, 3, 4, 5)
}

class AppComponent : Component {
    override val root = main {
        div {
            // The UI will reflect all future changes to Store.numbers.
            children = numbers.map { span { text(it) } }
        }
    }
}

fun main() {
    renderToDom(document.body!!, AppComponent())
}

```

## TODO

- Make MutableObservableList sortable
- Make MutableObservableList filterable
- Allow style definitions inside component code (should still be rendered to CSS rules in a stylesheet?)
- Automatically generate all DOM manipulation code (see https://github.com/JetBrains/kotlin/tree/master/libraries/tools/idl2k for ideas)
- Allow rendering to HTML on the JVM
- Routing
