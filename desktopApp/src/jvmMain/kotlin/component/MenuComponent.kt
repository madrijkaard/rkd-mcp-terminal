package component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import control.AppScreen
import control.StateControl

@Composable
fun MenuScreen() {
    val labels = listOf(
        "File System", "Opção 2", "Opção 3", "Opção 4",
        "Opção 5", "Opção 6", "Opção 7", "Opção 8"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        for (row in 0 until 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (col in 0 until 4) {
                    val index = row * 4 + col
                    Box(
                        modifier = Modifier
                            .width(0.dp) // placeholder, será expandido por weight
                            .weight(1f)
                            .height(80.dp) // ⬅️ reduzido para tornar retangular
                            .background(Color.Black)
                            .border(2.dp, Color.Green)
                            .clickable {
                                if (index == 0) {
                                    StateControl.currentScreen = AppScreen.FILE_SYSTEM
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = labels[index],
                            color = Color.Green,
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
