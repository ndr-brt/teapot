package org.tidalcycles.teapot

import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

val sine: Pattern<Double> = signal { span -> (sin(PI * 2 * span.midpoint()) + 1) / 2 }
val cosine: Pattern<Double> = sine rotL 0.25
val rand: Pattern<Double> = signal { Random.nextDouble() }

private fun <T> signal(function: (TimeSpan) -> T): Pattern<T> = Pattern { span ->
    listOf(Event(null, span, function(span)))
}