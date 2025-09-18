package composite

import androidx.compose.runtime.Composable

interface UiComponent {
    @Composable
    fun Render()
}