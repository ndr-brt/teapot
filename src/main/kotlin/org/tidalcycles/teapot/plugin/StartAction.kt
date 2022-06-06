package org.tidalcycles.teapot.plugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service

class StartAction: AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project!!

        val console = service<IdeConsole>()
        val repl = service<Repl>()

        if (repl.isRunning()) {
            console.logInfo("Teapot is already running")
        } else {
            console.logInfo("Starting teapot...")
            console.start(project)
            repl.start(console)

            repl.writeLine("import org.tidalcycles.teapot.*")
            repl.writeLine("start()")
        }
    }

}