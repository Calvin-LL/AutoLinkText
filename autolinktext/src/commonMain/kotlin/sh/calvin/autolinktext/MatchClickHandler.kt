package sh.calvin.autolinktext

import androidx.compose.runtime.Composable

typealias MatchClickHandler<T> = (match: TextMatchResult<T>) -> Unit

expect object MatchClickHandlerDefaults {
    @NotForAndroid
    fun webUrl(contextData: ContextData): MatchClickHandler<Any?>

    @NotForAndroid
    fun emailAddress(contextData: ContextData): MatchClickHandler<Any?>

    @NotForAndroid
    fun phoneNumber(contextData: ContextData): MatchClickHandler<Any?>

    @Composable
    fun webUrl(): MatchClickHandler<Any?>

    @Composable
    fun emailAddress(): MatchClickHandler<Any?>

    @Composable
    fun phoneNumber(): MatchClickHandler<Any?>
}

/**
 * only allow digits and "+"
 */
internal fun normalizePhoneNumber(phone: String): String {
    return phone.filter { it.isDigit() || it == '+' }
}