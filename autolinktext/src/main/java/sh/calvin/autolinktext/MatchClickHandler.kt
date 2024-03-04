package sh.calvin.autolinktext

fun interface MatchClickHandler {
    fun handleClick(s: String, match: TextMatchResult)
}

object MatchClickHandlerDefaults {
    val NoOp = MatchClickHandler { _, _ -> }
}