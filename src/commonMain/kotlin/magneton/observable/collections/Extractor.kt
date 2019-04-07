package magneton.observable.collections

import magneton.observable.Observable

typealias Extractor<T> = (T) -> Array<Observable<*>>
