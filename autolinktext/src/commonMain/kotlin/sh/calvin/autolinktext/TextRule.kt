package sh.calvin.autolinktext

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString

interface ContextData

val NullContextData: ContextData = object : ContextData {}

@Composable
internal expect fun platformWebUrl(): TextRule

@Composable
internal expect fun platformEmailAddress(): TextRule

@Composable
internal expect fun platformPhoneNumber(): TextRule

@Composable
internal expect fun platformDefaultList(): List<TextRule>

object TextRuleDefaults {
    @NotForAndroid
    fun webUrl(contextData: ContextData = NullContextData) = TextRule(
        textMatcher = TextMatcherDefaults.webUrl(contextData),
        matchClickHandler = MatchClickHandlerDefaults.webUrl(contextData),
    )

    @NotForAndroid
    fun emailAddress(contextData: ContextData = NullContextData) = TextRule(
        textMatcher = TextMatcherDefaults.emailAddress(contextData),
        matchClickHandler = MatchClickHandlerDefaults.emailAddress(contextData),
    )

    @NotForAndroid
    fun phoneNumber(contextData: ContextData = NullContextData) = TextRule(
        textMatcher = TextMatcherDefaults.phoneNumber(contextData),
        matchClickHandler = MatchClickHandlerDefaults.phoneNumber(contextData),
    )

    @Composable
    fun webUrl(): TextRule = platformWebUrl()

    @Composable
    fun emailAddress(): TextRule = platformEmailAddress()

    @Composable
    fun phoneNumber(): TextRule = platformPhoneNumber()

    @Composable
    fun defaultList(): List<TextRule> = platformDefaultList()
}

/**
 * A rule to match text and apply style and click handling.
 *
 * @param textMatcher The matcher to find the text in the input.
 * @param matchStyleProvider The provider to provide style for the matched text.
 * @param matchClickHandler The handler to handle click events on the matched text.
 */
class TextRule(
    val textMatcher: TextMatcher,
    val matchStyleProvider: MatchStyleProvider = MatchStyleProviderDefaults.Underline,
    val matchClickHandler: MatchClickHandler = MatchClickHandlerDefaults.NoOp
) {
    fun copy(
        textMatcher: TextMatcher = this.textMatcher,
        matchStyleProvider: MatchStyleProvider = this.matchStyleProvider,
        matchClickHandler: MatchClickHandler = this.matchClickHandler
    ) = TextRule(
        textMatcher = textMatcher,
        matchStyleProvider = matchStyleProvider,
        matchClickHandler = matchClickHandler
    )
}

internal fun Collection<TextRule>.getAllMatches(text: String): List<TextMatchResult> =
    flatMap { rule ->
        rule.textMatcher.apply(text).map { match ->
            TextMatchResult.fromSimpleTextMatchResult(match, rule, text)
        }
    }.pruneOverlaps()

// from https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/text/util/Linkify.java;l=737;drc=4f9480b13d3cab52255608ac5913922ca4269ac5
private fun List<TextMatchResult>.pruneOverlaps(): List<TextMatchResult> {
    val sortedList = sortedWith { a, b ->
        if (a.start < b.start) {
            return@sortedWith -1
        }

        if (a.start > b.start) {
            return@sortedWith 1
        }

        if (a.end < b.end) {
            return@sortedWith 1
        }

        if (a.end > b.end) {
            return@sortedWith -1
        }

        return@sortedWith 0
    }.toMutableList()

    var len: Int = sortedList.size
    var i = 0

    while (i < len - 1) {
        val a = sortedList[i]
        val b = sortedList[i + 1]
        var remove = -1
        if (a.start <= b.start && a.end > b.start) {
            if (b.end <= a.end) {
                remove = i + 1
            } else if (a.end - a.start > b.end - b.start) {
                remove = i + 1
            } else if (a.end - a.start < b.end - b.start) {
                remove = i
            }
            if (remove != -1) {
                sortedList.removeAt(remove)
                len--
                continue
            }
        }
        i++
    }

    return sortedList
}

internal fun List<TextMatchResult>.annotateString(text: String): AnnotatedString {
    val annotatedString = AnnotatedString.Builder(text)
    forEach { match ->
        val style = match.rule.matchStyleProvider(match)
        if (style != null) {
            annotatedString.addStyle(style, match.start, match.end)
        }
//        annotatedString.addStringAnnotation(
//            match.textRule.name,
//            text.slice(match.match),
//            match.match.start,
//            match.match.end
//        )
    }

    return annotatedString.toAnnotatedString()
}

fun Collection<TextRule>.annotateString(text: String): AnnotatedString {
    val matches = getAllMatches(text)
    return matches.annotateString(text)
}
