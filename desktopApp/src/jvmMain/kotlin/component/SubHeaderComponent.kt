package component

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import composite.UiComponent
import control.StateControl

class SubHeaderComponent : UiComponent {

    @Composable
    override fun Render() {
        val session = StateControl.session
        val modeLabel = session.mode.value
        val currentPath = session.currentDir.value.absolutePath

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentPath,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Green,
                    modifier = Modifier.weight(1f)
                )

                if (modeLabel.isNotEmpty()) {
                    Text(
                        text = modeLabel,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Green
                    )
                }
            }
        }
    }
}