package sh.calvin.autolinktext

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun AutoLinkText(
    text: String,
    modifier: Modifier = Modifier,
    textRules: List<TextRule> = TextRulesDefault,
    style: TextStyle = TextStyle.Default,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
) {
    val annotatedString = remember(text, textRules) {
        textRules.annotateString(text)
    }
    val textRulesMap = remember(textRules) {
        textRules.associateBy { it.name }
    }
    ClickableText(
        text = annotatedString,
        modifier = modifier,
        style = style,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = onTextLayout,
    ) {
        val clickedAnnotation = annotatedString.getStringAnnotations(it, it).first()
        val textRule = textRulesMap[clickedAnnotation.tag]
//        textRule?.matchClickHandler?.handleClick(text)
    }
}