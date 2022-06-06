package org.tidalcycles.teapot.plugin

import com.intellij.concurrency.JobScheduler
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.components.Service
import java.io.Reader
import java.nio.file.Path
import java.util.concurrent.TimeUnit

@Service
class Repl: Disposable {

    var process: Process? = null

    fun start(console: IdeConsole) {
        if (process == null || process?.isAlive == false) {
            val kotlincJvm = Path.of(PathManager.getPreInstalledPluginsPath()).resolve("Kotlin").resolve("kotlinc").resolve("bin").resolve("kotlinc-jvm")
            val classpath = Path.of(PathManager.getPluginsPath()).resolve("teapot").resolve("lib")

            val completeClasspath = classpath.toFile().listFiles()?.filter { it.extension == "jar" }?.joinToString(":")
            process = ProcessBuilder(kotlincJvm.toString(), "-cp", completeClasspath)
                .start().apply {
                    JobScheduler.getScheduler()
                        .scheduleWithFixedDelay({
                            inputStream.reader().pipeTo { console.logInfo(it) }
                            errorStream.reader().pipeTo { console.logError(it) }
                        }, 0, 100, TimeUnit.MILLISECONDS)
                }
        }
    }

    fun writeLine(line: String) {
        process?.outputStream?.writer()?.let {
            it.write("$line\n")
            it.flush()
        }
    }

    fun isRunning(): Boolean = process?.isAlive ?: false

    override fun dispose() {
        process?.destroy()
    }
}

private fun Reader.pipeTo(consumer: (String) -> Unit) {
    this.readString().takeIf { it.isNotBlank() }?.let(consumer)
}

private fun Reader.readString(): String {
    var chars = ""
    while (ready()) {
        chars += read().toChar()
    }
    return chars

}