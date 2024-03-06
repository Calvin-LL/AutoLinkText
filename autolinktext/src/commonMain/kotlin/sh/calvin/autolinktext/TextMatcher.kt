package sh.calvin.autolinktext

import androidx.compose.runtime.Composable

expect object TextMatcherDefaults {
    @NotForAndroid
    fun webUrl(contextData: ContextData): TextMatcher<Any?>

    @NotForAndroid
    fun emailAddress(contextData: ContextData): TextMatcher<Any?>

    @NotForAndroid
    fun phoneNumber(contextData: ContextData): TextMatcher<Any?>

    @Composable
    fun webUrl(): TextMatcher<Any?>

    @Composable
    fun emailAddress(): TextMatcher<Any?>

    @Composable
    fun phoneNumber(): TextMatcher<Any?>
}

/**
 * A [TextMatcher] is used to match text in a string.
 */
sealed class TextMatcher<out T> {
    abstract fun apply(text: String): List<SimpleTextMatchResult<T>>

    /**
     * A [TextMatcher] that matches a [Regex] in the text.
     */
    class RegexMatcher(
        val regex: Regex,
        val matchFilter: MatchFilter<MatchResult> = MatchFilterDefaults.NoOp,
    ) : TextMatcher<MatchResult>() {
        override fun apply(text: String): List<SimpleTextMatchResult<MatchResult>> {
            val matches = mutableListOf<SimpleTextMatchResult<MatchResult>>()
            regex.findAll(text).forEach {
                val result = SimpleTextMatchResult(
                    start = it.range.first,
                    end = it.range.last + 1,
                    it
                )
                if (matchFilter(text, result)) {
                    matches.add(result)
                }
            }
            return matches
        }
    }

    /**
     * A [TextMatcher] that matches all instances of a string in the text.
     */
    class StringMatcher(
        val string: String,
        val matchFilter: MatchFilter<Nothing?> = MatchFilterDefaults.NoOp,
    ) : TextMatcher<Nothing?>() {
        override fun apply(text: String): List<SimpleTextMatchResult<Nothing?>> {
            val matches = mutableListOf<SimpleTextMatchResult<Nothing?>>()
            var index = text.indexOf(string)
            while (index != -1) {
                val result = SimpleTextMatchResult(index, index + string.length)
                if (matchFilter(text, result)) {
                    matches.add(result)
                }
                index = text.indexOf(string, index + 1)
            }
            return matches
        }
    }

    /**
     * A [TextMatcher] that matches based on a function.
     */
    class FunctionMatcher<out T>(val function: (String) -> List<SimpleTextMatchResult<T>>) : TextMatcher<T>() {
        override fun apply(text: String): List<SimpleTextMatchResult<T>> {
            return function(text)
        }
    }
}