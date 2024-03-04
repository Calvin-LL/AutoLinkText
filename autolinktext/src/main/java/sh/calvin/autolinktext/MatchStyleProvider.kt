package sh.calvin.autolinktext

sealed class MatchStyle {
    class ParagraphStyle(val style: androidx.compose.ui.text.ParagraphStyle) : MatchStyle()
    class SpanStyle(val style: androidx.compose.ui.text.SpanStyle) : MatchStyle()
}

fun interface MatchStyleProvider {
    fun provideStyle(text: String, match: TextMatchResult): MatchStyle?
}

object MatchStyleProviderDefaults {
    val NoOp = MatchStyleProvider { _, _ -> null }
}