package org.tidalcycles.teapot.plugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange


class EvalCode: AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val editor = event.getData(EDITOR)!!
        val result = editor.currentParagraphTextAndRange();
        val code = result!!.first

        val repl = service<Repl>()

        repl.writeLine(code)
    }
}

fun Editor.currentParagraphTextAndRange(): Pair<String, TextRange>? {
    val currentLine = caretModel.logicalPosition.line
    val lines = document.text.split("\n")
    val lastLine = lines.size - 1
    if (lines[currentLine].isEmpty()) return null

    var fromLine = (0..currentLine).reversed().find { lines[it].isEmpty() } ?: 0
    val toLine = (currentLine..lastLine).find { lines[it].isEmpty() } ?: lastLine

    if (lines[fromLine].isEmpty() && fromLine < lastLine) fromLine++

    val textRange = TextRange(document.getLineStartOffset(fromLine), document.getLineEndOffset(toLine))
    return if (textRange.isEmpty) null else Pair(document.getText(textRange), textRange)
}