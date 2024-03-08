package sh.calvin.autolinktext

import kotlin.test.assertTrue

fun assertEqualsOneOf(expected: List<String>, actual: String) {
    assertTrue(expected.contains(actual), "Expected one of $expected, but was $actual")
}
