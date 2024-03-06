import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import sh.calvin.autolinktext.demo.App

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
