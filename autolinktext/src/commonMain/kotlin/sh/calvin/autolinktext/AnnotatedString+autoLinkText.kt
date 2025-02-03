package sh.calvin.autolinktext

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.style.TextDecoration

/**
 * Creates an [AnnotatedString] with all the text turned into clickable links based on the
 * provided [TextRule]s.
 *
 * @param text The text to auto-link.
 * @param textRules A list of rules to match the text and handle click events. Each rule
 * contains a [TextMatcher], a [MatchStylesProvider] and a [MatchClickHandler]. The [TextMatcher]
 * is used to find the matches in the text. The [MatchStylesProvider] is used to provide style
 * for the matched text. The [MatchClickHandler] is used to handle click events on the matched
 * text.
 * @param defaultLinkStyles The default [TextLinkStyles] to apply to the auto-linked text.
 */
@Composable
fun AnnotatedString.Companion.rememberAutoLinkText(
    text: String,
    textRules: Collection<TextRule<Any?>> = TextRuleDefaults.defaultList(),
    defaultLinkStyles: TextLinkStyles? = TextLinkStyles(
        style = SpanStyle(
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline
        ),
    ),
): AnnotatedString {
    val matches = remember(text, textRules) {
        textRules.map {
            if (it.stylesProvider == null && defaultLinkStyles != null)
                it.copy(styles = defaultLinkStyles)
            else
                it
        }
            .getAllMatches(text)
    }
    val annotatedString = remember(text, matches) {
        matches.annotateString(text)
    }

    return annotatedString
}
