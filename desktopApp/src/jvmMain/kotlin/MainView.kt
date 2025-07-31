import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

@Composable
fun MainView() {
    var inputText by remember { mutableStateOf("") }
    var currentDir by remember { mutableStateOf(File(System.getProperty("user.home"))) }
    var output by remember { mutableStateOf(listDirectory(currentDir)) }

    val inputParts = inputText.trim().split(" ")
    val isCdCommand = inputParts.firstOrNull() == "cd"
    val cdPrefix = if (isCdCommand && inputParts.size > 1) inputParts[1] else ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(12.dp)
    ) {
        // Mostra o diretório atual (sem o comando)
        Text(
            text = currentDir.path,
            color = Color.Green,
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Listagem com scroll
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            output.forEach { file ->
                val fileName = file.name
                val isDir = file.isDirectory
                val icon = if (isDir) "📁" else "📄"

                if (isDir && cdPrefix.isNotBlank() && fileName.startsWith(cdPrefix)) {
                    val annotated = buildAnnotatedString {
                        append("$icon ")
                        withStyle(SpanStyle(color = Color.White)) {
                            append(fileName.substring(0, cdPrefix.length))
                        }
                        withStyle(SpanStyle(color = Color.Green)) {
                            append(fileName.substring(cdPrefix.length))
                        }
                    }
                    Text(text = annotated, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
                } else {
                    Text(
                        text = "$icon $fileName",
                        color = Color.Green,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Campo de entrada com autocomplete e comandos
        BasicTextField(
            value = inputText,
            onValueChange = { inputText = it },
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
                        val candidates = output.filter { it.isDirectory && it.name.startsWith(cdPrefix) }
                        if (isCdCommand && candidates.size == 1 && cdPrefix.length < candidates[0].name.length) {
                            inputText = "cd ${candidates[0].name}"
                            true
                        } else false
                    } else if (event.key == Key.Enter && event.type == KeyEventType.KeyUp) {
                        handleCommand(
                            inputText.trim(),
                            currentDir,
                            onDirChange = {
                                currentDir = it
                                output = listDirectory(it)
                            },
                            onRefresh = {
                                output = listDirectory(currentDir)
                            }
                        )
                        inputText = ""
                        true
                    } else false
                },
            singleLine = true
        )
    }
}

private fun listDirectory(dir: File): List<File> {
    val isWindows = System.getProperty("os.name").lowercase().contains("win")
    val unwantedWindowsFiles = listOf(
        "ntuser.dat",
        "ntuser.dat.log1",
        "ntuser.dat.log2",
        "ntuser.ini"
    )

    return dir.listFiles()
        ?.filter { file ->
            val nameLower = file.name.lowercase()

            val hiddenByDotOrIni = nameLower.startsWith(".") || nameLower.endsWith(".ini")
            val hiddenByWindows = isWindows && (
                    nameLower in unwantedWindowsFiles ||
                            nameLower.contains("ntuser.dat{") ||
                            nameLower.endsWith(".regtrans-ms") ||
                            nameLower.endsWith(".blf")
                    )

            !hiddenByDotOrIni && !hiddenByWindows
        }
        ?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
        ?: emptyList()
}

private fun handleCommand(
    input: String,
    currentDir: File,
    onDirChange: (File) -> Unit,
    onRefresh: () -> Unit
) {
    when {
        input == "ls" -> onRefresh()
        input == "cd .." -> {
            currentDir.parentFile?.takeIf { it.exists() }?.let { onDirChange(it) }
        }
        input.startsWith("cd ") -> {
            val targetName = input.removePrefix("cd ").trim()
            val targetDir = File(currentDir, targetName)
            if (targetDir.exists() && targetDir.isDirectory) {
                onDirChange(targetDir)
            }
        }
    }
}
