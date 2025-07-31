import androidx.compose.ui.awt.ComposePanel
import javax.swing.JFrame
import javax.swing.SwingUtilities

fun main() {
    SwingUtilities.invokeLater {
        val frame = object : JFrame("Terminal Compose") {
            init {
                defaultCloseOperation = EXIT_ON_CLOSE
                isResizable = true
            }
        }

        val panel = ComposePanel().apply {
            setContent {
                MainView()
            }
        }

        frame.contentPane.add(panel)
        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.isVisible = true

        // Agora sim: força maximização após tornar visível
        frame.extendedState = JFrame.MAXIMIZED_BOTH
    }
}
