package org.tidalcycles.teapot

var stream: SuperDirtStream? = null
val p = mutableMapOf<Any, Pattern<out Any>>()

fun start(hostname: String = "127.0.0.1", port: Int = 57120) {
    if (stream == null) {
        println("Start Teapot")
        println("Connecting to Superdirt at $hostname:$port")
        stream = SuperDirtStream(hostname, port, 0.3, p)
        Clock.subscribe(stream!!).start()
    }
}

fun errorln(message: String) {
    System.err.println(message)
}