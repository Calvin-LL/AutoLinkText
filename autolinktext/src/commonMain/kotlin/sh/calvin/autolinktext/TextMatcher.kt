package sh.calvin.autolinktext

import androidx.compose.runtime.Composable

expect object TextMatcherDefaults {
    @NotForAndroid
    fun webUrl(contextData: ContextData): TextMatcher

    @NotForAndroid
    fun emailAddress(contextData: ContextData): TextMatcher

    @NotForAndroid
    fun phoneNumber(contextData: ContextData): TextMatcher

    @Composable
    fun webUrl(): TextMatcher

    @Composable
    fun emailAddress(): TextMatcher

    @Composable
    fun phoneNumber(): TextMatcher
}

/**
 * A [TextMatcher] is used to match text in a string.
 */
sealed class TextMatcher {
    abstract fun apply(text: String): List<SimpleTextMatchResult>

    /**
     * A [TextMatcher] that matches a [Regex] in the text.
     */
    class RegexMatcher(
        val regex: Regex,
        val matchFilter: MatchFilter = MatchFilterDefaults.NoOp,
    ) : TextMatcher() {
        override fun apply(text: String): List<SimpleTextMatchResult> {
            val matches = mutableListOf<SimpleTextMatchResult>()
            regex.findAll(text).forEach {
                val result = SimpleTextMatchResult.RegexMatch(it)
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
        val matchFilter: MatchFilter = MatchFilterDefaults.NoOp,
    ) : TextMatcher() {
        override fun apply(text: String): List<SimpleTextMatchResult> {
            val matches = mutableListOf<SimpleTextMatchResult>()
            var index = text.indexOf(string)
            while (index != -1) {
                val result = SimpleTextMatchResult.TextMatch(index, index + string.length)
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
    class FunctionMatcher(val function: (String) -> List<SimpleTextMatchResult>) : TextMatcher() {
        override fun apply(text: String): List<SimpleTextMatchResult> {
            return function(text)
        }
    }
}