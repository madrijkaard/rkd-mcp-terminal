package decorator

import androidx.compose.runtime.Composable
import component.HeaderComponent

@Composable
fun headerDecorator(content: @Composable () -> Unit) {
    HeaderComponent()
    content()
}
