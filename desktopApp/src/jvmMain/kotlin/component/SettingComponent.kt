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
import androidx.compose.ui.window.Dialog

@Composable
fun SettingComponent(onClose: () -> Unit) {
    Dialog(onDismissRequest = onClose) {
        Box(
            modifier = Modifier
                .size(300.dp, 180.dp)
                .background(Color.DarkGray)
                .border(2.dp, Color.Green)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Configurações", color = Color.Green, fontFamily = FontFamily.Monospace, fontSize = 16.sp)
                Spacer(Modifier.height(16.dp))
                Text("Conteúdo do popup", color = Color.White, fontFamily = FontFamily.Monospace)
                Spacer(Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .clickable(onClick = onClose)
                        .background(Color.Green)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("Fechar", color = Color.Black, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}
