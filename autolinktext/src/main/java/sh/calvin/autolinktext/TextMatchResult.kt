package sh.calvin.autolinktext

import kotlin.text.MatchResult
import java.util.regex.Matcher

sealed class TextMatchResult(
    val rule: TextRule,
    val text: String,
) {
    companion object {
        fun fromSimpleTextMatchResult(
            simpleTextMatchResult: SimpleTextMatchResult,
            rule: TextRule,
            text: String
        ) = when (simpleTextMatchResult) {
            is SimpleTextMatchResult.TextMatch -> TextMatch(
                rule,
                text,
                simpleTextMatchResult.start,
                simpleTextMatchResult.end
            )

            is SimpleTextMatchResult.MatcherMatch -> MatcherMatch(
                rule,
                text,
                simpleTextMatchResult.matcher,
                simpleTextMatchResult.start,
                simpleTextMatchResult.end
            )

            is SimpleTextMatchResult.RegexMatch -> RegexMatch(
                rule,
                text,
                simpleTextMatchResult.matchResult
            )
        }
    }

    /**
     * The index of the first character in s that was
     * matched by the pattern - inclusive
     */
    abstract val start: Int

    /**
     * The index of the last character in s that was
     * matched - exclusive
     */
    abstract val end: Int

    val matchedText: String
        get() = text.slice(this)

    class TextMatch(rule: TextRule, text: String, override val start: Int, override val end: Int) :
        TextMatchResult(rule, text)

    class MatcherMatch(
        rule: TextRule,
        text: String,
        val matcher: Matcher,
        override val start: Int,
        override val end: Int
    ) :
        TextMatchResult(rule, text)

    class RegexMatch(
        rule: TextRule,
        text: String,
        val matchResult: MatchResult,
    ) :
        TextMatchResult(rule, text) {
        override val start: Int
            get() = matchResult.range.first
        override val end: Int
            get() = matchResult.range.last + 1
    }
}

fun String.slice(match: TextMatchResult): String {
    return substring(match.start, match.end)
}