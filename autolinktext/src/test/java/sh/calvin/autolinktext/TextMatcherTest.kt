package sh.calvin.autolinktext

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.regex.Pattern

class TextMatcherTest {
    @Test
    fun `PatternMatcher should match the pattern in the text`() {
        val pattern = Pattern.compile("\\d+") // match digits
        val text = "123abc456def789"
        val matcher = TextMatcher.PatternMatcher(pattern)
        val matches = matcher.apply(text)

        assertEquals(3, matches.size)
        assertEquals(0, matches[0].start)
        assertEquals(3, matches[0].end)
        assertEquals(6, matches[1].start)
        assertEquals(9, matches[1].end)
        assertEquals(12, matches[2].start)
        assertEquals(15, matches[2].end)

        assertEquals("123", text.slice(matches[0]))
        assertEquals("456", text.slice(matches[1]))
        assertEquals("789", text.slice(matches[2]))
    }

    @Test
    fun `RegexMatcher should match the regex in the text`() {
        val regex = Regex("\\d+") // match digits
        val text = "123abc456def789"
        val matcher = TextMatcher.RegexMatcher(regex)
        val matches = matcher.apply(text)

        assertEquals(3, matches.size)
        assertEquals(0, matches[0].start)
        assertEquals(3, matches[0].end)
        assertEquals(6, matches[1].start)
        assertEquals(9, matches[1].end)
        assertEquals(12, matches[2].start)
        assertEquals(15, matches[2].end)

        assertEquals("123", text.slice(matches[0]))
        assertEquals("456", text.slice(matches[1]))
        assertEquals("789", text.slice(matches[2]))
    }

    @Test
    fun `StringMatcher should match all strings in the text`() {
        val string = "abc"
        val text = "123abc456abc789"
        val matcher = TextMatcher.StringMatcher(string)
        val matches = matcher.apply(text)

        assertEquals(2, matches.size)
        assertEquals(3, matches[0].start)
        assertEquals(6, matches[0].end)
        assertEquals(9, matches[1].start)
        assertEquals(12, matches[1].end)

        assertEquals("abc", text.slice(matches[0]))
        assertEquals("abc", text.slice(matches[1]))
    }

    @Test
    fun `FunctionMatcher should match based on the function`() {
        val text = "123abc456def789"
        val matcher = TextMatcher.FunctionMatcher {
            listOf(
                SimpleTextMatchResult.TextMatch(3, 6),
                SimpleTextMatchResult.TextMatch(6, 9)
            )
        }
        val matches = matcher.apply(text)

        assertEquals(2, matches.size)
        assertEquals(3, matches[0].start)
        assertEquals(6, matches[0].end)
        assertEquals(6, matches[1].start)
        assertEquals(9, matches[1].end)
    }
}