package magneton.observableold

/**
 * Base interface of all observable objects.
 */
interface Observable<T> {
    val value: T

    /**
     * [listener] will be called whenever [value] changes.
     */
    fun onChange(listener: ChangeListener<T>): Subscription

    /**
     * [listener] will be called now and whenever [value] changes.
     */
    fun nowAndOnChange(listener: ChangeListener<T>): Subscription {
        listener(value)
        return onChange(listener)
    }
}
