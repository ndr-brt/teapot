package org.tidalcycles.teapot.plugin

import com.intellij.concurrency.JobScheduler
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import java.io.Reader
import java.nio.file.Path
import java.util.concurrent.TimeUnit

@Service
class Repl: Disposable {

    lateinit var process: Process

    fun start(project: Project) {
        val console = IdeConsole()
        console.start(project)
        val kotlincJvm = Path.of(PathManager.getPreInstalledPluginsPath()).resolve("Kotlin").resolve("kotlinc").resolve("bin").resolve("kotlinc-jvm")
        val classpath = Path.of(PathManager.getPluginsPath()).resolve("teapot").resolve("lib")

        val completeClasspath = classpath.toFile().listFiles().filter { it.extension == "jar" }.joinToString(":")
        process = ProcessBuilder(kotlincJvm.toString(), "-cp", completeClasspath)
            .start()

        val outReader = process.inputStream.reader()
        val errReader = process.errorStream.reader()
        JobScheduler.getScheduler().scheduleWithFixedDelay(
            {
                outReader.readString().takeIf { it.isNotBlank() }?.let { console.logInfo(it) }
                errReader.readString().takeIf { it.isNotBlank() }?.let { console.logError(it) }
            },
            0, 100, TimeUnit.MILLISECONDS
        )
    }

    fun writeLine(line: String) {
        val writer = process.outputStream?.writer() ?: return
        writer.write("$line\n")
        writer.flush()
    }

    override fun dispose() {
        process.destroy()
    }
}


private fun Reader.readString(): String {
    var lines = ""
    while (ready()) {
        lines += read().toChar()
    }
    return lines

}