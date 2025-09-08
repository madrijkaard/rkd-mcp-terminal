package control

import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import dto.SessionDto
import java.io.File

enum class AppScreen {
    FILE_SYSTEM
}

object StateControl {

    var currentScreen by mutableStateOf(AppScreen.FILE_SYSTEM)

    var inputText by mutableStateOf(TextFieldValue(""))

    val sessions = mutableStateListOf(SessionDto())

    var selectedTabIndex by mutableStateOf(0)

    var showSettingsPopup by mutableStateOf(false)

    val session: SessionDto
        get() = sessions[selectedTabIndex]

    val currentDir: File
        get() = session.currentDir.value

    val output: List<File>
        get() = session.output.value

    val inputParts: List<String>
        get() = inputText.text.trim().split(" ")

    val isCdCommand: Boolean
        get() = inputParts.firstOrNull()?.lowercase() == "cd"

    val isSpyCommand: Boolean
        get() = inputParts.firstOrNull()?.lowercase() == "spy" && inputParts.size == 2

    val isFileCommand: Boolean
        get() = inputParts.firstOrNull()?.lowercase() == "file" && inputParts.size == 2

    val prefix: String
        get() = if (inputParts.size > 1) inputParts[1].lowercase() else ""

    val matchedDir: List<File>
        get() = output.filter { it.isDirectory && it.name.lowercase().startsWith(prefix) }

    val matchedFile: List<File>
        get() = output.filter { it.isFile && it.name.lowercase().startsWith(prefix) }

    val matchedAll: List<File>
        get() = output.filter { it.name.lowercase().startsWith(prefix) }
}
