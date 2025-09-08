import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import control.AppScreen
import control.StateControl
import decorator.*

@Composable
fun MainView() {
    when (StateControl.currentScreen) {

        AppScreen.FILE_SYSTEM -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                headerDecorator {
                }

                subHeaderDecorator {
                    Column(modifier = Modifier.weight(1f)) {
                        bodyDecorator {
                        }
                    }
                }

                inputDecorator {
                    settingsDecorator {
                    }
                }
            }
        }
    }
}
