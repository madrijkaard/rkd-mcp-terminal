package component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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

class HeaderComponent : UiComponent {

    @Composable
    override fun Render() {
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
                StateControl.sessions.forEachIndexed { index, _ ->
                    val isSelected = StateControl.selectedTabIndex == index
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .fillMaxHeight()
                            .background(if (isSelected) Color(0xFF2D2D2D) else Color(0xFF1E1E1E))
                            .border(1.dp, Color.Black)
                            .clickable { StateControl.selectedTabIndex = index }
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "Tab ${index + 1}",
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
                    .clickable { StateControl.showSettingsPopup = true },
                contentAlignment = Alignment.Center
            ) {
                Text("⚙️", fontSize = 16.sp)
            }
        }
    }
}