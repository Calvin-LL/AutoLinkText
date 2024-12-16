package sh.calvin.autolinktext

import androidx.compose.ui.text.TextLinkStyles

typealias MatchStylesProvider<T> = (match: TextMatchResult<T>) -> TextLinkStyles?
