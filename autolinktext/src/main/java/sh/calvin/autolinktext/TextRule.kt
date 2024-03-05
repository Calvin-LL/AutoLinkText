package sh.calvin.autolinktext

import android.content.Context
import androidx.compose.ui.text.AnnotatedString

class TextRule(
    val textMatcher: TextMatcher,
    val matchFilter: MatchFilter = MatchFilterDefaults.NoOp,
    val matchStyleProvider: MatchStyleProvider = MatchStyleProviderDefaults.Underline,
    val matchClickHandler: MatchClickHandler = MatchClickHandlerDefaults.NoOp
) {
    companion object {
        fun webUrl(context: Context) = TextRule(
            textMatcher = TextMatcher.WebUrl,
            matchFilter = MatchFilterDefaults.WebUrls,
            matchClickHandler = MatchClickHandlerDefaults.webUrl(context),
        )

        fun emailAddress(context: Context) = TextRule(
            textMatcher = TextMatcher.EmailAddress,
            matchClickHandler = MatchClickHandlerDefaults.emailAddress(context),
        )

        fun phoneNumber(context: Context) = TextRule(
            textMatcher = TextMatcher.PhoneNumber(context),
            matchFilter = MatchFilterDefaults.PhoneNumbers,
            matchClickHandler = MatchClickHandlerDefaults.phoneNumber(context),
        )

        fun defaultList(context: Context) = listOf(
            webUrl(context),
            emailAddress(context),
            phoneNumber(context),
        )
    }

    fun copy(
        textMatcher: TextMatcher = this.textMatcher,
        matchFilter: MatchFilter = this.matchFilter,
        matchStyleProvider: MatchStyleProvider = this.matchStyleProvider,
        matchClickHandler: MatchClickHandler = this.matchClickHandler
    ) = TextRule(
        textMatcher = textMatcher,
        matchFilter = matchFilter,
        matchStyleProvider = matchStyleProvider,
        matchClickHandler = matchClickHandler
    )
}

internal fun Collection<TextRule>.getAllMatches(text: String): List<TextMatchResult> = flatMap {
    it.textMatcher.apply(text).mapNotNull { match ->
        val result = TextMatchResult.fromSimpleTextMatchResult(match, it, text)
        if (it.matchFilter(result)) {
            result
        } else {
            null
        }
    }
}

// from https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/text/util/Linkify.java;l=737;drc=4f9480b13d3cab52255608ac5913922ca4269ac5
internal fun List<TextMatchResult>.pruneOverlaps(): List<TextMatchResult> {
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
    val matches = getAllMatches(text).pruneOverlaps()
    return matches.annotateString(text)
}
