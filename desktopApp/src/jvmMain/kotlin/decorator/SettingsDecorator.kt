package decorator

import androidx.compose.runtime.Composable
import component.SettingComponent
import control.StateControl

@Composable
fun settingsDecorator(content: @Composable () -> Unit) {
    content()

    if (StateControl.showSettingsPopup) {
        SettingComponent(onClose = { StateControl.showSettingsPopup = false })
    }
}
