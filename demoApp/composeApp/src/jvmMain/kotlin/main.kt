import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import sh.calvin.autolinktext.demo.App
import java.awt.Dimension

fun main() = application {
    Window(
        title = "AutoLinkText",
        state = rememberWindowState(width = 600.dp, height = 800.dp),
        onCloseRequest = ::exitApplication,
    ) {
        window.minimumSize = Dimension(350, 600)
        App()
    }
}
