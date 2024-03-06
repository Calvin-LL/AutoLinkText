package sh.calvin.autolinktext

open class SimpleTextMatchResult<out T>(
    val start: Int,
    val end: Int,
    val data: T
)

fun SimpleTextMatchResult(start: Int, end: Int) = SimpleTextMatchResult(start, end, null)

fun String.slice(match: SimpleTextMatchResult<*>): String {
    return substring(match.start, match.end)
}