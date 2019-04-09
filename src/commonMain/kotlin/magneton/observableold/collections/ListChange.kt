package magneton.observableold.collections

sealed class ListChange<T> {
    class Addition<T>(
            val added: List<T>,
            val from: Int
    ) : ListChange<T>() {
        val to: Int get() = from + added.size
        val addedWithIndex: Iterable<IndexedValue<T>>
            get() = added.zip(from until to) { e, i -> IndexedValue(i, e) }
    }

    class Removal<T>(
            val removed: List<T>,
            val from: Int
    ) : ListChange<T>() {
        val to: Int get() = from + removed.size
        val removedWithIndex: Iterable<IndexedValue<T>>
            get() = removed.zip(from until to) { e, i -> IndexedValue(i, e) }
    }

    class Replacement<T>(
            val removed: List<T>,
            val added: List<T>,
            val from: Int
    ) : ListChange<T>() {
        val removedTo: Int get() = from + removed.size
        val addedTo: Int get() = from + added.size
        val removedWithIndex: Iterable<IndexedValue<T>>
            get() = removed.zip(from until removedTo) { e, i -> IndexedValue(i, e) }
        val addedWithIndex: Iterable<IndexedValue<T>>
            get() = added.zip(from until addedTo) { e, i -> IndexedValue(i, e) }
    }

    class Update<T>(
            val updated: T,
            val index: Int
    ) : ListChange<T>()
}
