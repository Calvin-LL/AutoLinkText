package sh.calvin.autolinktext

import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

/**
 * A continent version of [BasicText] component to be able to handle click event on the text.
 *
 * This is a shorthand of [BasicText] with [pointerInput] to be able to handle click
 * event easily.
 *
 * @sample androidx.compose.foundation.samples.ClickableText
 *
 * @param text The text to be displayed.
 * @param modifier Modifier to apply to this layout node.
 * @param textRules A list of rules to match the text and handle click events. Each rule
 * contains a [TextMatcher], a [MatchStyleProvider] and a [MatchClickHandler]. The [TextMatcher]
 * is used to find the matches in the text. The [MatchStyleProvider] is used to provide style
 * for the matched text. The [MatchClickHandler] is used to handle click events on the matched
 * text.
 * @param style Style configuration for the text such as color, font, line height etc.
 * @param softWrap Whether the text should break at soft line breaks. If false, the glyphs in the
 * text will be positioned as if there was unlimited horizontal space. If [softWrap] is false,
 * [overflow] and [TextAlign] may have unexpected effects.
 * @param overflow How visual overflow should be handled.
 * @param maxLines An optional maximum number of lines for the text to span, wrapping if
 * necessary. If the text exceeds the given number of lines, it will be truncated according to
 * [overflow] and [softWrap]. If it is not null, then it must be greater than zero.
 * @param onTextLayout Callback that is executed when a new text layout is calculated. A
 * [TextLayoutResult] object that callback provides contains paragraph information, size of the
 * text, baselines and other details. The callback can be used to add additional decoration or
 * functionality to the text. For example, to draw selection around the text.
 * @param onClick Callback that is executed when users click the text. This callback is called
 * with clicked character's offset.
 */
@Composable
fun AutoLinkText(
    text: String,
    modifier: Modifier = Modifier,
    textRules: Collection<TextRule<Any?>> = TextRuleDefaults.defaultList(),
    style: TextStyle = TextStyle.Default,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
) {
    val matches = remember(text, textRules) {
        textRules.getAllMatches(text)
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
        match?.rule?.onClick?.invoke(match)
    }
}