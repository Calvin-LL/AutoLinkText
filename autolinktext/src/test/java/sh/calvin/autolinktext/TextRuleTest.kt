package sh.calvin.autolinktext

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class TextRuleTest {
    @Test
    fun `A list of matcher should handle overlaps`() {
        val text = "123abc456def789"
        val rule = listOf(
            TextRule(
                textMatcher = TextMatcher.RegexMatcher(Regex("123a")),
            ),
            TextRule(
                textMatcher = TextMatcher.RegexMatcher(Regex("abc")),
            ),
        )
        val matches = rule.getAllMatches(text)

        assertEquals(1, matches.size)
        assertEquals(0, matches[0].start)
        assertEquals(4, matches[0].end)
    }

    @Test
    fun `emails should be matched`() {
        val context = RuntimeEnvironment.getApplication()

        val text = "test someone@example.com test"
        val rule = listOf(TextRule.emailAddress(context))
        val matches = rule.getAllMatches(text)

        assertEquals(1, matches.size)
        assertEquals("someone@example.com", text.slice(matches[0]))
    }

    @Test
    fun `phone numbers should be matched`() {
        val context = RuntimeEnvironment.getApplication()

        val text = "test 123-456-7890 test"
        val rule = listOf(TextRule.phoneNumber(context))
        val matches = rule.getAllMatches(text)

        assertEquals(1, matches.size)
        assertEquals("123-456-7890", text.slice(matches[0]))
    }

    @Test
    fun `web urls should be matched`() {
        val context = RuntimeEnvironment.getApplication()

        val text = "test http://example.com test example.com"
        val rule = listOf(TextRule.webUrl(context))
        val matches = rule.getAllMatches(text)

        assertEquals(2, matches.size)
        assertEquals("http://example.com", text.slice(matches[0]))
        assertEquals("example.com", text.slice(matches[1]))
    }
}