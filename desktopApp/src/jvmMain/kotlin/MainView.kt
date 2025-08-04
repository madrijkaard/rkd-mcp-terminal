import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import decorator.*

@Composable
fun MainView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Cabeçalho com abas e engrenagem
        headerDecorator {
            // SubHeader depende da aba selecionada
        }

        // SubHeader deve estar aqui, fora do headerDecorator
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
