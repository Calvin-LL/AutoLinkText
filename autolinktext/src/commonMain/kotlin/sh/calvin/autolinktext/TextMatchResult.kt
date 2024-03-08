package sh.calvin.autolinktext

class TextMatchResult<T>(
    /**
     * The rule that was used to match the text
     */
    val rule: TextRule<T>,
    /**
     * The text that was matched
     */
    val matchedText: String,
    start: Int,
    endExclusive: Int,
    data: T,
) : SimpleTextMatchResult<T>(start, endExclusive, data) {
    constructor(
        rule: TextRule<T>,
        /**
         * The full text that was matched against
         */
        fullText: String,
        match: SimpleTextMatchResult<T>,
    ) : this(rule, fullText.slice(match), match.start, match.endExclusive, match.data)
}

fun TextMatchResult(rule: TextRule<Nothing?>, fullText: String, start: Int, end: Int) =
    TextMatchResult(rule, fullText, start, end, null)
