package sh.calvin.autolinktext

import androidx.compose.runtime.Composable

typealias MatchClickHandler<T> = (match: TextMatchResult<T>) -> Unit

interface MatchClickHandlerDefaultsInterface {
    @NotForAndroid
    fun webUrl(contextData: ContextData): MatchClickHandler<Any?>

    @NotForAndroid
    fun emailAddress(contextData: ContextData): MatchClickHandler<Any?>

    @NotForAndroid
    fun phoneNumber(contextData: ContextData): MatchClickHandler<Any?>

    @OptIn(NotForAndroid::class)
    @Composable
    fun webUrl() = webUrl(NullContextData)

    @OptIn(NotForAndroid::class)
    @Composable
    fun emailAddress() = emailAddress(NullContextData)

    @OptIn(NotForAndroid::class)
    @Composable
    fun phoneNumber() = phoneNumber(NullContextData)
}

internal expect fun getMatchClickHandlerDefaults(): MatchClickHandlerDefaultsInterface

val MatchClickHandlerDefaults = getMatchClickHandlerDefaults()

/**
 * only allow digits and "+"
 */
internal fun normalizePhoneNumber(phone: String): String {
    return phone.filter { it.isDigit() || it == '+' }
}
