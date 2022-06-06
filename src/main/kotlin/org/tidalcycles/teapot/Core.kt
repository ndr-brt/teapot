package org.tidalcycles.teapot

import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.sin

typealias Values = Map<String, Any>

fun <T> slowcat(vararg patterns: Pattern<T>): Pattern<T> {
    return Pattern { span ->
        val n = patterns.size
        val cyc = floor(span.begin).toInt()
        val index = cyc % n

        val pat = patterns[if (index < 0) index + n else index]
        pat.query(span)
    }.splitQueries()
}

fun <T> fastcat(vararg patterns: Pattern<T>): Pattern<T> {
    return slowcat(*patterns).fast(patterns.size)
}

fun <T> stack(vararg patterns: Pattern<T>): Pattern<T> {
    return Pattern { span ->
        patterns.flatMap { it.query(span) }
    }
}

fun <T> silence(): Pattern<T> = Pattern { emptyList() }

fun <T> pure(value: T): Pattern<T> {
    return Pattern { span ->
        span.spanCycles().map { sub -> Event(sub.begin.wholeCycle(), sub, value) }
    }
}

