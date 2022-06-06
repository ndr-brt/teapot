package org.tidalcycles.teapot

import java.util.logging.ConsoleHandler
import java.util.logging.Handler
import java.util.logging.Logger

var stream: SuperDirtStream? = null
val p = mutableMapOf<Any, Pattern<out Any>>()
val log: Logger = Logger.getGlobal()

fun start(hostname: String = "127.0.0.1", port: Int = 57120, loggerHandler: Handler = ConsoleHandler()) {
    if (stream == null) {
        log.addHandler(loggerHandler)
//        log.info("Start Teapot")
        println("Start Teapot")
//        log.info("Connecting to Superdirt at $hostname:$port")
        println("Connecting to Superdirt at $hostname:$port")
        stream = SuperDirtStream(hostname, port, 0.3, p)
        org.tidalcycles.teapot.Clock.subscribe(stream!!).start()
    }
}

fun errln(message: String) {
    System.err.println(message)
}