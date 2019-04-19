# Magneton

Magneton is a Kotlin frontend web framework inspired by React and MobX. It's in a very early stage of development.

## Example Code

```kotlin
// Define a model,...
class TodoModel(title: String) {
    var title: String by observable(title)
}

// ...a store,...
object TodoStore {
    var todos: List<TodoModel> by observable(listOf())
}

// ...and a view.
class AppComponent : Component() {
    override fun render() = div {
        table {
            TodoStore.todos.forEachIndexed { i, todo ->
                tr {
                    td { text(i.toString()) }
                    td { text(todo.title) }
                }
            }
        }
    }
}

fun main() {
    // Render our view to the DOM.
    renderToDom(document.body!!, AppComponent())

    // Add some to do's, the view will automatically be updated.
    TodoStore.todos = listOf(
            TodoModel("Buy milk"),
            TodoModel("Feed dog"),
            TodoModel("Contribute to Magneton")
    )
}
```

## TODO

Things that still need to be done in no particular order.

- Finish MobX-inspired observables implementation
    - Observable collections
    - Optimize performance
- Automatically generate all DOM manipulation code (see [https://github.com/JetBrains/kotlin/tree/master/libraries/tools/idl2k] for ideas)
- Allow rendering to HTML on the JVM
- Routing
- Event handling
- Documentation
    - Quick start
    - Reference docs
    - Tutorials
    - Optimization
        - Don't let your render function depend on observables that often change but don't cause a change in what's rendered. E.g. a check whether an element in a huge list is the selected element to add a CSS class to it. On an element-by-element basis this rarely changes, so rerendering every element every time the selection changes is very slow. Do the check in a computed value and reference that in your render function.
- Add application-wide node lifecycle hooks to facilitate extension
    - Implement rerendering as reaction to observable changes in terms of these hooks
