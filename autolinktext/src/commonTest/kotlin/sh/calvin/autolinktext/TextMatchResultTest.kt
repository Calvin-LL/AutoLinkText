package sh.calvin.autolinktext

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TextMatchResultTest {
    @Test
    fun shouldReturnCorrectSubstring() {
        val rule = TextRule(TextMatcher.StringMatcher("test"))
        val text = "123abc456def789"
        val match = TextMatchResult(rule, text, 3, 6)

        val result = text.slice(match)

        assertEquals("abc", result)
    }

    @Test
    fun shouldConstructWithoutData() {
        val rule = TextRule(TextMatcher.StringMatcher("test"))
        val text = "123abc456def789"

        val result = TextMatchResult(
            rule,
            text,
            3,
            6,
        )

        assertEquals(rule, result.rule)
        assertEquals(3, result.start)
        assertEquals(6, result.endExclusive)
        assertNull(result.data)
    }

    @Test
    fun shouldConstructFromSimpleTextMatchResultWithoutData() {
        val rule = TextRule(TextMatcher.StringMatcher("test"))
        val text = "123abc456def789"

        val textMatchResult = SimpleTextMatchResult(3, 6)
        val result = TextMatchResult(
            rule,
            text,
            textMatchResult,
        )

        assertEquals(rule, result.rule)
        assertEquals(3, result.start)
        assertEquals(6, result.endExclusive)
        assertNull(result.data)
    }

    @Test
    fun shouldConstructFromSimpleTextMatchResultWithData() {
        val rule = TextRule(TextMatcher.RegexMatcher(Regex("abc")))
        val text = "123abc456def789"
        val matchResult = Regex("abc").find(text)!!

        val textMatchResult = SimpleTextMatchResult(3, 6, matchResult)
        val result = TextMatchResult(
            rule,
            text,
            textMatchResult,
        )

        assertEquals(rule, result.rule)
        assertEquals(3, result.start)
        assertEquals(6, result.endExclusive)
        assertEquals(matchResult, result.data)
    }
}
