package org.tidalcycles.teapot

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MiniNotationParseTest {

    @Test
    fun `should parse single event as pure`() {
        assertIn(TimeSpan(0, 1))
            .equal(
                "bd".patternString(),
                pure("bd")
            )
    }

    @Test
    fun `should parse two events separated by space as fastcat`() {
        assertIn(TimeSpan(0, 1))
            .equal(
                "bd sd".patternString(),
                fastcat(pure("bd"), pure("sd"))
            )
    }

    @Test
    fun `should parse ~ as silence`() {
        assertIn(TimeSpan(0, 1))
            .equal(
                "~ bd".patternString(),
                fastcat(silence(), pure("bd"))
            )
    }

    @Test
    fun `should parse events in square brackets as fastcat`() {
        assertIn(TimeSpan(0, 1))
            .equal(
                "[bd sd]".patternString(),
                fastcat(pure("bd"), pure("sd"))
            )
    }

    @Test
    fun `should parse events in nested square brackets as fastcat`() {
        assertIn(TimeSpan(0, 1))
            .equal(
                "[bd sd] hh".patternString(),
                fastcat(fastcat(pure("bd"), pure("sd")), pure("hh"))
            )
    }

    @Test
    fun `should parse wildcard as fast`() {
        assertIn(TimeSpan(0, 1))
            .equal(
                "bd*3".patternString(),
                pure("bd") fast 3
            )
    }

    @Test
    fun `should parse slash as slow`() {
        assertIn(TimeSpan(0, 1))
            .equal(
                "bd/3".patternString(),
                pure("bd") slow 3
            )
    }

    @Test
    internal fun `should parse fast on a fastcat block`() {
        assertIn(TimeSpan(0, 1))
            .equal(
                "[bd sd]*2".patternString(),
                fastcat(pure("bd"), pure("sd")) fast 2
            )
    }

    @Test
    fun `should parse slow on a fastcat block`() {
        assertIn(TimeSpan(0, 1))
            .equal(
                "[bd sd]/2".patternString(),
                fastcat(pure("bd"), pure("sd")) slow 2
            )
    }

    @Test
    fun `should parse floating point numbers as doubles`() {
        assertIn(TimeSpan(0, 1))
            .equal(
                "0.1".patternDouble(),
                pure(0.1)
            )
    }

    @Test
    fun `should parse stack`() {
        assertIn(TimeSpan(0, 1))
            .equal(
                "[1, 2, 3]".patternInt(),
                stack(pure(1), pure(2), pure(3))
            )
    }

    @Test
    fun `should parse stack with no square bracket fastcat`() {
        assertIn(TimeSpan(0, 1))
            .equal(
                "[1, 2 3]".patternInt(),
                stack(pure(1), fastcat(pure(2), pure(3)))
            )
    }

    @Test
    fun `should parse events in angled brackets as fastcat`() {
        assertIn(TimeSpan(0, 1))
            .equal(
                "<1 2>".patternInt(),
                slowcat(pure(1), pure(2))
            )
    }

    @Test
    fun `should parse numbers with sign`() {
        assertIn(TimeSpan(0, 1))
            .equal(
                "-1 -3.6 +5.4 +3".patternDouble(),
                fastcat(pure(-1.0), pure(-3.6), pure(5.4), pure(3.0))
            )
    }

    private fun assertIn(timeSpan: TimeSpan): PatternEquals {
        return PatternEquals(timeSpan)
    }

    class PatternEquals(private val timeSpan: TimeSpan) {
        fun equal(actual: Pattern<*>, expected: Pattern<*>) {
            assertThat(
                actual.query(timeSpan)
            ).isEqualTo(
                expected.query(timeSpan)
            )
        }

    }
}