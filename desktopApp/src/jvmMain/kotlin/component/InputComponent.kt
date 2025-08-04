package component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
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
                    val completedText = when {
                        StateControl.isCdCommand && StateControl.matchedDir.size == 1 ->
                            "cd ${StateControl.matchedDir[0].name}"

                        StateControl.isSpyCommand && StateControl.matchedFile.size == 1 ->
                            "spy ${StateControl.matchedFile[0].name}"

                        StateControl.isFileCommand && StateControl.matchedFile.size == 1 &&
                                StateControl.matchedFile[0].extension.lowercase() != "pdf" ->
                            "file ${StateControl.matchedFile[0].name}"

                        else -> null
                    }

                    if (completedText != null) {
                        // Posiciona o cursor no final do texto autocompletado
                        StateControl.inputText = TextFieldValue(
                            text = completedText,
                            selection = TextRange(completedText.length)
                        )
                        true
                    } else false
                }

                else if (event.key == Key.Enter && event.type == KeyEventType.KeyUp) {
                    val cmd = StateControl.inputText.text.trim()
                    when (cmd) {
                        "new tab" -> {
                            StateControl.sessions.add(SessionDto())
                            StateControl.selectedTabIndex = StateControl.sessions.lastIndex
                        }

                        "close tab" -> {
                            if (StateControl.sessions.size > 1) {
                                StateControl.sessions.removeAt(StateControl.selectedTabIndex)
                                StateControl.selectedTabIndex =
                                    StateControl.selectedTabIndex.coerceAtMost(StateControl.sessions.lastIndex)
                            }
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

                        "spy n" -> {
                            if (StateControl.session.showSpy) {
                                StateControl.session.spyIndex += 100
                            }
                        }

                        "spy b" -> {
                            if (StateControl.session.showSpy) {
                                StateControl.session.spyIndex = (StateControl.session.spyIndex - 100).coerceAtLeast(0)
                            }
                        }

                        "spy exit" -> {
                            StateControl.session.showSpy = false
                            StateControl.session.spyLines = emptyList()
                            StateControl.session.spyIndex = 0
                            StateControl.session.spyFileName = ""
                            StateControl.session.mode.value = ""
                        }

                        "file save" -> {
                            StateControl.session.fileEditorPath?.writeText(StateControl.session.fileEditorContent)
                            StateControl.session.showFileEditor = false
                            StateControl.session.fileEditorContent = ""
                            StateControl.session.fileEditorPath = null
                            StateControl.session.output = listDirectory(StateControl.session.currentDir)
                            StateControl.session.mode.value = ""
                        }

                        "file cancel" -> {
                            StateControl.session.showFileEditor = false
                            StateControl.session.fileEditorContent = ""
                            StateControl.session.fileEditorPath = null
                            StateControl.session.output = listDirectory(StateControl.session.currentDir)
                            StateControl.session.mode.value = ""
                        }

                        else -> {
                            if (cmd.startsWith("cd ")) {
                                val target = File(StateControl.session.currentDir, StateControl.prefix)
                                if (target.exists() && target.isDirectory) {
                                    StateControl.session.currentDir = target
                                    StateControl.session.output = listDirectory(target)
                                }
                                StateControl.session.mode.value = ""
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
                                    StateControl.session.mode.value = "[spy mode]"
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
                                    StateControl.session.mode.value = "[file mode]"
                                }
                            } else {
                                StateControl.session.mode.value = ""
                            }
                        }
                    }

                    // Limpa o campo de entrada
                    StateControl.inputText = TextFieldValue("")
                    true
                } else false
            },
        singleLine = true
    )
}
