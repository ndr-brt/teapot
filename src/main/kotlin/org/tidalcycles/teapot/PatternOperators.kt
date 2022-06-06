package org.tidalcycles.teapot

operator fun Number.plus(pattern: Pattern<Double>) = pattern.withValue { this.toDouble() + it }
operator fun Number.minus(pattern: Pattern<Double>) = pattern.withValue { this.toDouble() - it }
operator fun Number.times(pattern: Pattern<Double>) = pattern.withValue { this.toDouble() * it }
operator fun Number.div(pattern: Pattern<Double>) = pattern.withValue { this.toDouble() / it  }

operator fun Pattern<Double>.plus(value: Number) = this.withValue { it + value.toDouble() }
operator fun Pattern<Double>.minus(value: Number) = this.withValue { it - value.toDouble() }
operator fun Pattern<Double>.times(value: Number) = this.withValue { it * value.toDouble() }
operator fun Pattern<Double>.div(value: Number) = this.withValue { it / value.toDouble() }