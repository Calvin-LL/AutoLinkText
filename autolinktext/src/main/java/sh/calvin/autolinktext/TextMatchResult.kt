package sh.calvin.autolinktext

import java.util.regex.Matcher

sealed class TextMatchResult {
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

    class TextMatch(override val start: Int, override val end: Int) : TextMatchResult()

    class MatcherMatch(val matcher: Matcher) : TextMatchResult() {
        override val start: Int
            get() = matcher.start()
        override val end: Int
            get() = matcher.end()
    }

    class RegexMatch(val regexMatchResult: MatchResult) : TextMatchResult() {
        override val start: Int
            get() = regexMatchResult.range.first
        override val end: Int
            get() = regexMatchResult.range.last + 1
    }
}

fun String.slice(match: TextMatchResult): String {
    return substring(match.start, match.end)
}