package sh.calvin.autolinktext

import androidx.compose.ui.text.AnnotatedString
import java.util.UUID

class TextRule(
    val name: String = UUID.randomUUID().toString(),
    val textMatcher: TextMatcher,
    val matchFilter: MatchFilter = MatchFilterDefaults.NoOp,
    val matchStyleProvider: MatchStyleProvider = MatchStyleProviderDefaults.NoOp,
    val matchClickHandler: MatchClickHandler = MatchClickHandlerDefaults.NoOp
) {
    companion object {
        // TODO: open Intent when clicked
        val WebUrl = TextRule("WebUrl", TextMatcher.WebUrl, MatchFilterDefaults.WebUrls)
        val EmailAddress = TextRule("EmailAddress", TextMatcher.EmailAddress)
        val PhoneNumber = TextRule("PhoneNumber", TextMatcher.PhoneNumber)
    }
}

val TextRulesDefault = listOf(
    TextRule.WebUrl,
    TextRule.EmailAddress,
    TextRule.PhoneNumber
)

private fun Collection<TextRule>.validate() {
    require(all { it.name.isNotBlank() }) { "All TextRules must have a non-blank name" }
    require(
        groupingBy { it.name }.eachCount()
            .all { it.value == 1 }) { "All TextRules must have a unique name" }
}

private class MatchResult(
    val textRule: TextRule,
    val match: TextMatchResult,
)

private fun Collection<TextRule>.getAllMatches(text: String): List<MatchResult> {
    val matches = mutableListOf<MatchResult>()
    forEach { rule ->
        val ruleMatches = rule.textMatcher.apply(text)
        ruleMatches.forEach { match ->
            if (rule.matchFilter.acceptMatch(text, match)) {
                matches.add(MatchResult(rule, match))
            }
        }
    }
    return matches
}

// from https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/text/util/Linkify.java;l=737;drc=4f9480b13d3cab52255608ac5913922ca4269ac5
private fun List<MatchResult>.pruneOverlaps(): List<MatchResult> {
    val sortedList = sortedWith { a, b ->
        if (a.match.start < b.match.start) {
            return@sortedWith -1
        }

        if (a.match.start > b.match.start) {
            return@sortedWith 1
        }

        if (a.match.end < b.match.end) {
            return@sortedWith 1
        }

        if (a.match.end > b.match.end) {
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
        if (a.match.start <= b.match.start && a.match.end > b.match.start) {
            if (b.match.end <= a.match.end) {
                remove = i + 1
            } else if (a.match.end - a.match.start > b.match.end - b.match.start) {
                remove = i + 1
            } else if (a.match.end - a.match.start < b.match.end - b.match.start) {
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

//private fun List<MatchResult>.annotateString(text: String): Pair<MatchResult, AnnotatedString> {
//    map { it.textRule }.validate()
//
//    val annotatedString = AnnotatedString.Builder(text)
//    forEach { match ->
//        when (val style = match.textRule.matchStyleProvider.provideStyle(text, match.match)) {
//            is MatchStyle.ParagraphStyle -> annotatedString.addStyle(
//                style.style,
//                match.match.start,
//                match.match.end
//            )
//
//            is MatchStyle.SpanStyle -> annotatedString.addStyle(
//                style.style,
//                match.match.start,
//                match.match.end
//            )
//
//            null -> {}
//        }
//        annotatedString.addStringAnnotation(
//            match.textRule.name,
//            text.slice(match.match),
//            match.match.start,
//            match.match.end
//        )
//    }
//
//    return annotatedString.toAnnotatedString()
//}

fun Collection<TextRule>.annotateString(text: String): AnnotatedString {
    validate()

    val annotatedString = AnnotatedString.Builder(text)
    val matches = getAllMatches(text).pruneOverlaps()
    matches.forEach { match ->
        when (val style = match.textRule.matchStyleProvider.provideStyle(text, match.match)) {
            is MatchStyle.ParagraphStyle -> annotatedString.addStyle(
                style.style,
                match.match.start,
                match.match.end
            )

            is MatchStyle.SpanStyle -> annotatedString.addStyle(
                style.style,
                match.match.start,
                match.match.end
            )

            null -> {}
        }
        annotatedString.addStringAnnotation(
            match.textRule.name,
            text.slice(match.match),
            match.match.start,
            match.match.end
        )
    }

    return annotatedString.toAnnotatedString()
}
