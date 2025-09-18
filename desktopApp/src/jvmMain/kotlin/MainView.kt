import androidx.compose.runtime.Composable
import component.*
import composite.VerticalComposite
import control.AppScreen
import control.StateControl

@Composable
fun MainView() {
    when (StateControl.currentScreen) {
        AppScreen.FILE_SYSTEM -> {
            val rootComposite = VerticalComposite()
            val header = HeaderComponent()
            rootComposite.add(header)
            val subHeader = SubHeaderComponent()
            rootComposite.add(subHeader)
            val body = BodyComponent()
            rootComposite.addWithWeight(body, weightRatio = 1f)
            val input = InputComponent()
            rootComposite.add(input)
            val settings = SettingComponent()
            rootComposite.add(settings)
            rootComposite.Render()
        }
    }
}