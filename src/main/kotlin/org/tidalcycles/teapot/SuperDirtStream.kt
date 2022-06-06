package org.tidalcycles.teapot

import com.illposed.osc.OSCMessage
import com.illposed.osc.transport.OSCPortOut
import java.math.BigDecimal
import java.net.InetSocketAddress
import java.time.Instant
import java.util.*

data class SuperDirtStream(
    val hostname: String,
    val port: Int,
    val latency: Double,
    val patterns: MutableMap<Any, Pattern<out Any>>
) {

    private val osc = OSCPortOut(InetSocketAddress(hostname, port))
    private val timer = Timer()

    fun tick(cycle: TimeSpan, state: ClockState) {
        patterns.map { entry ->
            val pattern = entry.value
            val events = pattern.query(cycle).filter { it.isOnset() }
//            println("Evaluate cycle $cycle. Events: ${events.size}")
            events.forEach { event ->
                val cycleOn = event.whole?.begin ?: 0.0 // these should always be discrete, so with a whole
                val cycleOff = event.whole?.end ?: 0.0

                val linkOn = state.timeAtCycle(cycleOn)
                val linkOff = state.timeAtCycle(cycleOff)
                val delta = (linkOff - linkOn) / (BigDecimal(1000))

                val millis = linkOn + (BigDecimal(latency).multiply(BigDecimal(1000)))

                val eventArguments = when (event.value) {
                    is Map<*, *> -> event.value.flatMap { listOf(it.key, it.value) }
                    else -> listOf("value", event.value)
                }

                val arguments = listOf<Any>(
                    "cps", state.cps(),
                    "cycle", event.whole?.begin ?: 0.0,
                    "delta", delta.toDouble(),
                ) + eventArguments

//                println("Message: $arguments")

                val message = OSCMessage("/dirt/play", arguments)
                val sendAt = Instant.ofEpochMilli(millis.toLong())

                scheduleAt(message, sendAt)
            }
        }
    }

    private fun scheduleAt(message: OSCMessage, sendAt: Instant) {
        try {
            timer.schedule(sendMessageTask(message), Date.from(sendAt))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendMessageTask(message: OSCMessage): TimerTask {
        return object : TimerTask() {
            override fun run() {
                try {
                    osc.send(message)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}
