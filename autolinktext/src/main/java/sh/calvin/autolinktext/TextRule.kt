package sh.calvin.autolinktext

import android.content.Context
import androidx.compose.ui.text.AnnotatedString
import java.util.UUID

class TextRule(
    val textMatcher: TextMatcher,
    val matchFilter: MatchFilter = MatchFilterDefaults.NoOp,
    val matchStyleProvider: MatchStyleProvider = MatchStyleProviderDefaults.Underline,
    val matchClickHandler: MatchClickHandler = MatchClickHandlerDefaults.NoOp
) {
    companion object {
        fun WebUrl(context: Context) = TextRule(
            textMatcher = TextMatcher.WebUrl,
            matchFilter = MatchFilterDefaults.WebUrls,
            matchClickHandler = MatchClickHandlerDefaults.WebUrl(context),
        )

        fun EmailAddress(context: Context) = TextRule(
            textMatcher = TextMatcher.EmailAddress,
            matchClickHandler = MatchClickHandlerDefaults.EmailAddress(context),
        )

        fun PhoneNumber(context: Context) = TextRule(
            textMatcher = TextMatcher.PhoneNumber(context),
            matchClickHandler = MatchClickHandlerDefaults.PhoneNumber(context),
        )
    }
}

fun TextRulesDefault(context: Context) = listOf(
    TextRule.WebUrl(context),
    TextRule.EmailAddress(context),
    TextRule.PhoneNumber(context),
)

internal fun Collection<TextRule>.getAllMatches(text: String): List<TextMatchResult> {
    val matches = mutableListOf<TextMatchResult>()
    forEach { rule ->
        val ruleMatches = rule.textMatcher.apply(text)
        ruleMatches.forEach { match ->
            if (rule.matchFilter.acceptMatch(text, match)) {
                matches.add(match.apply {
                    this.rule = rule
                })
            }
        }
    }
    return matches
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
        when (val style = match.rule?.matchStyleProvider?.provideStyle(text, match)) {
            is MatchStyle.ParagraphStyle -> annotatedString.addStyle(
                style.style,
                match.start,
                match.end
            )

            is MatchStyle.SpanStyle -> annotatedString.addStyle(
                style.style,
                match.start,
                match.end
            )

            null -> {}
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
