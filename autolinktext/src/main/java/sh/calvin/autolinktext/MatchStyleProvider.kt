package sh.calvin.autolinktext

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration

typealias MatchStyleProvider = (TextMatchResult) -> SpanStyle?

object MatchStyleProviderDefaults {
    val NoOp: MatchStyleProvider = { null }
    val Underline: MatchStyleProvider = {
        SpanStyle(textDecoration = TextDecoration.Underline)
    }
}