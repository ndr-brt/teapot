package org.tidalcycles.teapot

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PatternTest {

    @Test
    fun `fast should speed up patterns`() {
        val pattern = s("bd") fast 2

        assertThat(pattern.query(TimeSpan(0, 1)))
            .containsExactly(
                Event(TimeSpan(0, 0.5), TimeSpan(0, 0.5), mapOf("s" to "bd")),
                Event(TimeSpan(0.5, 1), TimeSpan(0.5, 1), mapOf("s" to "bd"))
            )
    }

    @Test
    fun `fast can be patternable`() {
        val pattern = fastcat(s("bd"), s("sd")) fast fastcat(pure(2), pure(1))

        assertThat(pattern.query(TimeSpan(0, 1)))
            .hasSize(3)
            .containsExactly(
                Event(TimeSpan(0, 0.25), TimeSpan(0, 0.25), mapOf("s" to "bd")),
                Event(TimeSpan(0.25, 0.5), TimeSpan(0.25, 0.5), mapOf("s" to "sd")),
                Event(TimeSpan(0.5, 1), TimeSpan(0.5, 1), mapOf("s" to "sd"))
            )
    }

    @Test
    fun `fast can be patternable with mini notation`() {
        val pattern = s("bd sd") fast "2 1"

        assertThat(pattern.query(TimeSpan(0, 1)))
            .hasSize(3)
            .containsExactly(
                Event(TimeSpan(0, 0.25), TimeSpan(0, 0.25), mapOf("s" to "bd")),
                Event(TimeSpan(0.25, 0.5), TimeSpan(0.25, 0.5), mapOf("s" to "sd")),
                Event(TimeSpan(0.5, 1), TimeSpan(0.5, 1), mapOf("s" to "sd"))
            )
    }

    @Test
    fun `slow should slow down patterns`() {
        val pattern = s("bd") slow 2

        assertThat(pattern.query(TimeSpan(0, 2)))
            .containsExactly(
                Event(TimeSpan(0, 2), TimeSpan(0, 2), mapOf("s" to "bd"))
            )
    }

    @Test
    fun `slowcat should concatenate patterns, one per cycle`() {
        val pattern = slowcat(s("bd"), s("sd"))

        assertThat(pattern.query(TimeSpan(0, 2)))
            .containsExactly(
                Event(TimeSpan(0, 1), TimeSpan(0, 1), mapOf("s" to "bd")),
                Event(TimeSpan(1, 2), TimeSpan(1, 2), mapOf("s" to "sd"))
            )
    }

    @Test
    fun `fastcat should concatenate patterns in the same cycle`() {
        val pattern = fastcat(s("bd"), s("sd"), s("hh"), s("cp"))

        assertThat(pattern.query(TimeSpan(0, 1)))
            .containsExactly(
                Event(TimeSpan(0, 0.25), TimeSpan(0, 0.25), mapOf("s" to "bd")),
                Event(TimeSpan(0.25, 0.5), TimeSpan(0.25, 0.5), mapOf("s" to "sd")),
                Event(TimeSpan(0.5, 0.75), TimeSpan(0.5, 0.75), mapOf("s" to "hh")),
                Event(TimeSpan(0.75, 1), TimeSpan(0.75, 1), mapOf("s" to "cp"))
            )
    }

    @Test
    fun `rotl should rotate pattern to left`() {
        val pattern = fastcat(s("bd"), s("sd"), s("hh"), s("cp")) rotL 0.25

        assertThat(pattern.query(TimeSpan(0, 1)))
            .containsExactly(
                Event(TimeSpan(0, 0.25), TimeSpan(0, 0.25), mapOf("s" to "sd")),
                Event(TimeSpan(0.25, 0.5), TimeSpan(0.25, 0.5), mapOf("s" to "hh")),
                Event(TimeSpan(0.5, 0.75), TimeSpan(0.5, 0.75), mapOf("s" to "cp")),
                Event(TimeSpan(0.75, 1), TimeSpan(0.75, 1), mapOf("s" to "bd"))
            )
    }

    @Test
    fun `rotl should be patternable`() {
        val pattern = fastcat(s("bd"), s("sd"), s("hh"), s("cp")) rotL fastcat(pure(0.25), pure(0))

        assertThat(pattern.query(TimeSpan(0, 1)))
            .containsExactly(
                Event(TimeSpan(0, 0.25), TimeSpan(0, 0.25), mapOf("s" to "sd")),
                Event(TimeSpan(0.25, 0.5), TimeSpan(0.25, 0.5), mapOf("s" to "hh")),
                Event(TimeSpan(0.5, 0.75), TimeSpan(0.5, 0.75), mapOf("s" to "hh")),
                Event(TimeSpan(0.75, 1), TimeSpan(0.75, 1), mapOf("s" to "cp"))
            )
    }

    @Test
    fun `rotr should rotate pattern to right`() {
        val pattern = fastcat(s("bd"), s("sd"), s("hh"), s("cp")) rotR 0.25

        assertThat(pattern.query(TimeSpan(0, 1)))
            .containsExactly(
                Event(TimeSpan(0, 0.25), TimeSpan(0, 0.25), mapOf("s" to "cp")),
                Event(TimeSpan(0.25, 0.5), TimeSpan(0.25, 0.5), mapOf("s" to "bd")),
                Event(TimeSpan(0.5, 0.75), TimeSpan(0.5, 0.75), mapOf("s" to "sd")),
                Event(TimeSpan(0.75, 1), TimeSpan(0.75, 1), mapOf("s" to "hh"))
            )
    }

    @Test
    fun `rotr should be patternable`() {
        val pattern = fastcat(s("bd"), s("sd"), s("hh"), s("cp")) rotR fastcat(pure(0.25), pure(0))

        assertThat(pattern.query(TimeSpan(0, 1)))
            .containsExactly(
                Event(TimeSpan(0, 0.25), TimeSpan(0, 0.25), mapOf("s" to "cp")),
                Event(TimeSpan(0.25, 0.5), TimeSpan(0.25, 0.5), mapOf("s" to "bd")),
                Event(TimeSpan(0.5, 0.75), TimeSpan(0.5, 0.75), mapOf("s" to "hh")),
                Event(TimeSpan(0.75, 1), TimeSpan(0.75, 1), mapOf("s" to "cp"))
            )
    }

    @Test
    fun `stack should stack patterns`() {
        val pattern = stack(s("bd"), s("sd"))

        assertThat(pattern.query(TimeSpan(0, 1)))
            .hasSize(2)
            .containsExactly(
                Event(TimeSpan(0, 1), TimeSpan(0, 1), mapOf("s" to "bd")),
                Event(TimeSpan(0, 1), TimeSpan(0, 1), mapOf("s" to "sd"))
            )
    }

    @Test
    fun `superimpose should apply a function on a pattern duplicate`() {
        val pattern = s("bd") superimpose { it `#` gain(0.5) }

        assertThat(pattern.query(TimeSpan(0, 1)))
            .containsExactly(
                Event(TimeSpan(0, 1), TimeSpan(0, 1), mapOf("s" to "bd")),
                Event(TimeSpan(0, 1), TimeSpan(0, 1), mapOf("s" to "bd", "gain" to 0.5))
            )
    }

    @Test
    fun `# should take structure from left and values from right`() {
        val pattern = fastcat(s("bd"), s("sd")) `#` n(1.2)

        assertThat(pattern.query(TimeSpan(0, 1)))
            .containsExactly(
                Event(TimeSpan(0, 0.5), TimeSpan(0, 0.5), mapOf("s" to "bd", "n" to 1.2)),
                Event(TimeSpan(0.5, 1), TimeSpan(0.5, 1), mapOf("s" to "sd", "n" to 1.2))
            )
    }

    @Test
    fun `+| should sum values and take structure from the right`() {
        val pattern = fastcat(s("bd")) `+|` gain(fastcat(pure(0.5), pure(1.0))) `+|` gain(0.1)

        assertThat(pattern.query(TimeSpan(0, 1)))
            .hasSize(2)
            .containsExactly(
                Event(TimeSpan(0, 1), TimeSpan(0, 0.5), mapOf("s" to "bd", "gain" to 0.6)),
                Event(TimeSpan(0, 1), TimeSpan(0.5, 1), mapOf("s" to "bd", "gain" to 1.1))
            )
    }

    @Test
    fun `|+ should sum values and take structure from the left`() {
        val pattern = fastcat(n("1"), n("3")) `|+` gain(pure(0.5)) `|+` gain (pure(0.2)) `|+` n(4)

        assertThat(pattern.query(TimeSpan(0, 1)))
            .hasSize(2)
            .containsExactly(
                Event(TimeSpan(0, 0.5), TimeSpan(0, 0.5), mapOf("n" to 5.0, "gain" to 0.7)),
                Event(TimeSpan(0.5, 1), TimeSpan(0.5, 1), mapOf("n" to 7.0, "gain" to 0.7))
            )
    }

    @Test
    fun `-| should subtract values and take structure from the right`() {
        val pattern = fastcat(s("bd")) `|+` gain(1) `-|` gain(fastcat(pure(0.5), pure(1.0)))

        assertThat(pattern.query(TimeSpan(0, 1)))
            .hasSize(2)
            .containsExactly(
                Event(TimeSpan(0, 0.5), TimeSpan(0, 0.5), mapOf("s" to "bd", "gain" to 0.5)),
                Event(TimeSpan(0.5, 1), TimeSpan(0.5, 1), mapOf("s" to "bd", "gain" to 0.0))
            )
    }

    @Test
    fun `|- should subtract values and take structure from the left`() {
        val pattern = fastcat(s("bd"), s("sd")) `|-` gain(pure(0.5)) `|-` gain (pure(0.2))

        assertThat(pattern.query(TimeSpan(0, 1)))
            .hasSize(2)
            .containsExactly(
                Event(TimeSpan(0, 0.5), TimeSpan(0, 0.5), mapOf("s" to "bd", "gain" to -0.7)),
                Event(TimeSpan(0.5, 1), TimeSpan(0.5, 1), mapOf("s" to "sd", "gain" to -0.7))
            )
    }

}