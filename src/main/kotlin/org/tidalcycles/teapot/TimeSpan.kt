package org.tidalcycles.teapot

import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

typealias Time = Double

// TODO: is this the right place for these 3 functions?
fun Time.sam(): Time = floor(this)
fun Time.nextSam(): Time = sam() + 1
fun Time.wholeCycle() = TimeSpan(sam(), nextSam())

data class TimeSpan(val begin: Time, val end: Time) {
    constructor(begin: Number, end: Number) : this(begin.toDouble(), end.toDouble())

    fun spanCycles(): List<TimeSpan> {
        return when {
            begin == end -> emptyList()
            floor(begin) == floor(end) -> listOf(TimeSpan(begin, end))
            else -> listOf(TimeSpan(begin, begin.nextSam())) + TimeSpan(begin.nextSam(), end).spanCycles()
        }
    }

    fun withTime(f: (Time) -> Time): TimeSpan {
        return TimeSpan(f(begin), f(end))
    }

    operator fun times(b: Time): TimeSpan {
        return TimeSpan(begin * b, end * b)
    }

    infix fun intersect(other: TimeSpan): TimeSpan? {
        val start = max(this.begin, other.begin)
        val end = min(this.end, other.end)
        return if (start < end) TimeSpan(start, end) else null
    }

    fun midpoint(): Time = (this.begin + this.end) / 2
}

val innerSpan: (TimeSpan?, TimeSpan?) -> TimeSpan? = { a, _ -> a }
val outerSpan: (TimeSpan?, TimeSpan?) -> TimeSpan? = { _, b -> b }
