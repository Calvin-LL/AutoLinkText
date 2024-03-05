package sh.calvin.autolinktext

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration

sealed class MatchStyle {
    class ParagraphStyle(val style: androidx.compose.ui.text.ParagraphStyle) : MatchStyle()
    class SpanStyle(val style: androidx.compose.ui.text.SpanStyle) : MatchStyle()
}

fun interface MatchStyleProvider {
    fun provideStyle(match: TextMatchResult): MatchStyle?
}

object MatchStyleProviderDefaults {
    val NoOp = MatchStyleProvider { null }
    val Underline = MatchStyleProvider {
        MatchStyle.SpanStyle(
            SpanStyle(textDecoration = TextDecoration.Underline)
        )
    }
}