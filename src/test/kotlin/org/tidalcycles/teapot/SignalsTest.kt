package org.tidalcycles.teapot

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SignalsTest {

    @Test
    fun `sine can be used to pattern a parameter`() {
        val pattern = s("0") fast 4 `#` doubleParameter("name")(sine)

        assertThat(pattern.query(TimeSpan(0, 1)))
            .hasSize(4)
            .allMatch { event ->
                val gain = event.value["name"] as Double
                gain != 0.0 && gain != 1.0
            }
    }

    @Test
    fun `cosine can be used to pattern a parameter`() {
        val pattern = s("0") fast 4 `#` doubleParameter("name")(cosine)

        assertThat(pattern.query(TimeSpan(0, 1)))
            .hasSize(4)
            .allMatch { event ->
                val gain = event.value["name"] as Double
                gain != 0.0 && gain != 1.0
            }
    }

    @Test
    fun `rand should return a double value from 0 to 1`() {
        val pattern = s("0") `#` doubleParameter("name")(rand)

        assertThat(pattern.query(TimeSpan(0, 1)))
            .first().matches { event ->
                val gain = event.value["name"] as Double
                gain != 0.0 && gain != 1.0
            }
    }
}