package sh.calvin.autolinktext

import kotlin.test.Test
import kotlin.test.assertEquals

class SimpleTextMatchResultTest {
    @Test
    fun shouldReturnCorrectSubstring() {
        val text = "123abc456def789"
        val match = SimpleTextMatchResult(3, 6)

        val result = text.slice(match)

        assertEquals("abc", result)
    }
}
