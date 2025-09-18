package component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import composite.UiComponent
import control.StateControl

class SettingComponent : UiComponent {

    @Composable
    override fun Render() {
        if (StateControl.showSettingsPopup) {
            var selectedTab by remember { mutableStateOf(0) }
            val tabs = listOf("Geral", "Aparência", "Sobre")

            Dialog(onDismissRequest = { StateControl.showSettingsPopup = false }) {
                Box(
                    modifier = Modifier
                        .size(400.dp, 250.dp)
                        .background(Color.DarkGray)
                        .border(2.dp, Color.Green)
                        .padding(16.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            tabs.forEachIndexed { index, title ->
                                Text(
                                    text = title,
                                    color = if (selectedTab == index) Color.Green else Color.LightGray,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 14.sp,
                                    modifier = Modifier
                                        .clickable { selectedTab = index }
                                        .border(
                                            width = 1.dp,
                                            color = if (selectedTab == index) Color.Green else Color.Gray
                                        )
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        when (selectedTab) {
                            0 -> GeralTab()
                            1 -> AparenciaTab()
                            2 -> SobreTab()
                        }

                        Spacer(Modifier.weight(1f))

                        Box(
                            modifier = Modifier
                                .align(Alignment.End)
                                .clickable(onClick = { StateControl.showSettingsPopup = false })
                                .background(Color.Green)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("Fechar", color = Color.Black, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun GeralTab() {
        Text("Configurações gerais aqui", color = Color.White, fontFamily = FontFamily.Monospace)
    }

    @Composable
    private fun AparenciaTab() {
        Text("Opções de aparência (cores, temas, etc.)", color = Color.White, fontFamily = FontFamily.Monospace)
    }

    @Composable
    private fun SobreTab() {
        Column {
            Text("Terminal Compose v1.0", color = Color.White, fontFamily = FontFamily.Monospace)
            Text("Desenvolvido por você 💻", color = Color.White, fontFamily = FontFamily.Monospace)
        }
    }
}