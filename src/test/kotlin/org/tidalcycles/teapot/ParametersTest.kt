package org.tidalcycles.teapot

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ParametersTest {

    @Test
    fun `double parameter should set a parameter with double value`() {
        val pattern = s("bd") `#` doubleParameter("pan")(0.5)

        assertThat(pattern.query(TimeSpan(0, 1)))
            .containsExactly(
                Event(TimeSpan(0, 1), TimeSpan(0, 1), mapOf("s" to "bd", "pan" to 0.5))
            )
    }

    @Test
    fun `double parameter should be patternable`() {
        val pattern = fastcat(s("bd"), s("sd")) `#` doubleParameter("pan")(fastcat(pure(0.5), pure(1.0)))

        assertThat(pattern.query(TimeSpan(0, 1)))
            .containsExactly(
                Event(TimeSpan(0, 0.5), TimeSpan(0, 0.5), mapOf("s" to "bd", "pan" to 0.5)),
                Event(TimeSpan(0.5, 1), TimeSpan(0.5, 1), mapOf("s" to "sd", "pan" to 1.0))
            )
    }

    @Test
    fun `int parameter should set a parameter with int value`() {
        val pattern = s("bd") `#` intParameter("ip")(3)

        assertThat(pattern.query(TimeSpan(0, 1)))
            .containsExactly(
                Event(TimeSpan(0, 1), TimeSpan(0, 1), mapOf("s" to "bd", "ip" to 3))
            )
    }

    @Test
    fun `int parameter should be patternable`() {
        val pattern = intParameter("ip")(fastcat(pure(1), pure(2)))

        assertThat(pattern.query(TimeSpan(0, 1)))
            .containsExactly(
                Event(TimeSpan(0, 0.5), TimeSpan(0, 0.5), mapOf("ip" to 1)),
                Event(TimeSpan(0.5, 1), TimeSpan(0.5, 1), mapOf("ip" to 2))
            )
    }

    @Test
    fun `string parameter should set a parameter with string value`() {
        val pattern = s("bd") `#` stringParameter("sp")("any")

        assertThat(pattern.query(TimeSpan(0, 1)))
            .containsExactly(
                Event(TimeSpan(0, 1), TimeSpan(0, 1), mapOf("s" to "bd", "sp" to "any"))
            )
    }

    @Test
    fun `string parameter should be patternable`() {
        val pattern = fastcat(s("bd"), s("sd")) `#` stringParameter("sp")(fastcat(pure("any"), pure("other")))

        assertThat(pattern.query(TimeSpan(0, 1)))
            .containsExactly(
                Event(TimeSpan(0, 0.5), TimeSpan(0, 0.5), mapOf("s" to "bd", "sp" to "any")),
                Event(TimeSpan(0.5, 1), TimeSpan(0.5, 1), mapOf("s" to "sd", "sp" to "other"))
            )
    }

}