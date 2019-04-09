package magneton.observableold

import magneton.observableold.collections.Extractor
import magneton.observableold.collections.MutableObservableList
import magneton.observableold.collections.MutableWrappedObservableList
import magneton.observableold.vars.CombinedVar
import magneton.observableold.vars.MutableVar
import magneton.observableold.vars.SimpleMutableVar
import magneton.observableold.vars.Var
import kotlin.jvm.JvmName

//
// Vars
//

fun <T> mutableVar(value: T): MutableVar<T> = SimpleMutableVar(value)

fun <R, T1, T2> combine(
        dependency1: Observable<T1>,
        dependency2: Observable<T2>,
        computeValue: (T1, T2) -> R
): Var<R> =
        CombinedVar(
                { computeValue(dependency1.value, dependency2.value) },
                dependency1,
                dependency2
        )

fun <R, T1, T2, T3> combine(
        dependency1: Observable<T1>,
        dependency2: Observable<T2>,
        dependency3: Observable<T3>,
        computeValue: (T1, T2, T3) -> R
): Var<R> =
        CombinedVar(
                { computeValue(dependency1.value, dependency2.value, dependency3.value) },
                dependency1,
                dependency2,
                dependency3
        )

fun <R> combine(computeValue: () -> R, vararg dependencies: Observable<*>): Var<R> =
        CombinedVar(computeValue, *dependencies)

fun <T : Any?> Var<T>.isNull(): Var<Boolean> = map { it == null }
fun <T : Any?> Var<T>.isNotNull(): Var<Boolean> = map { it != null }

//
// Operator Overloads
//

operator fun Var<Int>.plus(other: Var<Int>): Var<Int> =
        CombinedVar({ value + other.value }, this, other)

operator fun Var<Int>.plus(other: Int): Var<Int> =
        CombinedVar({ value + other }, this)

operator fun Var<Int>.minus(other: Var<Int>): Var<Int> =
        CombinedVar({ value - other.value }, this, other)

operator fun Var<Int>.times(other: Var<Int>): Var<Int> =
        CombinedVar({ value * other.value }, this, other)

operator fun Var<Int>.div(other: Var<Int>): Var<Int> =
        CombinedVar({ value / other.value }, this, other)


@JvmName("plusDouble")
operator fun Var<Double>.plus(other: Var<Double>): Var<Double> =
        CombinedVar({ value + other.value }, this, other)

@JvmName("minusDouble")
operator fun Var<Double>.minus(other: Var<Double>): Var<Double> =
        CombinedVar({ value - other.value }, this, other)

@JvmName("timesDouble")
operator fun Var<Double>.times(other: Var<Double>): Var<Double> =
        CombinedVar({ value * other.value }, this, other)

@JvmName("divDouble")
operator fun Var<Double>.div(other: Var<Double>): Var<Double> =
        CombinedVar({ value / other.value }, this, other)

//
// Collections
//

fun <T> observableListOf(extractor: Extractor<T>? = null): MutableObservableList<T> =
        MutableWrappedObservableList(arrayListOf(), extractor)

fun <T> observableListOf(vararg elements: T): MutableObservableList<T> =
        MutableWrappedObservableList(arrayListOf(*elements), null)

fun <T> List<T>.observable(extractor: Extractor<T>? = null): MutableObservableList<T> =
        MutableWrappedObservableList(toMutableList(), extractor)
