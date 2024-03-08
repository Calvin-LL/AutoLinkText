package sh.calvin.autolinktext

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.UrlAnnotation
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
 * @param styleProvider The provider to provide style for the matched text.
 * @param onClick The handler to handle click events on the matched text.
 * @param annotationProvider The provider to provide annotation for the matched text.
 */
class TextRule<T>(
    val textMatcher: TextMatcher<T>,
    val styleProvider: MatchStyleProvider<T>,
    val onClick: MatchClickHandler<T>? = null,
    // if there's no annotation but the text is clickable, use the matched text as the annotation
    val annotationProvider: MatchAnnotationProvider<T> = if (onClick != null)
        MatchAnnotationProviderDefaults.Verbatim
    else
    // if there's no annotation and the text is not clickable, use no annotation
        MatchAnnotationProviderDefaults.NoAnnotation,
) {
    constructor(
        textMatcher: TextMatcher<T>,
        style: SpanStyle? = SpanStyle(
            textDecoration = TextDecoration.Underline
        ),
        onClick: MatchClickHandler<T>? = null,
        // if there's no annotation but the text is clickable, use the matched text as the annotation
        annotationProvider: MatchAnnotationProvider<T> = if (onClick != null)
            MatchAnnotationProviderDefaults.Verbatim
        else
        // if there's no annotation and the text is not clickable, use no annotation
            MatchAnnotationProviderDefaults.NoAnnotation,
    ) : this(
        textMatcher = textMatcher,
        styleProvider = { style },
        onClick = onClick,
        annotationProvider = annotationProvider,
    )

    fun copy(
        textMatcher: TextMatcher<T> = this.textMatcher,
        styleProvider: MatchStyleProvider<T> = this.styleProvider,
        onClick: MatchClickHandler<T>? = this.onClick,
        annotationProvider: MatchAnnotationProvider<T> = this.annotationProvider,
    ) = TextRule(
        textMatcher = textMatcher,
        styleProvider = styleProvider,
        onClick = onClick,
        annotationProvider = annotationProvider,
    )

    fun copy(
        textMatcher: TextMatcher<T> = this.textMatcher,
        style: SpanStyle?,
        onClick: MatchClickHandler<T>? = this.onClick,
        annotationProvider: MatchAnnotationProvider<T> = this.annotationProvider,
    ) = TextRule(
        textMatcher = textMatcher,
        styleProvider = { style },
        onClick = onClick,
        annotationProvider = annotationProvider,
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

        if (a.endExclusive < b.endExclusive) {
            return@sortedWith 1
        }

        if (a.endExclusive > b.endExclusive) {
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
        if (a.start <= b.start && a.endExclusive > b.start) {
            if (b.endExclusive <= a.endExclusive) {
                remove = i + 1
            } else if (a.endExclusive - a.start > b.endExclusive - b.start) {
                remove = i + 1
            } else if (a.endExclusive - a.start < b.endExclusive - b.start) {
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

@OptIn(ExperimentalTextApi::class)
internal fun <T> List<TextMatchResult<T>>.annotateString(text: String): AnnotatedString {
    val annotatedString = AnnotatedString.Builder(text)
    forEach { match ->
        val style = match.rule.styleProvider(match)
        if (style != null) {
            annotatedString.addStyle(style, match.start, match.endExclusive)
        }
        match.rule.annotationProvider(match)?.also { annotation ->
            annotatedString.addUrlAnnotation(
                urlAnnotation = UrlAnnotation(annotation),
                start = match.start,
                end = match.endExclusive,
            )
        }
    }

    return annotatedString.toAnnotatedString()
}

fun <T> Collection<TextRule<T>>.annotateString(text: String): AnnotatedString {
    val matches = getAllMatches(text)
    return matches.annotateString(text)
}
