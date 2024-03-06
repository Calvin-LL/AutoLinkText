package sh.calvin.autolinktext

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SimpleTextMatchResultTest {
    @Test
    fun shouldReturnCorrectSubstring() {
        val text = "123abc456def789"
        val match = SimpleTextMatchResult.TextMatch(3, 6)

        val result = text.slice(match)

        assertEquals("abc", result)
    }

    @Test
    fun shouldConstructFromTextMatchResult() {
        val rule = TextRule(TextMatcher.StringMatcher("test"))
        val text = "123abc456def789"
        val textMatchResult =
            TextMatchResult.TextMatch(rule, text, 3, 6)
        val result = SimpleTextMatchResult.fromTextMatchResult(textMatchResult)

        assertTrue(result is SimpleTextMatchResult.TextMatch)
        assertEquals(3, result.start)
        assertEquals(6, result.end)
    }
}