@file:Suppress("DANGEROUS_CHARACTERS")

package org.tidalcycles.teapot

data class Pattern<T>(val query: (TimeSpan) -> List<Event<T>>) {
    infix fun fast(factor: Number): Pattern<T> {
        return withQueryTime { time -> time * factor.toDouble() }
            .withEventTime { time -> time / factor.toDouble() }
    }

    infix fun fast(pattern: Pattern<out Number>): Pattern<T> = pattern.withValue(this::fast).outerJoin()
    infix fun fast(miniNotation: String): Pattern<T> = fast(miniNotation.patternDouble())

    infix fun slow(factor: Number): Pattern<T> {
        return withQueryTime { time -> time / factor.toDouble() }
            .withEventTime { time -> time * factor.toDouble() }
    }

    infix fun slow(pattern: Pattern<out Number>): Pattern<T> = pattern.withValue(this::slow).outerJoin()
    infix fun slow(miniNotation: String): Pattern<T> = slow(miniNotation.patternDouble())

    infix fun rotL(factor: Number): Pattern<T> {
        return withQueryTime { time -> time + factor.toDouble() }
            .withEventTime {  time -> time - factor.toDouble()}
    }

    infix fun rotL(pattern: Pattern<out Number>): Pattern<T> = pattern.withValue(this::rotL).outerJoin()

    infix fun rotR(factor: Number): Pattern<T> {
        return withQueryTime { time -> time - factor.toDouble() }
            .withEventTime {  time -> time + factor.toDouble() }
    }

    infix fun rotR(pattern: Pattern<out Number>): Pattern<T> = pattern.withValue(this::rotR).outerJoin()

    infix fun superimpose(function: (Pattern<T>) -> Pattern<T>): Pattern<T> = stack(this, function(this))

    fun splitQueries(): Pattern<T> {
        return Pattern { span -> span.spanCycles().flatMap { subspan -> this.query(subspan) } }
    }

    fun <O> bindWhole(chooseWhole: (TimeSpan?, TimeSpan?) -> TimeSpan?, func: (T) -> Pattern<O>): Pattern<O> {
        return Pattern { span ->
            this.query(span)
                .flatMap { innerEvent -> func(innerEvent.value).query(innerEvent.part)
                    .map { outerEvent ->
                        Event(chooseWhole(innerEvent.whole, outerEvent.whole), outerEvent.part, outerEvent.value)
                    }
                }
        }
    }

    fun <O> withValue(f: (T) -> O): Pattern<O> {
        return Pattern { span -> this.query(span).map { it.withValue(f) } }
    }

    private fun withQueryTime(f: (Time) -> Time): Pattern<T> {
        return Pattern { span -> this.query(span.withTime(f)) }
    }

    private fun withEventTime(f: (Time) -> Time): Pattern<T> {
        return this.withEventSpan { span -> span.withTime(f) }
    }

    private fun withEventSpan(f: (TimeSpan) -> TimeSpan): Pattern<T> {
        return Pattern { span -> this.query(span).map { event -> event.withSpan(f) } }
    }

    override fun toString(): String {
        return this.query(TimeSpan(0, 1)).joinToString("\n") { it.toString() }
    }
}

fun <T> Pattern<Pattern<T>>.innerJoin(): Pattern<T> {
    return this.bindWhole(innerSpan) { it }
}

fun <T> Pattern<Pattern<T>>.outerJoin(): Pattern<T> {
    return this.bindWhole(outerSpan) { it }
}

infix fun Pattern<Values>.`#`(pattern: Pattern<Values>): Pattern<Values> {
    return this.withValue { leftValues -> pattern.withValue { rightValues -> leftValues + rightValues } }.innerJoin()
}

infix fun Pattern<Values>.`+|`(pattern: Pattern<Values>): Pattern<Values> {
    return this.withValue { leftValues -> pattern.withValue { rightValues -> leftValues.sumValues(rightValues) } }.outerJoin()
}

infix fun Pattern<Values>.`|+`(pattern: Pattern<Values>): Pattern<Values> {
    return this.withValue { leftValues -> pattern.withValue { rightValues -> leftValues.sumValues(rightValues) } }.innerJoin()
}

infix fun Pattern<Values>.`-|`(pattern: Pattern<Values>): Pattern<Values> {
    return this.withValue { leftValues -> pattern.withValue { rightValues -> leftValues.subtractValues(rightValues) } }.outerJoin()
}

infix fun Pattern<Values>.`|-`(pattern: Pattern<Values>): Pattern<Values> {
    return this.withValue { leftValues -> pattern.withValue { rightValues -> leftValues.subtractValues(rightValues) } }.innerJoin()
}

infix fun Values.sumValues(other: Values): Values {
    val newKeys = other.keys - this.keys
    val newEntries = other.filterKeys { key -> key in newKeys }
    return this.mapValues { entry ->
        when (entry.value) {
            is Int -> (entry.value as Int).toInt() + (other[entry.key]?.let { it as Int } ?: 0).toInt()
            is Double -> (entry.value as Double).toDouble() + (other[entry.key]?.let { it as Double } ?: 0).toDouble()
            else -> entry.value
        }
    }.plus(newEntries)
}

infix fun Values.subtractValues(other: Values): Values {
    return other
        .map { entry -> entry.key to negate(entry.value) }
        .toMap()
        .let { this.sumValues(it) }
}

fun negate(value: Any): Any {
    return when(value) {
        is Double -> value.unaryMinus()
        is Int -> value.unaryMinus()
        else -> value
    }
}