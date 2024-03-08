package sh.calvin.autolinktext

import kotlin.test.Test
import kotlin.test.assertEquals

class TextMatcherTest {
    @Test
    fun testRegexMatcher() {
        val regex = Regex("\\d+") // match digits
        val text = "123abc456def789"
        val matcher = TextMatcher.RegexMatcher(regex)
        val matches = matcher.apply(text)

        assertEquals(3, matches.size)
        assertEquals(0, matches[0].start)
        assertEquals(3, matches[0].endExclusive)
        assertEquals(6, matches[1].start)
        assertEquals(9, matches[1].endExclusive)
        assertEquals(12, matches[2].start)
        assertEquals(15, matches[2].endExclusive)

        assertEquals("123", text.slice(matches[0]))
        assertEquals("456", text.slice(matches[1]))
        assertEquals("789", text.slice(matches[2]))
    }

    @Test
    fun testStringMatcher() {
        val string = "abc"
        val text = "123abc456abc789"
        val matcher = TextMatcher.StringMatcher(string)
        val matches = matcher.apply(text)

        assertEquals(2, matches.size)
        assertEquals(3, matches[0].start)
        assertEquals(6, matches[0].endExclusive)
        assertEquals(9, matches[1].start)
        assertEquals(12, matches[1].endExclusive)

        assertEquals("abc", text.slice(matches[0]))
        assertEquals("abc", text.slice(matches[1]))
    }

    @Test
    fun testFunctionMatcher() {
        val text = "123abc456def789"
        val matcher = TextMatcher.FunctionMatcher {
            listOf(
                SimpleTextMatchResult(3, 6),
                SimpleTextMatchResult(6, 9)
            )
        }
        val matches = matcher.apply(text)

        assertEquals(2, matches.size)
        assertEquals(3, matches[0].start)
        assertEquals(6, matches[0].endExclusive)
        assertEquals(6, matches[1].start)
        assertEquals(9, matches[1].endExclusive)
    }
}
