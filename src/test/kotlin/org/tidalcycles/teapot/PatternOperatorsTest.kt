package org.tidalcycles.teapot

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PatternOperatorsTest {

    @Test
    fun `double pattern can be summed`() {
        val pattern = sine + 10

        assertThat(pattern.query(TimeSpan(0, 1)))
            .allMatch { event -> event.value > 10 && event.value < 11 }
    }

    @Test
    fun `double pattern can be summed the other way`() {
        val pattern = 10 + sine

        assertThat(pattern.query(TimeSpan(0, 1)))
            .allMatch { event -> event.value > 10 && event.value < 11 }
    }

    @Test
    fun `double pattern can be subtracted`() {
        val pattern = sine - 1

        assertThat(pattern.query(TimeSpan(0, 1)))
            .allMatch { event -> event.value > -1 && event.value < 0 }
    }

    @Test
    fun `double pattern can be subtracted the other way around`() {
        val pattern = 0 - sine

        assertThat(pattern.query(TimeSpan(0, 1)))
            .allMatch { event -> event.value > -1 && event.value < 0 }
    }

    @Test
    fun `double pattern can be multiplied`() {
        val pattern = sine * 10

        assertThat(pattern.query(TimeSpan(0, 1)))
            .allMatch { event -> event.value > 4.0 && event.value < 6.0 }
    }

    @Test
    fun `double pattern can be multiplied the other way around`() {
        val pattern = 10 * sine

        assertThat(pattern.query(TimeSpan(0, 1)))
            .allMatch { event -> event.value > 4.0 && event.value < 6.0 }
    }

    @Test
    fun `double pattern can be divided`() {
        val pattern = sine / 10

        assertThat(pattern.query(TimeSpan(0, 1)))
            .allMatch { event -> event.value > 0 && event.value < 0.1 }
    }

    @Test
    fun `double pattern can be divided the other way around`() {
        val pattern = 1 / sine

        assertThat(pattern.query(TimeSpan(0, 1)))
            .allMatch { event -> event.value > 1 && event.value < 10 }
    }
}