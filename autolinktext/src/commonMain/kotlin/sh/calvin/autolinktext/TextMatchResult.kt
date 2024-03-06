package sh.calvin.autolinktext

sealed class TextMatchResult(
    /**
     * The rule that was used to match the text
     */
    val rule: TextRule,
    /**
     * The full text that was matched against
     */
    val fullText: String,
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

    /**
     * The text that was matched
     */
    val matchedText: String
        get() = fullText.slice(this)

    class TextMatch(rule: TextRule, text: String, override val start: Int, override val end: Int) :
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