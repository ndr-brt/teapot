package org.tidalcycles.teapot.plugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service

class StartAction: AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project!!

        val repl = service<Repl>()

        repl.start(project)

        repl.writeLine("import org.tidalcycles.teapot.*")
        repl.writeLine("start()")
    }

}