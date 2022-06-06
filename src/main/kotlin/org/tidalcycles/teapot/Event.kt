package org.tidalcycles.teapot

// TODO: differentiation between discrete event and continuous event (without whole)
data class Event<T>(val whole: TimeSpan?, val part: TimeSpan, val value: T) {
    fun <O> withValue(f: (T) -> O): Event<O> {
        return Event(this.whole, this.part, f(this.value))
    }

    fun withSpan(f: (TimeSpan) -> TimeSpan): Event<T> {
        return Event(this.whole?.let(f), f(this.part), this.value)
    }

    fun isOnset() = whole?.begin == part.begin

}