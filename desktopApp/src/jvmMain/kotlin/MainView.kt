import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import component.*
import control.StateControl

@Composable
fun MainView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(12.dp)
    ) {
        HeaderComponent()
        SubHeaderComponent()

        Row(modifier = Modifier.weight(1f)) {
            BodyComponent()
        }

        InputComponent()
    }

    if (StateControl.showSettingsPopup) {
        SettingComponent(onClose = { StateControl.showSettingsPopup = false })
    }
}
