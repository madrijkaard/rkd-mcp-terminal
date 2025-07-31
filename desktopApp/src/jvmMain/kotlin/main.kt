import androidx.compose.ui.awt.ComposePanel
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.SwingUtilities

fun main() {
    SwingUtilities.invokeLater {
        val frame = object : JFrame("Terminal Compose") {
            init {
                preferredSize = Dimension(800, 600)
                defaultCloseOperation = EXIT_ON_CLOSE
                isResizable = true
            }
        }

        val panel = ComposePanel().apply {
            preferredSize = frame.preferredSize
            setContent {
                MainView()
            }
        }

        frame.contentPane.add(panel)
        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.isVisible = true
    }
}
