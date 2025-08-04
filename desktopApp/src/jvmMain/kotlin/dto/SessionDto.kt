package dto

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import util.listDirectory
import java.io.File

data class SessionDto(
    var currentDir: File = File(System.getProperty("user.home")),
    var output: List<File> = listDirectory(File(System.getProperty("user.home"))),
    var showSpy: Boolean = false,
    var spyLines: List<String> = emptyList(),
    var spyIndex: Int = 0,
    var spyFileName: String = "",
    var showFileEditor: Boolean = false,
    var fileEditorContent: String = "",
    var fileEditorPath: File? = null,
    var splitRatio: MutableState<Float> = mutableStateOf(0.3f),
    var mode: MutableState<String> = mutableStateOf("")
)
