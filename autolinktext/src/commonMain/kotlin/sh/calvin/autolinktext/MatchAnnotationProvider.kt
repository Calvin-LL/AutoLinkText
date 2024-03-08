package sh.calvin.autolinktext

/**
 * A function that provides an annotation for a match
 * Should return a string that will be used as the URL for the match
 * If the function returns null, the match will not be annotated
 */
typealias MatchAnnotationProvider<T> = (match: TextMatchResult<T>) -> String?

interface MatchAnnotationProviderDefaultsInterface {
    val NoAnnotation: MatchAnnotationProvider<*>
        get() = { null }

    /**
     * A [MatchAnnotationProvider] that uses the matched text as the URL
     * This lets screen readers know that the text is clickable, but doesn't provide a different URL
     */
    val Verbatim: MatchAnnotationProvider<*>
        get() = { it.matchedText }

    val WebUrl: MatchAnnotationProvider<*>
        get() = { it.matchedText }

    val EmailAddress: MatchAnnotationProvider<*>
        get() = { it.matchedText }

    val PhoneNumber: MatchAnnotationProvider<*>
        get() = { it.matchedText }
}

internal expect fun getMatchAnnotationProviderDefaults(): MatchAnnotationProviderDefaultsInterface

val MatchAnnotationProviderDefaults = getMatchAnnotationProviderDefaults()
