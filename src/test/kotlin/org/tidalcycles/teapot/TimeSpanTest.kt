package org.tidalcycles.teapot

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.impl.DummyProject
import com.intellij.openapi.module.impl.ModuleImpl
import com.intellij.openapi.project.DefaultProjectFactoryImpl
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test

internal class TimeSpanTest {
    @Test
    fun `spans over cycles`() {
        val timeSpan = TimeSpan(0.3, 1.5)

        assertThat(timeSpan.spanCycles()).containsExactly(TimeSpan(0.3, 1.0), TimeSpan(1.0, 1.5))
    }

    @Test
    fun `spans over cycles excludes empty timespan`() {
        val timeSpan = TimeSpan(0.0, 1.0)

        assertThat(timeSpan.spanCycles()).containsExactly(TimeSpan(0.0, 1.0))
    }

    @Test
    fun `spans in the same cycle avoid stackoverflow`() {
        val timeSpan = TimeSpan(0.25, 0.5)

        assertThat(timeSpan.spanCycles()).containsExactly(TimeSpan(0.25, 0.5))
    }

    @Test
    fun `intersect should return null if there is no intersection`() {
        assertThat(TimeSpan(0, 0.5) intersect TimeSpan(0.5, 1)).isEqualTo(null)
    }

    @Test
    fun `intersect should return intersection between two timespans`() {
        assertThat(TimeSpan(0, 0.7) intersect TimeSpan(0.3, 1)).isEqualTo(TimeSpan(0.3, 0.7))
    }

    @Test
    internal fun `midpoint should return the middle point of a span`() {
        assertThat(TimeSpan(0.3, 0.7).midpoint()).isEqualTo(0.5)
    }
}