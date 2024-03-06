package sh.calvin.autolinktext

import androidx.compose.runtime.Composable

actual object TextMatcherDefaults {
    actual fun webUrl(contextData: ContextData): TextMatcher<Any?> =
        TextMatcher.RegexMatcher(BackUpRegex.WebUrl, MatchFilterDefaults.WebUrls)

    actual fun emailAddress(contextData: ContextData): TextMatcher<Any?> =
        TextMatcher.RegexMatcher(BackUpRegex.Email)

    actual fun phoneNumber(contextData: ContextData): TextMatcher<Any?> =
        TextMatcher.RegexMatcher(BackUpRegex.PhoneNumber, MatchFilterDefaults.PhoneNumber)

    @Composable
    actual fun webUrl() = webUrl(NullContextData)

    @Composable
    actual fun emailAddress() = emailAddress(NullContextData)

    @Composable
    actual fun phoneNumber() = phoneNumber(NullContextData)
}