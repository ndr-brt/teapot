package org.tidalcycles.teapot

typealias Parameter = (Any) -> Pattern<Values>

val gain = doubleParameter("gain")
val n = doubleParameter("n")
val lpf = doubleParameter("cutoff")
val pan = doubleParameter("pan")
val speed = doubleParameter("speed")

val s = stringParameter("s")

fun doubleParameter(name: String): Parameter {
    return { value ->
        when (value) {
            is Number -> pure(mapOf(name to value.toDouble()))
            is String -> doubleParameter(name)(value.patternDouble())
            is Pattern<*> -> value.withValue { doubleParameter(name)(it!!) }.innerJoin()
            else -> throw RuntimeException("Parameter $name cannot be of type ${value.javaClass}")
        }
    }
}

fun intParameter(name: String): Parameter {
    return { value ->
        when (value) {
            is Number -> pure(mapOf(name to value))
            is String -> intParameter(name)(value.patternInt())
            is Pattern<*> -> value.withValue { intParameter(name)(it!!) }.innerJoin()
            else -> throw RuntimeException("Parameter $name cannot be of type ${value.javaClass}")
        }
    }
}

fun stringParameter(name: String): Parameter {
    return { value ->
        when (value) {
            is String -> value.patternString().withValue { mapOf(name to it) }
            is Pattern<*> -> value.withValue { stringParameter(name)(it!!) }.innerJoin()
            else -> throw RuntimeException("Parameter $name cannot be of type ${value.javaClass}")
        }
    }
}

