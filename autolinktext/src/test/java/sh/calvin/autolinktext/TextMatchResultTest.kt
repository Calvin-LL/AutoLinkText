package sh.calvin.autolinktext

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class TextMatchResultTest {
    @Test
    fun `TextMatch should return the correct substring`() {
        val context = RuntimeEnvironment.getApplication()
        val text = "123abc456def789"
        val match = TextMatchResult.TextMatch(TextRule.emailAddress(context), text, 3, 6)

        val result = text.slice(match)

        Assert.assertEquals("abc", result)
    }

    @Test
    fun `TextMatchResult should be constructed from SimpleTextMatchResult`() {
        val context = RuntimeEnvironment.getApplication()
        val textMatchResult = SimpleTextMatchResult.TextMatch(3, 6)
        val result = TextMatchResult.fromSimpleTextMatchResult(
            textMatchResult,
            TextRule.emailAddress(context),
            "test"
        )

        Assert.assertTrue(result is TextMatchResult.TextMatch)
        Assert.assertEquals(3, result.start)
        Assert.assertEquals(6, result.end)
    }
}