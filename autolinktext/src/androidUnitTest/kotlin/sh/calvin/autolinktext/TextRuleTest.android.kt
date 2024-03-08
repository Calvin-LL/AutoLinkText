package sh.calvin.autolinktext

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class AndroidTextRuleTest {
    @OptIn(NotForAndroid::class)
    @Test
    fun emailsShouldBeMatched() {
        val context = RuntimeEnvironment.getApplication()
        val contextData = AndroidContextData(context)

        val text = "test someone@example.com test"
        val rule = listOf(TextRuleDefaults.emailAddress(contextData))
        val matches = rule.getAllMatches(text)

        assertEquals(1, matches.size)
        assertEquals("someone@example.com", text.slice(matches[0]))
    }

    @OptIn(NotForAndroid::class)
    @Test
    fun phoneNumbersShouldBeMatched() {
        val context = RuntimeEnvironment.getApplication()
        val contextData = AndroidContextData(context)

        val text = "test 123-456-7890 test"
        val rule = listOf(TextRuleDefaults.phoneNumber(contextData))
        val matches = rule.getAllMatches(text)

        assertEquals(1, matches.size)
        assertEquals("123-456-7890", text.slice(matches[0]))
    }

    @OptIn(NotForAndroid::class)
    @Test
    fun webUrlsShouldBeMatched() {
        val context = RuntimeEnvironment.getApplication()
        val contextData = AndroidContextData(context)

        val text = "test http://example.com test example.com"
        val rule = listOf(TextRuleDefaults.webUrl(contextData))
        val matches = rule.getAllMatches(text)

        assertEquals(2, matches.size)
        assertEquals("http://example.com", text.slice(matches[0]))
        assertEquals("example.com", text.slice(matches[1]))
    }

    @OptIn(NotForAndroid::class)
    @Test
    fun emailShouldNotBeConfusedWithWebUrl() {
        val context = RuntimeEnvironment.getApplication()
        val contextData = AndroidContextData(context)

        val text = "test someone@example.com test"
        val rules = listOf(
            TextRuleDefaults.webUrl(contextData),
            TextRuleDefaults.emailAddress(contextData),
        )
        val matches = rules.getAllMatches(text)

        assertEquals(1, matches.size)
        assertEquals("someone@example.com", text.slice(matches[0]))
        assertEquals(rules[1], matches[0].rule)
    }

    @OptIn(NotForAndroid::class)
    @Test
    fun shouldMatchComplexText() {
        val context = RuntimeEnvironment.getApplication()
        val contextData = AndroidContextData(context)

        val text = "Visit https://www.google.com\n" +
                "Visit www.google.com\n" +
                "Email test@example.com\n" +
                "Call 6045557890\n" +
                "Call +1 (604) 555-7890\n" +
                "Call 604-555-7890\n"
        val rules = listOf(
            TextRuleDefaults.webUrl(contextData),
            TextRuleDefaults.emailAddress(contextData),
            TextRuleDefaults.phoneNumber(contextData),
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
