package sh.calvin.autolinktext

import androidx.compose.ui.text.SpanStyle

typealias MatchStyleProvider = (match: TextMatchResult) -> SpanStyle?