import androidx.compose.ui.window.ComposeUIViewController
import sh.calvin.autolinktext.demo.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
