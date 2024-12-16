package sh.calvin.autolinktext

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles

interface ContextData

val NullContextData: ContextData = object : ContextData {}

interface TextRuleDefaultsInterface {
    @NotForAndroid
    fun webUrl(contextData: ContextData = NullContextData) = TextRule.Url(
        textMatcher = TextMatcherDefaults.webUrl(contextData),
        urlProvider = MatchUrlProviderDefaults.WebUrl,
    )

    @NotForAndroid
    fun emailAddress(contextData: ContextData = NullContextData) = TextRule.Url(
        textMatcher = TextMatcherDefaults.emailAddress(contextData),
        urlProvider = MatchUrlProviderDefaults.EmailAddress,
    )

    @NotForAndroid
    fun phoneNumber(contextData: ContextData = NullContextData) = TextRule.Url(
        textMatcher = TextMatcherDefaults.phoneNumber(contextData),
        urlProvider = MatchUrlProviderDefaults.PhoneNumber,
    )

    @NotForAndroid
    fun defaultList(contextData: ContextData = NullContextData) = listOf(
        webUrl(contextData),
        emailAddress(contextData),
        phoneNumber(contextData),
    )

    @OptIn(NotForAndroid::class)
    @Composable
    fun webUrl() = webUrl(NullContextData)

    @OptIn(NotForAndroid::class)
    @Composable
    fun emailAddress() = emailAddress(NullContextData)

    @OptIn(NotForAndroid::class)
    @Composable
    fun phoneNumber() = phoneNumber(NullContextData)

    @OptIn(NotForAndroid::class)
    @Composable
    fun defaultList() = defaultList(NullContextData)
}

internal expect fun getTextRuleDefaults(): TextRuleDefaultsInterface

val TextRuleDefaults = getTextRuleDefaults()

/**
 * A rule to match text and apply style and click handling.
 *
 * @param textMatcher The matcher to find the text in the input.
 * @param stylesProvider The provider to provide style for the matched text.
 */
sealed class TextRule<T> private constructor(
    open val textMatcher: TextMatcher<T>,
    open val stylesProvider: MatchStylesProvider<T>?,
) {
    /**
     * A rule to match text and apply style and url.
     *
     * @param textMatcher The matcher to find the text in the input.
     * @param stylesProvider The provider to provide style for the matched text.
     * @param urlProvider The provider to provide urls for the matched text.
     */
    data class Url<T>(
        override val textMatcher: TextMatcher<T>,
        override val stylesProvider: MatchStylesProvider<T>? = null,
        val urlProvider: MatchUrlProvider<T> = MatchUrlProviderDefaults.Verbatim
    ) : TextRule<T>(textMatcher, stylesProvider) {
        fun copy(
            textMatcher: TextMatcher<T> = this.textMatcher,
            styles: TextLinkStyles?,
            urlProvider: MatchUrlProvider<T> = this.urlProvider,
        ) = Url(
            textMatcher = textMatcher,
            stylesProvider = styles?.let { s -> { s } },
            urlProvider = urlProvider,
        )

        override fun copy(
            textMatcher: TextMatcher<T>,
            styles: TextLinkStyles,
        ) = Url(
            textMatcher = textMatcher,
            stylesProvider = styles.let { s -> { s } },
            urlProvider = this.urlProvider,
        )
    }

    /**
     * A rule to match text and apply style and click handling.
     *
     * @param textMatcher The matcher to find the text in the input.
     * @param stylesProvider The provider to provide style for the matched text.
     * @param onClick The handler to handle click events on the matched text.
     */
    data class Clickable<T>(
        override val textMatcher: TextMatcher<T>,
        override val stylesProvider: MatchStylesProvider<T>? = null,
        val onClick: MatchClickHandler<T>,
    ) : TextRule<T>(textMatcher, stylesProvider) {
        fun copy(
            textMatcher: TextMatcher<T> = this.textMatcher,
            styles: TextLinkStyles?,
            onClick: MatchClickHandler<T> = this.onClick,
        ) = Clickable(
            textMatcher = textMatcher,
            stylesProvider = styles?.let { s -> { s } },
            onClick = onClick,
        )

        override fun copy(
            textMatcher: TextMatcher<T>,
            styles: TextLinkStyles,
        ) = Clickable(
            textMatcher = textMatcher,
            stylesProvider = styles.let { s -> { s } },
            onClick = this.onClick,
        )
    }

    /**
     * A rule to match text and apply style.
     *
     * @param textMatcher The matcher to find the text in the input.
     * @param stylesProvider The provider to provide style for the matched text.
     */
    data class Styleable<T>(
        override val textMatcher: TextMatcher<T>,
        override val stylesProvider: MatchStylesProvider<T>,
    ) : TextRule<T>(textMatcher, stylesProvider) {
        constructor(
            textMatcher: TextMatcher<T>,
            styles: TextLinkStyles,
        ) : this(textMatcher, { styles })

        constructor(
            textMatcher: TextMatcher<T>,
            style: SpanStyle,
        ) : this(textMatcher, { TextLinkStyles(style) })

        override fun copy(
            textMatcher: TextMatcher<T>,
            styles: TextLinkStyles,
        ) = Styleable(
            textMatcher = textMatcher,
            stylesProvider = styles.let { s -> { s } },
        )
    }

    abstract fun copy(
        textMatcher: TextMatcher<T> = this.textMatcher,
        styles: TextLinkStyles,
    ): TextRule<T>
}

