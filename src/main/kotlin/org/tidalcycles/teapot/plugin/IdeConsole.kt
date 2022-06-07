package org.tidalcycles.teapot.plugin

import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.execution.ui.ConsoleViewContentType.LOG_ERROR_OUTPUT
import com.intellij.execution.ui.ConsoleViewContentType.LOG_INFO_OUTPUT
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.RegisterToolWindowTask
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager

interface Console {
    fun logInfo(message: String)
    fun logError(message: String)
    fun logException(throwable: Throwable)
}

@Service
class IdeConsole : Console, Disposable {
    private val windowId = "Teapot"
    private var console: ConsoleView? = null
    private var project: Project? = null

    fun start(project: Project) {
        this.project = project
        val toolWindowManager = ToolWindowManager.getInstance(project)
        val toolWindow = toolWindowManager.getToolWindow(windowId)
        if (toolWindow == null) {
            val console = TextConsoleBuilderFactory.getInstance().createBuilder(project).console
            this.console = console
            val registerTask = RegisterToolWindowTask(windowId, component = console.component, canCloseContent = false)
            toolWindowManager.registerToolWindow(registerTask)
        }

        toolWindowManager.getToolWindow(windowId)!!.show()
    }

    override fun dispose() {
        project?.let { ToolWindowManager.getInstance(it) }?.unregisterToolWindow(windowId)
    }

    override fun logInfo(message: String) = printToConsoles(message, LOG_INFO_OUTPUT)
    override fun logError(message: String) = printToConsoles(message, LOG_ERROR_OUTPUT)
    override fun logException(throwable: Throwable) = printToConsoles(throwable.stackTraceToString(), LOG_ERROR_OUTPUT)

    private fun printToConsoles(s: String, contentType: ConsoleViewContentType) {
        console?.print("$s\n", contentType)
    }
}