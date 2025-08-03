package component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import control.StateControl
import dto.SessionDto
import util.extractTextLines
import util.listDirectory
import java.io.File

@Composable
fun InputComponent() {
    BasicTextField(
        value = StateControl.inputText,
        onValueChange = { StateControl.inputText = it },
        textStyle = TextStyle(
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.DarkGray)
            .padding(8.dp)
            .onPreviewKeyEvent { event ->
                if (event.key == Key.Tab && event.type == KeyEventType.KeyDown) {
                    when {
                        StateControl.isCdCommand && StateControl.matchedDir.size == 1 -> {
                            StateControl.inputText = "cd ${StateControl.matchedDir[0].name}"
                            true
                        }
                        StateControl.isSpyCommand && StateControl.matchedFile.size == 1 -> {
                            StateControl.inputText = "spy ${StateControl.matchedFile[0].name}"
                            true
                        }
                        StateControl.isFileCommand && StateControl.matchedFile.size == 1 && StateControl.matchedFile[0].extension.lowercase() != "pdf" -> {
                            StateControl.inputText = "file ${StateControl.matchedFile[0].name}"
                            true
                        }
                        else -> false
                    }
                } else if (event.key == Key.Enter && event.type == KeyEventType.KeyUp) {
                    when (val cmd = StateControl.inputText.trim()) {
                        "new tab" -> {
                            StateControl.sessions.add(SessionDto())
                            StateControl.selectedTabIndex = StateControl.sessions.lastIndex
                        }
                        "ls" -> StateControl.session.output = listDirectory(StateControl.session.currentDir)
                        "cd .." -> {
                            StateControl.session.currentDir.parentFile?.takeIf { it.exists() }?.let {
                                StateControl.session.currentDir = it
                                StateControl.session.output = listDirectory(it)
                            }
                        }
                        "home" -> {
                            StateControl.session.currentDir = File(System.getProperty("user.home"))
                            StateControl.session.output = listDirectory(StateControl.session.currentDir)
                        }
                        "spy continue" -> if (StateControl.session.showSpy) StateControl.session.spyIndex += 100
                        "spy exit" -> {
                            StateControl.session.showSpy = false
                            StateControl.session.spyLines = emptyList()
                            StateControl.session.spyIndex = 0
                            StateControl.session.spyFileName = ""
                        }
                        "file save" -> {
                            StateControl.session.fileEditorPath?.writeText(StateControl.session.fileEditorContent)
                            StateControl.session.showFileEditor = false
                            StateControl.session.fileEditorContent = ""
                            StateControl.session.fileEditorPath = null
                            StateControl.session.output = listDirectory(StateControl.session.currentDir)
                        }
                        "file cancel" -> {
                            StateControl.session.showFileEditor = false
                            StateControl.session.fileEditorContent = ""
                            StateControl.session.fileEditorPath = null
                            StateControl.session.output = listDirectory(StateControl.session.currentDir)
                        }
                        else -> {
                            if (cmd.startsWith("cd ")) {
                                val target = File(StateControl.session.currentDir, StateControl.prefix)
                                if (target.exists() && target.isDirectory) {
                                    StateControl.session.currentDir = target
                                    StateControl.session.output = listDirectory(target)
                                }
                            } else if (cmd.startsWith("spy ")) {
                                val supported = listOf("pdf", "txt", "json", "xml", "java", "log", "md", "py", "yml", "yaml", "sh")
                                val target = File(StateControl.session.currentDir, StateControl.prefix)
                                if (target.exists() && supported.contains(target.extension.lowercase())) {
                                    StateControl.session.spyLines = extractTextLines(target)
                                    StateControl.session.spyIndex = 0
                                    StateControl.session.splitRatio.value = 0.3f
                                    StateControl.session.showSpy = true
                                    StateControl.session.showFileEditor = false
                                    StateControl.session.spyFileName = target.name
                                }
                            } else if (cmd.startsWith("file ")) {
                                val parts = cmd.split(" ")
                                if (parts.size == 2) {
                                    val file = File(StateControl.session.currentDir, parts[1])
                                    if (file.extension.lowercase() == "pdf") return@onPreviewKeyEvent true
                                    val exists = file.exists()
                                    if (!exists) file.createNewFile()
                                    StateControl.session.fileEditorPath = file
                                    StateControl.session.fileEditorContent = if (exists) file.readText() else ""
                                    StateControl.session.splitRatio.value = 0.3f
                                    StateControl.session.showFileEditor = true
                                    StateControl.session.showSpy = false
                                }
                            }
                        }
                    }
                    StateControl.inputText = ""
                    true
                } else false
            },
        singleLine = true
    )
}