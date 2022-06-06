package org.tidalcycles.teapot.plugin

import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.execution.ui.ConsoleViewContentType.LOG_ERROR_OUTPUT
import com.intellij.execution.ui.ConsoleViewContentType.LOG_INFO_OUTPUT
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.RegisterToolWindowTask
import com.intellij.openapi.wm.ToolWindowManager

interface Console {
    fun logInfo(message: String)
    fun logError(message: String)
    fun logException(throwable: Throwable)
}

@Service
class IdeConsole : Console, Disposable {
    private val tidalToolWindowId = "Teapot"
    private var console: ConsoleView? = null
    private var project: Project? = null

    fun start(project: Project?) {
        ProjectManager.getInstance().openProjects.forEach { registerTidalToolWindowIn(it) }
        if (project != null) {
            ToolWindowManager.getInstance(project).getToolWindow(tidalToolWindowId)?.show()
        }
    }

    private fun registerTidalToolWindowIn(project: Project) {
        val toolWindowManager = ToolWindowManager.getInstance(project)
        if (toolWindowManager.getToolWindow(tidalToolWindowId) == null) {
            val console = TextConsoleBuilderFactory.getInstance().createBuilder(project).console
            toolWindowManager.registerToolWindow(RegisterToolWindowTask(tidalToolWindowId, component = console.component, canCloseContent = false))
            this.console = console
        }
    }

    fun stop() {
        ToolWindowManager.getInstance(project!!).unregisterToolWindow(tidalToolWindowId)
    }

    override fun dispose() = stop()

    override fun logInfo(message: String) {
        val cleaned = message.replace("Prelude>", "").trim()
        printToConsoles("$cleaned\n", LOG_INFO_OUTPUT)
    }

    override fun logError(message: String) {
        val cleaned = message.replace("Prelude>", "").trim()
        printToConsoles("$cleaned\n", LOG_ERROR_OUTPUT)
    }

    override fun logException(throwable: Throwable) {
        printToConsoles("${throwable.stackTraceToString()}\n", LOG_ERROR_OUTPUT)
    }

    private fun printToConsoles(s: String, contentType: ConsoleViewContentType) {
        console?.print(s, contentType)
    }
}