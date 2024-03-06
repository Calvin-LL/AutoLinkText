package sh.calvin.autolinktext

class TextMatchResult<T>(
    /**
     * The rule that was used to match the text
     */
    val rule: TextRule<T>,
    /**
     * The full text that was matched against
     */
    val fullText: String,
    start: Int,
    endExclusive: Int,
    data: T,
) : SimpleTextMatchResult<T>(start, endExclusive, data) {
    constructor(
        rule: TextRule<T>,
        fullText: String,
        match: SimpleTextMatchResult<T>
    ) : this(rule, fullText, match.start, match.endExclusive, match.data)

    val matchedText: String
        get() = fullText.slice(this)
}

fun TextMatchResult(rule: TextRule<Nothing?>, fullText: String, start: Int, end: Int) =
    TextMatchResult(rule, fullText, start, end, null)