import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
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
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File

@Composable
fun MainView() {
    var inputText by remember { mutableStateOf("") }
    var currentDir by remember { mutableStateOf(File(System.getProperty("user.home"))) }
    var output by remember { mutableStateOf(listDirectory(currentDir)) }
    var showSpy by remember { mutableStateOf(false) }
    var spyLines by remember { mutableStateOf(listOf<String>()) }
    var spyIndex by remember { mutableStateOf(0) }
    var spyFileName by remember { mutableStateOf("") }

    val inputParts = inputText.trim().split(" ")
    val isCdCommand = inputParts.firstOrNull() == "cd"
    val isSpyCommand = inputParts.firstOrNull() == "spy" && inputParts.size == 2
    val prefix = if (inputParts.size > 1) inputParts[1] else ""

    val matchedDir = output.filter { it.isDirectory && it.name.startsWith(prefix) }
    val matchedFile = output.filter { it.isFile && it.name.startsWith(prefix) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(12.dp)
    ) {
        // Caminho atual
        Text(
            text = currentDir.path,
            color = Color.Green,
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(modifier = Modifier.weight(1f)) {
            // Listagem do lado esquerdo com rolagem
            Box(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                Column {
                    output.forEach { file ->
                        val name = file.name
                        val icon = if (file.isDirectory) "📁" else "📄"

                        val annotated = when {
                            isCdCommand && file.isDirectory && name.startsWith(prefix) ->
                                buildAnnotatedString {
                                    append("$icon ")
                                    withStyle(SpanStyle(color = Color.White)) {
                                        append(name.substring(0, prefix.length))
                                    }
                                    withStyle(SpanStyle(color = Color.Green)) {
                                        append(name.substring(prefix.length))
                                    }
                                }
                            isSpyCommand && prefix.isNotEmpty() && file.isFile && name.startsWith(prefix) ->
                                buildAnnotatedString {
                                    append("$icon ")
                                    withStyle(SpanStyle(color = Color.Blue)) {
                                        append(name.substring(0, prefix.length))
                                    }
                                    withStyle(SpanStyle(color = Color.Green)) {
                                        append(name.substring(prefix.length))
                                    }
                                }
                            else -> buildAnnotatedString {
                                append("$icon ")
                                withStyle(SpanStyle(color = Color.Green)) {
                                    append(name)
                                }
                            }
                        }

                        Text(
                            text = annotated,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Linha divisória verde e conteúdo espiado
            if (showSpy) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                        .background(Color.Green)
                )
                Box(
                    Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Column {
                        spyLines.drop(spyIndex).take(100).forEach {
                            Text(
                                text = it,
                                color = Color.Green,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Campo de entrada
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
                        if (isCdCommand && matchedDir.size == 1 && prefix.length < matchedDir[0].name.length) {
                            inputText = "cd ${matchedDir[0].name}"
                            true
                        } else if (isSpyCommand && matchedFile.size == 1 && prefix.length < matchedFile[0].name.length) {
                            inputText = "spy ${matchedFile[0].name}"
                            true
                        } else {
                            false
                        }
                    } else if (event.key == Key.Enter && event.type == KeyEventType.KeyUp) {
                        when (val cmd = inputText.trim()) {
                            "ls" -> output = listDirectory(currentDir)

                            "cd .." -> {
                                currentDir.parentFile?.takeIf { it.exists() }?.let {
                                    currentDir = it
                                    output = listDirectory(it)
                                }
                            }

                            "home" -> {
                                currentDir = File(System.getProperty("user.home"))
                                output = listDirectory(currentDir)
                            }

                            "spy continue" -> {
                                if (showSpy) spyIndex += 100
                            }

                            "spy exit" -> {
                                showSpy = false
                                spyLines = emptyList()
                                spyIndex = 0
                                spyFileName = ""
                            }

                            else -> {
                                if (cmd.startsWith("cd ")) {
                                    val target = File(currentDir, prefix)
                                    if (target.exists() && target.isDirectory) {
                                        currentDir = target
                                        output = listDirectory(currentDir)
                                    }
                                } else if (cmd.startsWith("spy ")) {
                                    val target = File(currentDir, prefix)
                                    if (target.exists() && target.extension.lowercase() == "pdf") {
                                        spyLines = extractPdfTextLines(target)
                                        spyIndex = 0
                                        showSpy = true
                                        spyFileName = target.name
                                    }
                                }
                            }
                        }

                        inputText = ""
                        true
                    } else {
                        false
                    }
                },
            singleLine = true
        )
    }
}

private fun listDirectory(dir: File): List<File> {
    val hiddenFilesWindows = listOf(
        "NTUSER.DAT", "ntuser.dat.LOG1", "ntuser.dat.LOG2",
        "NTUSER.DAT{", "ntuser.ini"
    )

    return dir.listFiles()
        ?.filter {
            !it.name.startsWith(".") &&
                    !it.name.endsWith(".ini") &&
                    hiddenFilesWindows.none { prefix -> it.name.startsWith(prefix) }
        }
        ?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
        ?: emptyList()
}

private fun extractPdfTextLines(file: File): List<String> {
    return try {
        PDDocument.load(file).use { document ->
            val stripper = PDFTextStripper()
            stripper.getText(document).split("\n")
        }
    } catch (e: Exception) {
        listOf("Erro ao abrir PDF: ${e.message}")
    }
}
