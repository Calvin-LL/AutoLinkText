package sh.calvin.autolinktext

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class TextRuleTest {
    @Test
    fun testAListOfMatcherShouldHandleOverlaps() {
        val text = "123abc456def789"
        val rules = listOf(
            TextRule(
                textMatcher = TextMatcher.RegexMatcher(Regex("123a")),
            ),
            TextRule(
                textMatcher = TextMatcher.RegexMatcher(Regex("abc")),
            ),
        )
        val matches = rules.getAllMatches(text)

        assertEquals(1, matches.size)
        assertEquals(0, matches[0].start)
        assertEquals(4, matches[0].end)
        assertEquals("123a", text.slice(matches[0]))
        assertEquals(rules[0], matches[0].rule)
    }

    @Test
    fun testCopy1() {
        val textMatcher = TextMatcher.RegexMatcher(Regex("123a"))
        val styleProvider: MatchStyleProvider = { null }
        val clickHandler: MatchClickHandler = { }
        val rule = TextRule(
            textMatcher = textMatcher,
            matchStyleProvider = styleProvider,
            onClick = clickHandler
        )
        val copy = rule.copy()

        assertEquals(rule.textMatcher, copy.textMatcher)
        assertEquals(rule.matchStyleProvider, copy.matchStyleProvider)
        assertEquals(rule.onClick, copy.onClick)
    }

    @Test
    fun testCopy2() {
        val textMatcher1 = TextMatcher.RegexMatcher(Regex("123a"))
        val styleProvider1: MatchStyleProvider = { null }
        val clickHandler1: MatchClickHandler = { }
        val rule1 = TextRule(
            textMatcher = textMatcher1,
            matchStyleProvider = styleProvider1,
            onClick = clickHandler1
        )

        val textMatcher2 = TextMatcher.RegexMatcher(Regex("abc"))
        val styleProvider2: MatchStyleProvider = { null }
        val clickHandler2: MatchClickHandler = { }
        val rule2 = rule1.copy(
            textMatcher = textMatcher2,
            matchStyleProvider = styleProvider2,
            matchClickHandler = clickHandler2
        )

        assertEquals(textMatcher2, rule2.textMatcher)
        assertEquals(styleProvider2, rule2.matchStyleProvider)
        assertEquals(clickHandler2, rule2.onClick)
    }

    @Test
    fun testCopy3() {
        val textMatcher1 = TextMatcher.RegexMatcher(Regex("123a"))
        val styleProvider1: MatchStyleProvider = { null }
        val clickHandler1: MatchClickHandler = { }
        val rule1 = TextRule(
            textMatcher = textMatcher1,
            matchStyleProvider = styleProvider1,
            onClick = clickHandler1
        )

        val textMatcher2 = TextMatcher.RegexMatcher(Regex("abc"))
        val clickHandler2: MatchClickHandler = { }
        val rule2 = rule1.copy(
            textMatcher = textMatcher2,
            matchStyle = null,
            matchClickHandler = clickHandler2
        )

        assertEquals(textMatcher2, rule2.textMatcher)
        assertNotEquals(styleProvider1, rule2.matchStyleProvider)
        assertEquals(clickHandler2, rule2.onClick)
    }

    @Test
    fun testCopy4() {
        val textMatcher1 = TextMatcher.RegexMatcher(Regex("123a"))
        val styleProvider1: MatchStyleProvider = { null }
        val clickHandler1: MatchClickHandler = { }
        val rule1 = TextRule(
            textMatcher = textMatcher1,
            matchStyleProvider = styleProvider1,
            onClick = clickHandler1
        )

        val textMatcher2 = TextMatcher.RegexMatcher(Regex("abc"))
        val clickHandler2: MatchClickHandler = { }
        val rule2 = rule1.copy(
            textMatcher = textMatcher2,
            matchClickHandler = clickHandler2
        )

        assertEquals(textMatcher2, rule2.textMatcher)
        assertEquals(styleProvider1, rule2.matchStyleProvider)
        assertEquals(clickHandler2, rule2.onClick)
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
            TextRuleDefaults.emailAddress(NullContextData)
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
            TextRuleDefaults.phoneNumber(NullContextData)
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
        assertEquals("+1 (604) 555-7890", text.slice(matches[4]))
        assertEquals(rules[2], matches[4].rule)
        assertEquals("604-555-7890", text.slice(matches[5]))
        assertEquals(rules[2], matches[5].rule)
    }
}