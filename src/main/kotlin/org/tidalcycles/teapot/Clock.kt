package org.tidalcycles.teapot

import java.math.BigDecimal
import java.util.concurrent.Executors

object Clock {

    private val bpm: Double = 120.0
    private val bpc = 4

    private var streams = mutableListOf<org.tidalcycles.teapot.SuperDirtStream>()
    private val executor = Executors.newFixedThreadPool(1)

    fun subscribe(stream: org.tidalcycles.teapot.SuperDirtStream): org.tidalcycles.teapot.Clock {
        org.tidalcycles.teapot.Clock.streams.add(stream)
        return this
    }

    fun start() {
        org.tidalcycles.teapot.Clock.executor.submit(this::notifyThread)
    }

    fun notifyThread() {
        var ticks = 0
        val frame = 300.0 // in millis
        val clockState = org.tidalcycles.teapot.ClockState(org.tidalcycles.teapot.Clock.bpm, frame)

        while (true) {
            val logicalTime = clockState.logicalTimeAt(ticks)

            val span = clockState.timeSpanFor(logicalTime)

            org.tidalcycles.teapot.Clock.streams.forEach { it.tick(span, clockState) }

            val wait = logicalTime - clockState.now()

            if (wait > 0) {
                Thread.sleep(wait.toLong())
            }

            ticks++
        }
    }

}

class ClockState(private val bpm: Double, private val frame: Double) {
    private val start: Long = now()
    private val bpc = 4

    fun timeAtCycle(cycle: Double) = (BigDecimal(cycle) / BigDecimal(cps()) * BigDecimal(1000)) + BigDecimal(start)
    fun cps(): Double = bpm / bpc / 60
    fun logicalTimeAt(ticks: Int): Double = start + (ticks * frame)
    fun now() = System.currentTimeMillis()
    fun timeSpanFor(time: org.tidalcycles.teapot.Time): org.tidalcycles.teapot.TimeSpan {
        val from = cycleAtTime(time)
        val to = cycleAtTime(time + frame)
        return org.tidalcycles.teapot.TimeSpan(from, to)
    }

    private fun cycleAtTime(time: Double) = (BigDecimal(time) - BigDecimal(start)) * BigDecimal(cps()) / BigDecimal(1000)

}