package control

import androidx.compose.runtime.*
import dto.SessionDto
import java.io.File

object StateControl {

    var inputText by mutableStateOf("")

    val sessions = mutableStateListOf(SessionDto())

    var selectedTabIndex by mutableStateOf(0)

    var showSettingsPopup by mutableStateOf(false)

    val session: SessionDto
        get() = sessions[selectedTabIndex]

    val currentDir: File
        get() = session.currentDir

    val output: List<File>
        get() = session.output

    val inputParts: List<String>
        get() = inputText.trim().split(" ")

    val isCdCommand: Boolean
        get() = inputParts.firstOrNull() == "cd"

    val isSpyCommand: Boolean
        get() = inputParts.firstOrNull() == "spy" && inputParts.size == 2

    val isFileCommand: Boolean
        get() = inputParts.firstOrNull() == "file" && inputParts.size == 2

    val prefix: String
        get() = if (inputParts.size > 1) inputParts[1] else ""

    val matchedDir: List<File>
        get() = output.filter { it.isDirectory && it.name.startsWith(prefix) }

    val matchedFile: List<File>
        get() = output.filter { it.isFile && it.name.startsWith(prefix) }

    init {
        println("BillPughSingleton instanciado")
    }

    fun doSomething() {
        println("Executando lógica do singleton")
    }
}
