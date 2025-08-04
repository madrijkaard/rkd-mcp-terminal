package decorator

import androidx.compose.runtime.Composable
import component.InputComponent

@Composable
fun inputDecorator(content: @Composable () -> Unit) {
    content()
    InputComponent()
}
