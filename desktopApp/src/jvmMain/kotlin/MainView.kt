import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
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

    var showFileEditor by remember { mutableStateOf(false) }
    var fileEditorContent by remember { mutableStateOf("") }
    var fileEditorPath by remember { mutableStateOf<File?>(null) }

    val inputParts = inputText.trim().split(" ")
    val isCdCommand = inputParts.firstOrNull() == "cd"
    val isSpyCommand = inputParts.firstOrNull() == "spy" && inputParts.size == 2
    val isFileCommand = inputParts.firstOrNull() == "file" && inputParts.size == 2
    val prefix = if (inputParts.size > 1) inputParts[1] else ""

    val matchedDir = output.filter { it.isDirectory && it.name.startsWith(prefix) }
    val matchedFile = output.filter { it.isFile && it.name.startsWith(prefix) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentDir.path,
                color = Color.Green,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp
            )
            when {
                showSpy -> Text(
                    text = "[spy mode]",
                    color = Color.Green,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                )
                showFileEditor -> Text(
                    text = "[edit mode]",
                    color = Color.Green,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                )
            }
        }

        Row(modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                Column {
                    output.forEach { file ->
                        val name = file.name
                        val icon = if (file.isDirectory) "\uD83D\uDCC1" else "\uD83D\uDCC4"

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
                            isSpyCommand && file.isFile && name.startsWith(prefix) ->
                                buildAnnotatedString {
                                    append("$icon ")
                                    withStyle(SpanStyle(color = Color.Blue)) {
                                        append(name.substring(0, prefix.length))
                                    }
                                    withStyle(SpanStyle(color = Color.Green)) {
                                        append(name.substring(prefix.length))
                                    }
                                }
                            isFileCommand && file.isFile && name.startsWith(prefix) && file.extension.lowercase() != "pdf" ->
                                buildAnnotatedString {
                                    append("$icon ")
                                    withStyle(SpanStyle(color = Color(0xFFFFA500))) {
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

            if (showSpy || showFileEditor) {
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
                ) {
                    if (showSpy) {
                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {
                            spyLines.drop(spyIndex).take(100).forEach {
                                Text(
                                    text = it,
                                    color = Color.Green,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else if (showFileEditor) {
                        val scrollState = rememberScrollState()
                        val focusRequester = remember { FocusRequester() }

                        LaunchedEffect(Unit) {
                            focusRequester.requestFocus()
                        }

                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.Black)
                                .padding(8.dp)
                                .border(1.dp, Color.Green)
                                .verticalScroll(scrollState)
                                .focusable()
                                .clickable { focusRequester.requestFocus() }
                        ) {
                            BasicTextField(
                                value = fileEditorContent,
                                onValueChange = { fileEditorContent = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester),
                                textStyle = TextStyle(
                                    color = Color.Green,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp
                                ),
                                singleLine = false,
                                maxLines = Int.MAX_VALUE,
                                cursorBrush = SolidColor(Color.Green)
                            )
                        }
                    }
                }
            }
        }

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
                        when {
                            isCdCommand && matchedDir.size == 1 -> {
                                inputText = "cd ${matchedDir[0].name}"
                                true
                            }
                            isSpyCommand && matchedFile.size == 1 -> {
                                inputText = "spy ${matchedFile[0].name}"
                                true
                            }
                            isFileCommand && matchedFile.size == 1 && matchedFile[0].extension.lowercase() != "pdf" -> {
                                inputText = "file ${matchedFile[0].name}"
                                true
                            }
                            else -> false
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
                            "spy continue" -> if (showSpy) spyIndex += 100
                            "spy exit" -> {
                                showSpy = false
                                spyLines = emptyList()
                                spyIndex = 0
                                spyFileName = ""
                            }
                            "file save" -> {
                                if (showFileEditor && fileEditorPath != null) {
                                    fileEditorPath?.writeText(fileEditorContent)
                                    showFileEditor = false
                                    fileEditorContent = ""
                                    fileEditorPath = null
                                    output = listDirectory(currentDir)
                                }
                            }
                            "file cancel" -> {
                                showFileEditor = false
                                fileEditorContent = ""
                                fileEditorPath = null
                                output = listDirectory(currentDir)
                            }
                            else -> {
                                if (cmd.startsWith("cd ")) {
                                    val target = File(currentDir, prefix)
                                    if (target.exists() && target.isDirectory) {
                                        currentDir = target
                                        output = listDirectory(currentDir)
                                    }
                                } else if (cmd.startsWith("spy ")) {
                                    val supportedExtensions = listOf("pdf", "txt", "json", "xml", "java", "log", "md", "py", "yml", "yaml")
                                    val target = File(currentDir, prefix)
                                    if (target.exists() && supportedExtensions.contains(target.extension.lowercase())) {
                                        spyLines = extractTextLines(target)
                                        spyIndex = 0
                                        showSpy = true
                                        showFileEditor = false
                                        spyFileName = target.name
                                    }
                                } else if (cmd.startsWith("file ")) {
                                    val parts = cmd.split(" ")
                                    if (parts.size == 2) {
                                        val file = File(currentDir, parts[1])
                                        if (file.extension.lowercase() == "pdf") return@onPreviewKeyEvent true
                                        val exists = file.exists()
                                        if (!exists) file.createNewFile()
                                        fileEditorPath = file
                                        fileEditorContent = if (exists) file.readText() else ""
                                        showFileEditor = true
                                        showSpy = false
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
    val hiddenFilesWindows = listOf("NTUSER.DAT", "ntuser.dat.LOG1", "ntuser.dat.LOG2", "NTUSER.DAT{", "ntuser.ini")
    return dir.listFiles()
        ?.filter {
            !it.name.startsWith(".") && !it.name.endsWith(".ini") &&
                    hiddenFilesWindows.none { prefix -> it.name.startsWith(prefix) }
        }
        ?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
        ?: emptyList()
}

private fun extractTextLines(file: File): List<String> {
    return try {
        when (file.extension.lowercase()) {
            "pdf" -> PDDocument.load(file).use { PDFTextStripper().getText(it).split("\n") }
            else -> file.readLines()
        }
    } catch (e: Exception) {
        listOf("Erro ao abrir arquivo: ${e.message}")
    }
}
