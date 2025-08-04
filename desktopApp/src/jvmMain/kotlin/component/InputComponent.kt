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
import kotlin.system.exitProcess

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
                // AUTOCOMPLETE
                if (event.key == Key.Tab && event.type == KeyEventType.KeyDown) {
                    val input = StateControl.inputText.text.trim()
                    val parts = input.split(" ")
                    if (parts.size == 2) {
                        val command = parts[0]
                        val matches = StateControl.matchedAll
                        if (matches.size == 1) {
                            val match = matches[0]
                            val completedText = "$command ${match.name}"
                            StateControl.inputText = TextFieldValue(
                                text = completedText,
                                selection = TextRange(completedText.length)
                            )
                            return@onPreviewKeyEvent true
                        }
                    }
                    return@onPreviewKeyEvent false
                }

                // ENTER
                else if (event.key == Key.Enter && event.type == KeyEventType.KeyUp) {
                    val cmd = StateControl.inputText.text.trim()
                    when (cmd) {
                        "new tab" -> {
                            StateControl.sessions.add(SessionDto())
                            StateControl.selectedTabIndex = StateControl.sessions.lastIndex
                            resetVisualState()
                        }

                        "close tab" -> {
                            if (StateControl.sessions.size > 1) {
                                StateControl.sessions.removeAt(StateControl.selectedTabIndex)
                                StateControl.selectedTabIndex =
                                    StateControl.selectedTabIndex.coerceAtMost(StateControl.sessions.lastIndex)
                                resetVisualState()
                            }
                        }

                        "ls" -> {
                            StateControl.session.output = listDirectory(StateControl.session.currentDir.value)
                            resetVisualState()
                        }

                        "cd .." -> {
                            StateControl.session.currentDir.value.parentFile?.takeIf { it.exists() }?.let { parent ->
                                StateControl.session.currentDir.value = parent
                                StateControl.session.output = listDirectory(parent)
                            }
                            resetVisualState()
                        }

                        "home" -> {
                            val home = File(System.getProperty("user.home"))
                            StateControl.session.currentDir.value = home
                            StateControl.session.output = listDirectory(home)
                            resetVisualState()
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

                        "spy close" -> {
                            StateControl.session.showSpy = false
                            StateControl.session.spyLines = emptyList()
                            StateControl.session.spyDirContent = emptyList()
                            StateControl.session.spyIndex = 0
                            StateControl.session.spyFileName = ""
                            StateControl.session.mode.value = ""
                        }

                        "file save" -> {
                            StateControl.session.fileEditorPath?.writeText(StateControl.session.fileEditorContent)
                            StateControl.session.showFileEditor = false
                            StateControl.session.fileEditorContent = ""
                            StateControl.session.fileEditorPath = null
                            StateControl.session.output = listDirectory(StateControl.session.currentDir.value)
                            StateControl.session.mode.value = ""
                        }

                        "file close" -> {
                            StateControl.session.showFileEditor = false
                            StateControl.session.fileEditorContent = ""
                            StateControl.session.fileEditorPath = null
                            StateControl.session.output = listDirectory(StateControl.session.currentDir.value)
                            StateControl.session.mode.value = ""
                        }

                        "exit" -> {
                            exitProcess(0)
                        }

                        else -> {
                            when {
                                cmd.startsWith("cd ") -> {
                                    val target = File(StateControl.session.currentDir.value, StateControl.prefix)
                                    if (target.exists() && target.isDirectory) {
                                        StateControl.session.currentDir.value = target
                                        StateControl.session.output = listDirectory(target)
                                    }
                                    resetVisualState()
                                }

                                cmd.startsWith("spy ") -> {
                                    val supported = listOf("pdf", "txt", "json", "xml", "java", "log", "md", "py", "yml", "yaml", "sh")
                                    val target = File(StateControl.session.currentDir.value, StateControl.prefix)

                                    if (target.exists() && target.isFile && supported.contains(target.extension.lowercase())) {
                                        StateControl.session.spyLines = extractTextLines(target)
                                        StateControl.session.spyIndex = 0
                                        StateControl.session.splitRatio.value = 0.3f
                                        StateControl.session.showSpy = true
                                        StateControl.session.showFileEditor = false
                                        StateControl.session.spyFileName = target.name
                                        StateControl.session.spyDirContent = emptyList()
                                        StateControl.session.mode.value = "[spy mode]"
                                    } else if (target.exists() && target.isDirectory) {
                                        StateControl.session.spyDirContent = listDirectory(target)
                                        StateControl.session.spyIndex = 0
                                        StateControl.session.splitRatio.value = 0.3f
                                        StateControl.session.showSpy = true
                                        StateControl.session.showFileEditor = false
                                        StateControl.session.spyLines = emptyList()
                                        StateControl.session.spyFileName = target.name
                                        StateControl.session.mode.value = "[spy mode]"
                                    } else {
                                        StateControl.session.mode.value = ""
                                    }
                                }

                                cmd.startsWith("file ") -> {
                                    val parts = cmd.split(" ")
                                    if (parts.size == 2) {
                                        val file = File(StateControl.session.currentDir.value, parts[1])
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
                                }

                                cmd.startsWith("mk ") -> {
                                    val expression = cmd.removePrefix("mk ").trim()
                                    val parts = expression.split("->", ";")
                                    val currentDir = StateControl.session.currentDir.value

                                    val mainDirs = parts.getOrNull(0)?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
                                    val subDirs = parts.getOrNull(1)?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
                                    val finalDirs = parts.getOrNull(2)?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()

                                    var lastMainDir: File? = null

                                    for (dirName in mainDirs) {
                                        val dir = File(currentDir, dirName)
                                        dir.mkdirs()
                                        lastMainDir = dir
                                    }

                                    for (dirName in subDirs) {
                                        lastMainDir?.let {
                                            File(it, dirName).mkdirs()
                                        }
                                    }

                                    for (dirName in finalDirs) {
                                        File(currentDir, dirName).mkdirs()
                                    }

                                    StateControl.session.output = listDirectory(currentDir)
                                    resetVisualState()
                                }

                                else -> {
                                    StateControl.session.mode.value = ""
                                }
                            }
                        }
                    }

                    StateControl.inputText = TextFieldValue("")
                    true
                } else false
            },
        singleLine = true
    )
}

// 🔁 Função auxiliar para limpar visualizações laterais
private fun resetVisualState() {
    StateControl.session.showSpy = false
    StateControl.session.spyLines = emptyList()
    StateControl.session.spyDirContent = emptyList()
    StateControl.session.spyIndex = 0
    StateControl.session.spyFileName = ""
    StateControl.session.showFileEditor = false
    StateControl.session.fileEditorContent = ""
    StateControl.session.fileEditorPath = null
    StateControl.session.mode.value = ""
}
