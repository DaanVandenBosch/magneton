package magneton.observableold.collections

interface MutableObservableList<T> : ObservableList<T>, MutableList<T> {
    fun replaceAll(elements: Collection<T>)

    override fun subList(fromIndex: Int, toIndex: Int): MutableObservableList<T>
}
