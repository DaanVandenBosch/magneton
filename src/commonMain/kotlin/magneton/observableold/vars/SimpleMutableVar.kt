package magneton.observableold.vars

class SimpleMutableVar<T>(value: T) : MutableVar<T>, AbstractVar<T>() {
    override var value = value
        set(value) {
            if (value != field) {
                field = value
                callListeners()
            }
        }
}
