package sh.calvin.autolinktext

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun AutoLinkText(
    text: String,
    modifier: Modifier = Modifier,
    textRules: List<TextRule> = TextRule.defaultList(LocalContext.current),
    style: TextStyle = TextStyle.Default,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
) {
    val matches = remember(text, textRules) {
        textRules.getAllMatches(text).pruneOverlaps()
    }
    val annotatedString = remember(matches) {
        matches.annotateString(text)
    }
    ClickableText(
        text = annotatedString,
        modifier = modifier,
        style = style,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = onTextLayout,
    ) { offset ->
        val match = matches.find { it.start <= offset && it.end > offset }
        match?.rule?.matchClickHandler?.handleClick(match)
    }
}