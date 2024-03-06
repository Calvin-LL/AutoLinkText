package sh.calvin.autolinktext

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TextMatchResultTest {
    @Test
    fun shouldReturnCorrectSubstring() {
        val rule = TextRule(TextMatcher.StringMatcher("test"))
        val text = "123abc456def789"
        val match = TextMatchResult.TextMatch(rule, text, 3, 6)

        val result = text.slice(match)

        assertEquals("abc", result)
    }

    @Test
    fun shouldConstructFromSimpleTextMatchResult() {
        val rule = TextRule(TextMatcher.StringMatcher("test"))
        val text = "123abc456def789"

        val textMatchResult = SimpleTextMatchResult.TextMatch(3, 6)
        val result = TextMatchResult.fromSimpleTextMatchResult(
            textMatchResult,
            rule,
            text
        )

        assertTrue(result is TextMatchResult.TextMatch)
        assertEquals(rule, result.rule)
        assertEquals(text, result.fullText)
        assertEquals(3, result.start)
        assertEquals(6, result.end)
    }
}