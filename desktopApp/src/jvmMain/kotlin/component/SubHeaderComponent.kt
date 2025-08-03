package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = StateControl.session.currentDir.path,
            color = Color.Green,
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp
        )
        when {
            StateControl.session.showSpy -> Text(
                "[spy mode]",
                color = Color.Green,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp
            )

            StateControl.session.showFileEditor -> Text(
                "[edit mode]",
                color = Color.Green,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp
            )
        }
    }
}