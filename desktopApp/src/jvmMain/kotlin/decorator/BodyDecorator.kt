package decorator

import androidx.compose.runtime.Composable
import component.BodyComponent

@Composable
fun bodyDecorator(content: @Composable () -> Unit) {
    BodyComponent()
    content()
}

