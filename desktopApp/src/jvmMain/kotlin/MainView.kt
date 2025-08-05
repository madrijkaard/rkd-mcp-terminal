import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import component.MenuScreen
import control.AppScreen
import control.StateControl
import decorator.*

@Composable
fun MainView() {
    when (StateControl.currentScreen) {
        AppScreen.MENU -> {
            MenuScreen()
        }

        AppScreen.FILE_SYSTEM -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                // Cabeçalho com abas e engrenagem
                headerDecorator {
                    // SubHeader depende da aba selecionada
                }

                // SubHeader deve estar fora do headerDecorator
                subHeaderDecorator {
                    // Corpo da aba
                    Column(modifier = Modifier.weight(1f)) {
                        bodyDecorator {
                            // Saída de arquivos, visualizações, etc.
                        }
                    }
                }

                // Input de comandos e popup de configurações
                inputDecorator {
                    settingsDecorator {
                        // Fim da hierarquia
                    }
                }
            }
        }
    }
}
