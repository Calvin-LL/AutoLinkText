package sh.calvin.autolinktext

/**
 * A function that provides an url for a match
 * Should return a string that will be used as the URL for the match
 * If the function returns null, the match will not be annotated
 */
typealias MatchUrlProvider<T> = (match: TextMatchResult<T>) -> String?

interface MatchUrlProviderDefaultsInterface {
    val NoUrl: MatchUrlProvider<*>
        get() = { null }

    /**
     * A [MatchUrlProvider] that uses the matched text as the URL
     * This lets screen readers know that the text is clickable, but doesn't provide a different URL
     */
    val Verbatim: MatchUrlProvider<*>
        get() = { it.matchedText }

    val WebUrl: MatchUrlProvider<*>
        get() = { it.matchedText }

    val EmailAddress: MatchUrlProvider<*>
        get() = { "mailto:${it.matchedText}" }

    val PhoneNumber: MatchUrlProvider<*>
        get() = { "tel:${normalizePhoneNumber(it.matchedText)}" }
}

internal expect fun getMatchUrlProviderDefaults(): MatchUrlProviderDefaultsInterface

val MatchUrlProviderDefaults = getMatchUrlProviderDefaults()

/**
 * only allow digits and "+"
 */
internal fun normalizePhoneNumber(phone: String): String {
    return phone.filter { it.isDigit() || it == '+' }
}
