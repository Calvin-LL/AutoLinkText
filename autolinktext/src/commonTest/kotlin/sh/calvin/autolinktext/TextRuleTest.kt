package sh.calvin.autolinktext

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.LinkAnnotation
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
        assertEquals(4, matches[0].endExclusive)
        assertEquals("123a", text.slice(matches[0]))
        assertEquals(rules[0], matches[0].rule)
    }

    @Test
    fun testSecondaryConstructor() {
        val textMatcher = TextMatcher.RegexMatcher(Regex("123a"))
        val clickHandler: MatchClickHandler<Any?> = { }
        val urlProvider: MatchUrlProvider<Any?> = { null }
        val rule = TextRule(
            textMatcher = textMatcher,
            styles = null,
            onClick = clickHandler,
            urlProvider = urlProvider,
        )

        assertEquals(textMatcher, rule.textMatcher)
        assertNull(rule.stylesProvider)
        assertEquals(clickHandler, rule.onClick)
        assertEquals(urlProvider, rule.urlProvider)
    }

    @Test
    fun testCopy1() {
        val textMatcher = TextMatcher.RegexMatcher(Regex("123a"))
        val stylesProvider: MatchStylesProvider<Any?> = { null }
        val clickHandler: MatchClickHandler<Any?> = { }
        val urlProvider: MatchUrlProvider<Any?> = { null }
        val rule = TextRule(
            textMatcher = textMatcher,
            stylesProvider = stylesProvider,
            onClick = clickHandler,
            urlProvider = urlProvider,
        )
        val copy = rule.copy()

        assertEquals(rule.textMatcher, copy.textMatcher)
        assertEquals(rule.stylesProvider, copy.stylesProvider)
        assertEquals(rule.onClick, copy.onClick)
        assertEquals(rule.urlProvider, copy.urlProvider)
    }

    @Test
    fun testCopy2() {
        val textMatcher1 = TextMatcher.RegexMatcher(Regex("123a"))
        val stylesProvider1: MatchStylesProvider<Any?> = { null }
        val clickHandler1: MatchClickHandler<Any?> = { }
        val urlProvider1: MatchUrlProvider<Any?> = { null }
        val rule1 = TextRule(
            textMatcher = textMatcher1,
            stylesProvider = stylesProvider1,
            onClick = clickHandler1,
            urlProvider = urlProvider1,
        )

        val textMatcher2 = TextMatcher.RegexMatcher(Regex("abc"))
        val stylesProvider2: MatchStylesProvider<Any?> = { null }
        val clickHandler2: MatchClickHandler<Any?> = { }
        val urlProvider2: MatchUrlProvider<Any?> = { null }
        val rule2 = rule1.copy(
            textMatcher = textMatcher2,
            stylesProvider = stylesProvider2,
            onClick = clickHandler2,
            urlProvider = urlProvider2,
        )

        assertEquals(textMatcher2, rule2.textMatcher)
        assertEquals(stylesProvider2, rule2.stylesProvider)
        assertEquals(clickHandler2, rule2.onClick)
        assertEquals(urlProvider2, rule2.urlProvider)
    }

    @Test
    fun testCopy3() {
        val textMatcher1 = TextMatcher.RegexMatcher(Regex("123a"))
        val stylesProvider1: MatchStylesProvider<Any?> = { null }
        val clickHandler1: MatchClickHandler<Any?> = { }
        val rule1 = TextRule(
            textMatcher = textMatcher1,
            stylesProvider = stylesProvider1,
            onClick = clickHandler1,
        )

        val textMatcher2 = TextMatcher.RegexMatcher(Regex("abc"))
        val clickHandler2: MatchClickHandler<Any?> = { }
        val urlProvider2: MatchUrlProvider<Any?> = { null }
        val rule2 = rule1.copy(
            textMatcher = textMatcher2,
            styles = null,
            onClick = clickHandler2,
            urlProvider = urlProvider2,
        )

        assertEquals(textMatcher2, rule2.textMatcher)
        assertNotEquals(stylesProvider1, rule2.stylesProvider)
        assertEquals(clickHandler2, rule2.onClick)
        assertEquals(urlProvider2, rule2.urlProvider)
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
