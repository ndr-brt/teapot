package org.tidalcycles.teapot

import com.illposed.osc.*
import com.illposed.osc.messageselector.OSCPatternAddressMessageSelector
import com.illposed.osc.transport.OSCPortInBuilder
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS

internal class TeapotTest {

    @Test
    fun `should send messages to superdirt`() {
        val messages = mutableListOf<OSCMessageEvent>()

        OSCPortInBuilder()
            .addPacketListener(OSCPacketDispatcher())
            .addMessageListener(OSCPatternAddressMessageSelector("/dirt/play"), messages::add)
            .setPort(57123)
            .build().startListening()

        p[0] = s("bd")

        start(port = 57123)

        await().atLeast(500, MILLISECONDS).atMost(3, SECONDS).untilAsserted {
            assertThat(messages).hasSize(2)
        }
    }

}
