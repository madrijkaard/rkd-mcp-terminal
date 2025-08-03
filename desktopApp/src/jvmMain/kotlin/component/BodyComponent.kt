package component

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import control.StateControl

@Composable
fun BodyComponent() {
    Row(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(StateControl.session.splitRatio.value)
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                StateControl.session.output.forEach { file ->
                    val name = file.name
                    val icon = if (file.isDirectory) "\uD83D\uDCC1" else "\uD83D\uDCC4"

                    val annotated = when {
                        StateControl.isCdCommand && file.isDirectory && name.startsWith(StateControl.prefix) ->
                            buildAnnotatedString {
                                append("$icon ")
                                withStyle(SpanStyle(color = Color.White)) {
                                    append(name.substring(0, StateControl.prefix.length))
                                }
                                withStyle(SpanStyle(color = Color.Green)) {
                                    append(name.substring(StateControl.prefix.length))
                                }
                            }

                        StateControl.isSpyCommand && file.isFile && name.startsWith(StateControl.prefix) ->
                            buildAnnotatedString {
                                append("$icon ")
                                withStyle(SpanStyle(color = Color.Blue)) {
                                    append(name.substring(0, StateControl.prefix.length))
                                }
                                withStyle(SpanStyle(color = Color.Green)) {
                                    append(name.substring(StateControl.prefix.length))
                                }
                            }

                        StateControl.isFileCommand && file.isFile && name.startsWith(StateControl.prefix) && file.extension.lowercase() != "pdf" ->
                            buildAnnotatedString {
                                append("$icon ")
                                withStyle(SpanStyle(color = Color(0xFFFFA500))) {
                                    append(name.substring(0, StateControl.prefix.length))
                                }
                                withStyle(SpanStyle(color = Color.Green)) {
                                    append(name.substring(StateControl.prefix.length))
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

        if (StateControl.session.showSpy || StateControl.session.showFileEditor) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(2.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val sensitivity = 0.1f
                            val deltaRatio = (dragAmount.x / size.width.toFloat()) * sensitivity
                            StateControl.session.splitRatio.value =
                                (StateControl.session.splitRatio.value + deltaRatio).coerceIn(0.1f, 0.9f)
                        }
                    }
                    .background(Color.Green)
            )

            Box(
                Modifier
                    .weight(1f - StateControl.session.splitRatio.value)
                    .padding(start = 8.dp)
            ) {
                if (StateControl.session.showSpy) {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        StateControl.session.spyLines.drop(StateControl.session.spyIndex).take(100).forEach {
                            Text(
                                text = it,
                                color = Color.Green,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else if (StateControl.session.showFileEditor) {
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
                            value = StateControl.session.fileEditorContent,
                            onValueChange = { StateControl.session.fileEditorContent = it },
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
}
