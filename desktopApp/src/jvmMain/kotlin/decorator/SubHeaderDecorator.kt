package decorator

import androidx.compose.runtime.Composable
import component.SubHeaderComponent

@Composable
fun subHeaderDecorator(content: @Composable () -> Unit) {
    SubHeaderComponent()
    content()
}
