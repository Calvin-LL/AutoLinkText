package sh.calvin.autolinktext

import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class SimpleTextMatchResultTest {
    @Test
    fun `TextMatch should return the correct substring`() {
        val text = "123abc456def789"
        val match = SimpleTextMatchResult.TextMatch(3, 6)

        val result = text.slice(match)

        assertEquals("abc", result)
    }

    @Test
    fun `SimpleTextMatchResult should be constructed from TextMatchResult`() {
        val context = RuntimeEnvironment.getApplication()
        val textMatchResult =
            TextMatchResult.TextMatch(TextRule.emailAddress(context), "test", 3, 6)
        val result = SimpleTextMatchResult.fromTextMatchResult(textMatchResult)

        Assert.assertTrue(result is SimpleTextMatchResult.TextMatch)
        assertEquals(3, result.start)
        assertEquals(6, result.end)
    }
}