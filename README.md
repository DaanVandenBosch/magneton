# Magneton

Magneton is a Kotlin frontend web framework inspired by React and MobX. It is in a very early stage of development.

## Example Code

```kotlin
class TodoModel(title: String) {
    var title: String by observable(title)
}

object TodoStore {
    var todos: List<TodoModel> by observable(listOf())
}

class AppComponent : Component {
    override fun render() = div {
        table {
            TodoStore.todos.forEachIndexed { (i, todo) ->
                tr {
                    td { text(i) }
                    td { text(todo.title) }
                }
            }
        }
    }
}

fun main() {
    renderToDom(document.body!!, AppComponent())
}
```

## TODO

- Finish MobX-inspired observables implementation
    - Observable collections
    - Performance
- Allow style definitions inside component code (should still be rendered to CSS rules in a stylesheet to support pseudo classes?)
- Automatically generate all DOM manipulation code (see [https://github.com/JetBrains/kotlin/tree/master/libraries/tools/idl2k] for ideas)
- Allow rendering to HTML on the JVM
- Routing
- Event handling
- Documentation
    - Quick start
    - Reference docs
    - Tutorials
