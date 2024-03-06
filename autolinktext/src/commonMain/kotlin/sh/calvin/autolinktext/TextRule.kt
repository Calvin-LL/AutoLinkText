package sh.calvin.autolinktext

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration

interface ContextData

val NullContextData: ContextData = object : ContextData {}

interface TextRuleDefaultsInterface {
    @NotForAndroid
    fun webUrl(contextData: ContextData = NullContextData) = TextRule(
        textMatcher = TextMatcherDefaults.webUrl(contextData),
        onClick = MatchClickHandlerDefaults.webUrl(contextData),
    )

    @NotForAndroid
    fun emailAddress(contextData: ContextData = NullContextData) = TextRule(
        textMatcher = TextMatcherDefaults.emailAddress(contextData),
        onClick = MatchClickHandlerDefaults.emailAddress(contextData),
    )

    @NotForAndroid
    fun phoneNumber(contextData: ContextData = NullContextData) = TextRule(
        textMatcher = TextMatcherDefaults.phoneNumber(contextData),
        onClick = MatchClickHandlerDefaults.phoneNumber(contextData),
    )

    @NotForAndroid
    fun defaultList(contextData: ContextData = NullContextData) = listOf(
        webUrl(contextData),
        emailAddress(contextData),
        phoneNumber(contextData),
    )

    @OptIn(NotForAndroid::class)
    @Composable
    fun webUrl() = webUrl(NullContextData)

    @OptIn(NotForAndroid::class)
    @Composable
    fun emailAddress() = emailAddress(NullContextData)

    @OptIn(NotForAndroid::class)
    @Composable
    fun phoneNumber() = phoneNumber(NullContextData)

    @OptIn(NotForAndroid::class)
    @Composable
    fun defaultList() = defaultList(NullContextData)
}

internal expect fun getTextRuleDefaults(): TextRuleDefaultsInterface

val TextRuleDefaults = getTextRuleDefaults()

/**
 * A rule to match text and apply style and click handling.
 *
 * @param textMatcher The matcher to find the text in the input.
 * @param matchStyleProvider The provider to provide style for the matched text.
 * @param onClick The handler to handle click events on the matched text.
 */
class TextRule<T>(
    val textMatcher: TextMatcher<T>,
    val matchStyleProvider: MatchStyleProvider<T>,
    val onClick: MatchClickHandler<T> = {}
) {
    constructor(
        textMatcher: TextMatcher<T>,
        matchStyle: SpanStyle? = SpanStyle(
            textDecoration = TextDecoration.Underline
        ),
        onClick: MatchClickHandler<T> = {}
    ) : this(
        textMatcher = textMatcher,
        matchStyleProvider = { matchStyle },
        onClick = onClick
    )

    fun copy() = TextRule(
        textMatcher = textMatcher,
        matchStyleProvider = matchStyleProvider,
        onClick = onClick
    )

    fun copy(
        textMatcher: TextMatcher<T> = this.textMatcher,
        matchClickHandler: MatchClickHandler<T> = this.onClick
    ) = TextRule(
        textMatcher = textMatcher,
        matchStyleProvider = matchStyleProvider,
        onClick = matchClickHandler
    )

    fun copy(
        textMatcher: TextMatcher<T> = this.textMatcher,
        matchStyleProvider: MatchStyleProvider<T> = this.matchStyleProvider,
        matchClickHandler: MatchClickHandler<T> = this.onClick
    ) = TextRule(
        textMatcher = textMatcher,
        matchStyleProvider = matchStyleProvider,
        onClick = matchClickHandler
    )

    fun copy(
        textMatcher: TextMatcher<T> = this.textMatcher,
        matchStyle: SpanStyle? = null,
        matchClickHandler: MatchClickHandler<T> = this.onClick
    ) = TextRule(
        textMatcher = textMatcher,
        matchStyleProvider = { matchStyle },
        onClick = matchClickHandler
    )
}

internal fun <T> Collection<TextRule<T>>.getAllMatches(text: String): List<TextMatchResult<T>> =
    flatMap { rule ->
        rule.textMatcher.apply(text).map { match ->
            TextMatchResult(rule, text, match)
        }
    }.pruneOverlaps()

// from https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/text/util/Linkify.java;l=737;drc=4f9480b13d3cab52255608ac5913922ca4269ac5
private fun <T> List<TextMatchResult<T>>.pruneOverlaps(): List<TextMatchResult<T>> {
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

internal fun <T> List<TextMatchResult<T>>.annotateString(text: String): AnnotatedString {
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

fun <T> Collection<TextRule<T>>.annotateString(text: String): AnnotatedString {
    val matches = getAllMatches(text)
    return matches.annotateString(text)
}
