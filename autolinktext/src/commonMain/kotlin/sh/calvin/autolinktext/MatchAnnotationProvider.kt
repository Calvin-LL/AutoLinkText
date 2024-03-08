package sh.calvin.autolinktext

/**
 * A function that provides an annotation for a match
 * Should return a string that will be used as the URL for the match
 * If the function returns null, the match will not be annotated
 */
typealias MatchAnnotationProvider<T> = (match: TextMatchResult<T>) -> String?

object MatchAnnotationProviderDefaults {
    val NoAnnotation: MatchAnnotationProvider<*> = { null }

    /**
     * A [MatchAnnotationProvider] that uses the matched text as the URL
     * This lets screen readers know that the text is clickable, but doesn't provide a different URL
     */
    val Verbatim: MatchAnnotationProvider<*> = { it.matchedText }
}
