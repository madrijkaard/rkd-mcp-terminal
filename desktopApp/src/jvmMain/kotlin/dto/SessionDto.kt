package dto

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import util.listDirectory
import java.io.File

data class SessionDto(
    var currentDir: MutableState<File> = mutableStateOf(File(System.getProperty("user.home"))),
    var output: List<File> = listDirectory(File(System.getProperty("user.home"))),

    // Modo de visualização (spy) para arquivos
    var showSpy: Boolean = false,
    var spyLines: List<String> = emptyList(),
    var spyIndex: Int = 0,
    var spyFileName: String = "",

    // Novo: Modo de visualização (spy) para diretórios
    var spyDirContent: List<File> = emptyList(),

    // Editor de arquivos
    var showFileEditor: Boolean = false,
    var fileEditorContent: String = "",
    var fileEditorPath: File? = null,

    // Layout
    var splitRatio: MutableState<Float> = mutableStateOf(0.3f),
    var mode: MutableState<String> = mutableStateOf("")
)
