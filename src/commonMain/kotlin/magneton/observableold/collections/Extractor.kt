package magneton.observableold.collections

import magneton.observableold.Observable

typealias Extractor<T> = (T) -> Array<Observable<*>>
