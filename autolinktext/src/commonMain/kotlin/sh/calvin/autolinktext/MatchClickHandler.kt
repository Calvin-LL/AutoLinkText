package sh.calvin.autolinktext

import androidx.compose.runtime.Composable

typealias MatchClickHandler = (match: TextMatchResult) -> Unit

expect object MatchClickHandlerDefaults {
    @NotForAndroid
    fun webUrl(contextData: ContextData): MatchClickHandler

    @NotForAndroid
    fun emailAddress(contextData: ContextData): MatchClickHandler

    @NotForAndroid
    fun phoneNumber(contextData: ContextData): MatchClickHandler

    @Composable
    fun webUrl(): MatchClickHandler

    @Composable
    fun emailAddress(): MatchClickHandler

    @Composable
    fun phoneNumber(): MatchClickHandler
}

/**
 * only allow digits and "+"
 */
internal fun normalizePhoneNumber(phone: String): String {
    return phone.filter { it.isDigit() || it == '+' }
}