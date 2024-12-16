package sh.calvin.autolinktext

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import kotlin.test.Test
import kotlin.test.assertEquals

class TextRuleTest {
    @Test
    fun testAListOfMatcherShouldHandleOverlaps() {
        val text = "123abc456def789"
        val rules = listOf(
            TextRule.Styleable(
                textMatcher = TextMatcher.RegexMatcher(Regex("123a")),
                style = SpanStyle()
            ),
            TextRule.Styleable(
                textMatcher = TextMatcher.RegexMatcher(Regex("abc")),
                style = SpanStyle()
            ),
        )
        val matches = rules.getAllMatches(text)

        assertEquals(1, matches.size)
        assertEquals(0, matches[0].start)
        assertEquals(4, matches[0].endExclusive)
        assertEquals("123a", text.slice(matches[0]))
        assertEquals(rules[0], matches[0].rule)
    }

    @OptIn(NotForAndroid::class)
    @IgnoreAndroid
    @Test
    fun emailsShouldBeMatched() {
        val text = "test someone@example.com test"
        val rules = listOf(TextRuleDefaults.emailAddress(NullContextData))
        val matches = rules.getAllMatches(text)

        assertEquals(1, matches.size)
        assertEquals("someone@example.com", text.slice(matches[0]))
    }

    @OptIn(NotForAndroid::class)
    @IgnoreAndroid
    @Test
    fun phoneNumbersShouldBeMatched() {
        val text = "test 123-456-7890 test"
        val rules = listOf(TextRuleDefaults.phoneNumber(NullContextData))
        val matches = rules.getAllMatches(text)

        assertEquals(1, matches.size)
        assertEquals("123-456-7890", text.slice(matches[0]))
    }

    @OptIn(NotForAndroid::class)
    @IgnoreAndroid
    @Test
    fun webUrlsShouldBeMatched() {
        val text = "test http://example.com test example.com"
        val rules = listOf(TextRuleDefaults.webUrl(NullContextData))
        val matches = rules.getAllMatches(text)

        assertEquals(2, matches.size)
        assertEquals("http://example.com", text.slice(matches[0]))
        assertEquals("example.com", text.slice(matches[1]))
    }

    @OptIn(NotForAndroid::class)
    @IgnoreAndroid
    @Test
    fun emailShouldNotBeConfusedWithWebUrl() {
        val text = "test someone@example.com test"
        val rules = listOf(
            TextRuleDefaults.webUrl(NullContextData),
            TextRuleDefaults.emailAddress(NullContextData),
        )
        val matches = rules.getAllMatches(text)

        assertEquals(1, matches.size)
        assertEquals("someone@example.com", text.slice(matches[0]))
        assertEquals(rules[1], matches[0].rule)
    }

    @OptIn(NotForAndroid::class)
    @IgnoreAndroid
    @Test
    fun shouldMatchComplexText() {
        val text = "Visit https://www.google.com\n" +
                "Visit www.google.com\n" +
                "Email test@example.com\n" +
                "Call 6045557890\n" +
                "Call +1 (604) 555-7890\n" +
                "Call 604-555-7890\n"
        val rules = listOf(
            TextRuleDefaults.webUrl(NullContextData),
            TextRuleDefaults.emailAddress(NullContextData),
            TextRuleDefaults.phoneNumber(NullContextData),
        )
        val matches = rules.getAllMatches(text)

        assertEquals(6, matches.size)
        assertEquals("https://www.google.com", text.slice(matches[0]))
        assertEquals(rules[0], matches[0].rule)
        assertEquals("www.google.com", text.slice(matches[1]))
        assertEquals(rules[0], matches[1].rule)
        assertEquals("test@example.com", text.slice(matches[2]))
        assertEquals(rules[1], matches[2].rule)
        assertEquals("6045557890", text.slice(matches[3]))
        assertEquals(rules[2], matches[3].rule)
        assertEqualsOneOf(listOf("+1 (604) 555-7890", "(604) 555-7890"), text.slice(matches[4]))
        assertEquals(rules[2], matches[4].rule)
        assertEquals("604-555-7890", text.slice(matches[5]))
        assertEquals(rules[2], matches[5].rule)
    }

    @OptIn(NotForAndroid::class, ExperimentalTextApi::class)
    @IgnoreAndroid
    @Test
    fun testAnnotateString() {
        val text = "Visit https://www.google.com\n" +
                "Visit www.google.com\n" +
                "Email test@example.com\n" +
                "Call 6045557890\n" +
                "Call +1 (604) 555-7890\n" +
                "Call 604-555-7890\n"
        val rules = listOf(
            TextRuleDefaults.webUrl(NullContextData),
            TextRuleDefaults.emailAddress(NullContextData),
            TextRuleDefaults.phoneNumber(NullContextData),
        )
        val matches = rules.getAllMatches(text)
        val annotatedString = matches.annotateString(text)

        fun getUrlAtMatch(index: Int) = (annotatedString.getLinkAnnotations(
            matches[index].start, matches[index].endExclusive
        ).first().item as? LinkAnnotation.Url)?.url

        assertEquals("https://www.google.com", getUrlAtMatch(0))
        assertEqualsOneOf(
            listOf("https://www.google.com", "http://www.google.com"),
            getUrlAtMatch(1) ?: ""
        )
        assertEquals("mailto:test@example.com", getUrlAtMatch(2))
        assertEquals("tel:6045557890", getUrlAtMatch(3))
        assertEquals("tel:+16045557890", getUrlAtMatch(4))
        assertEquals("tel:6045557890", getUrlAtMatch(5))
    }
}
