import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File

data class Session(
    var currentDir: File = File(System.getProperty("user.home")),
    var output: List<File> = listDirectory(File(System.getProperty("user.home"))),
    var showSpy: Boolean = false,
    var spyLines: List<String> = emptyList(),
    var spyIndex: Int = 0,
    var spyFileName: String = "",
    var showFileEditor: Boolean = false,
    var fileEditorContent: String = "",
    var fileEditorPath: File? = null,
    var splitRatio: Float = 0.3f
)

@Composable
fun MainView() {
    var inputText by remember { mutableStateOf("") }
    val sessions = remember { mutableStateListOf(Session()) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showSettingsPopup by remember { mutableStateOf(false) }

    val session = sessions[selectedTabIndex]
    val currentDir = session.currentDir
    val output = session.output

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
                .height(32.dp)
                .background(Color(0xFF1E1E1E))
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState())
            ) {
                sessions.forEachIndexed { index, _ ->
                    val isSelected = selectedTabIndex == index
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .fillMaxHeight()
                            .background(if (isSelected) Color(0xFF2D2D2D) else Color(0xFF1E1E1E))
                            .border(1.dp, Color.Black)
                            .clickable { selectedTabIndex = index }
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "Aba ${index + 1}",
                            color = if (isSelected) Color.Green else Color.LightGray,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .width(32.dp)
                    .fillMaxHeight()
                    .clickable { showSettingsPopup = true },
                contentAlignment = Alignment.Center
            ) {
                Text("⚙️", fontSize = 16.sp)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = session.currentDir.path,
                color = Color.Green,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp
            )
            when {
                session.showSpy -> Text("[spy mode]", color = Color.Green, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
                session.showFileEditor -> Text("[edit mode]", color = Color.Green, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
            }
        }

        Row(modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.weight(session.splitRatio).verticalScroll(rememberScrollState())) {
                Column {
                    session.output.forEach { file ->
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

            if (session.showSpy || session.showFileEditor) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .width(4.dp)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val sensitivity = 0.05f
                                val deltaRatio = (dragAmount.x / size.width.toFloat()) * sensitivity
                                session.splitRatio = (session.splitRatio + deltaRatio).coerceIn(0.1f, 0.9f)
                            }
                        }
                        .background(Color.Green)
                )

                Box(
                    Modifier
                        .weight(1f - session.splitRatio)
                        .padding(start = 8.dp)
                ) {
                    if (session.showSpy) {
                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {
                            session.spyLines.drop(session.spyIndex).take(100).forEach {
                                Text(
                                    text = it,
                                    color = Color.Green,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else if (session.showFileEditor) {
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
                                value = session.fileEditorContent,
                                onValueChange = { session.fileEditorContent = it },
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
                            "new tab" -> {
                                sessions.add(Session())
                                selectedTabIndex = sessions.lastIndex
                            }
                            "ls" -> session.output = listDirectory(session.currentDir)
                            "cd .." -> {
                                session.currentDir.parentFile?.takeIf { it.exists() }?.let {
                                    session.currentDir = it
                                    session.output = listDirectory(it)
                                }
                            }
                            "home" -> {
                                session.currentDir = File(System.getProperty("user.home"))
                                session.output = listDirectory(session.currentDir)
                            }
                            "spy continue" -> if (session.showSpy) session.spyIndex += 100
                            "spy exit" -> {
                                session.showSpy = false
                                session.spyLines = emptyList()
                                session.spyIndex = 0
                                session.spyFileName = ""
                            }
                            "file save" -> {
                                session.fileEditorPath?.writeText(session.fileEditorContent)
                                session.showFileEditor = false
                                session.fileEditorContent = ""
                                session.fileEditorPath = null
                                session.output = listDirectory(session.currentDir)
                            }
                            "file cancel" -> {
                                session.showFileEditor = false
                                session.fileEditorContent = ""
                                session.fileEditorPath = null
                                session.output = listDirectory(session.currentDir)
                            }
                            else -> {
                                if (cmd.startsWith("cd ")) {
                                    val target = File(session.currentDir, prefix)
                                    if (target.exists() && target.isDirectory) {
                                        session.currentDir = target
                                        session.output = listDirectory(target)
                                    }
                                } else if (cmd.startsWith("spy ")) {
                                    val supported = listOf("pdf", "txt", "json", "xml", "java", "log", "md", "py", "yml", "yaml", "sh")
                                    val target = File(session.currentDir, prefix)
                                    if (target.exists() && supported.contains(target.extension.lowercase())) {
                                        session.spyLines = extractTextLines(target)
                                        session.spyIndex = 0
                                        session.splitRatio = 0.3f
                                        session.showSpy = true
                                        session.showFileEditor = false
                                        session.spyFileName = target.name
                                    }
                                } else if (cmd.startsWith("file ")) {
                                    val parts = cmd.split(" ")
                                    if (parts.size == 2) {
                                        val file = File(session.currentDir, parts[1])
                                        if (file.extension.lowercase() == "pdf") return@onPreviewKeyEvent true
                                        val exists = file.exists()
                                        if (!exists) file.createNewFile()
                                        session.fileEditorPath = file
                                        session.fileEditorContent = if (exists) file.readText() else ""
                                        session.splitRatio = 0.3f
                                        session.showFileEditor = true
                                        session.showSpy = false
                                    }
                                }
                            }
                        }
                        inputText = ""
                        true
                    } else false
                },
            singleLine = true
        )
    }

    if (showSettingsPopup) {
        Dialog(onDismissRequest = { showSettingsPopup = false }) {
            Box(
                modifier = Modifier
                    .size(300.dp, 180.dp)
                    .background(Color.DarkGray)
                    .border(2.dp, Color.Green)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Configurações", color = Color.Green, fontFamily = FontFamily.Monospace, fontSize = 16.sp)
                    Spacer(Modifier.height(16.dp))
                    Text("Conteúdo do popup", color = Color.White, fontFamily = FontFamily.Monospace)
                    Spacer(Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .clickable { showSettingsPopup = false }
                            .background(Color.Green)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("Fechar", color = Color.Black, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
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
