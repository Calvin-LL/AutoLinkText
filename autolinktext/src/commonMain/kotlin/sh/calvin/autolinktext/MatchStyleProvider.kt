package sh.calvin.autolinktext

import androidx.compose.ui.text.SpanStyle

typealias MatchStyleProvider<T> = (match: TextMatchResult<T>) -> SpanStyle?