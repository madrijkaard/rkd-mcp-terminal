package component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import control.StateControl

@Composable
fun SubHeaderComponent() {
    val session = StateControl.session
    val modeLabel = session.mode.value

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
                text = session.currentDir.absolutePath,
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