internal fun <T> Collection<TextRule<T>>.getAllMatches(text: String): List<TextMatchResult<T>> =
    flatMap { rule ->
        rule.textMatcher.apply(text).map { match ->
            TextMatchResult(rule, text, match)
        }
    }.pruneOverlaps()

// from https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/text/util/Linkify.java;l=737;drc=4f9480b13d3cab52255608ac5913922ca4269ac5
private fun <T> List<TextMatchResult<T>>.pruneOverlaps(): List<TextMatchResult<T>> {
    val sortedList = sortedWith { a, b ->
        if (a.start < b.start) {
            return@sortedWith -1
        }

        if (a.start > b.start) {
            return@sortedWith 1
        }

        if (a.endExclusive < b.endExclusive) {
            return@sortedWith 1
        }

        if (a.endExclusive > b.endExclusive) {
            return@sortedWith -1
        }

        return@sortedWith 0
    }.toMutableList()

    var len: Int = sortedList.size
    var i = 0

    while (i < len - 1) {
        val a = sortedList[i]
        val b = sortedList[i + 1]
        var remove = -1
        if (a.start <= b.start && a.endExclusive > b.start) {
            if (b.endExclusive <= a.endExclusive) {
                remove = i + 1
            } else if (a.endExclusive - a.start > b.endExclusive - b.start) {
                remove = i + 1
            } else if (a.endExclusive - a.start < b.endExclusive - b.start) {
                remove = i
            }
            if (remove != -1) {
                sortedList.removeAt(remove)
                len--
                continue
            }
        }
        i++
    }

    return sortedList
}

internal fun List<TextMatchResult<*>>.annotateString(text: String): AnnotatedString {
    val annotatedString = AnnotatedString.Builder(text)
    forEach { match ->
        annotatedString.addMatch(match)
    }

    return annotatedString.toAnnotatedString()
}

fun <T> AnnotatedString.Builder.addMatch(
    match: TextMatchResult<T>
) {
    val styles = match.rule.stylesProvider?.invoke(match)
    when (match.rule) {
        is TextRule.Url -> {
            val url = match.rule.urlProvider(match)

            if (url != null) {
                addLink(
                    url = LinkAnnotation.Url(
                        url = url,
                        styles = styles,
                    ),
                    start = match.start,
                    end = match.endExclusive,
                )
            }
        }

        is TextRule.Clickable -> {
            addLink(
                clickable = LinkAnnotation.Clickable(
                    tag = "",
                    styles = styles,
                    linkInteractionListener = {
                        match.rule.onClick(match)
                    }
                ),
                start = match.start,
                end = match.endExclusive,
            )
        }

        is TextRule.Styleable -> {
            styles?.style?.let {
                addStyle(
                    it,
                    match.start,
                    match.endExclusive
                )
            }
        }
    }
}

fun <T> Collection<TextRule<T>>.annotateString(text: String): AnnotatedString {
    val matches = getAllMatches(text)
    return matches.annotateString(text)
}