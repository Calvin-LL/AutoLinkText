package sh.calvin.autolinktext

open class SimpleTextMatchResult<out T>(
    override val start: Int,
    override val endExclusive: Int,
    val data: T
) : OpenEndRange<Int> {
    fun slice(text: String) = text.substring(start, endExclusive)
}

fun SimpleTextMatchResult(start: Int, end: Int) = SimpleTextMatchResult(start, end, null)

fun String.slice(match: SimpleTextMatchResult<*>) = match.slice(this)