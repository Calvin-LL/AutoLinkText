package sh.calvin.autolinktext

import kotlin.text.MatchResult
import java.util.regex.Matcher

sealed class SimpleTextMatchResult {
    companion object {
        fun fromTextMatchResult(textMatchResult: TextMatchResult) =
            when (textMatchResult) {
                is TextMatchResult.TextMatch -> TextMatch(
                    textMatchResult.start,
                    textMatchResult.end
                )

                is TextMatchResult.MatcherMatch -> MatcherMatch(
                    textMatchResult.matcher,
                    textMatchResult.start,
                    textMatchResult.end
                )

                is TextMatchResult.RegexMatch -> RegexMatch(textMatchResult.matchResult)
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

    class TextMatch(override val start: Int, override val end: Int) :
        SimpleTextMatchResult()

    class MatcherMatch(
        val matcher: Matcher,
        override val start: Int,
        override val end: Int
    ) :
        SimpleTextMatchResult()

    class RegexMatch(
        val matchResult: MatchResult,
    ) :
        SimpleTextMatchResult() {
        override val start: Int
            get() = matchResult.range.start
        override val end: Int
            get() = matchResult.range.endInclusive + 1
    }
}

fun String.slice(match: SimpleTextMatchResult): String {
    return substring(match.start, match.end)
}