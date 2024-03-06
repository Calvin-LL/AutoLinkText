package sh.calvin.autolinktext

import androidx.compose.runtime.Composable

actual object TextMatcherDefaults {
    actual fun webUrl(contextData: ContextData): TextMatcher =
        TextMatcher.RegexMatcher(BackUpRegex.WebUrl, MatchFilterDefaults.WebUrls)

    actual fun emailAddress(contextData: ContextData): TextMatcher =
        TextMatcher.RegexMatcher(BackUpRegex.Email)

    actual fun phoneNumber(contextData: ContextData): TextMatcher =
        TextMatcher.RegexMatcher(BackUpRegex.PhoneNumber, MatchFilterDefaults.PhoneNumber)

    @Composable
    actual fun webUrl(): TextMatcher = webUrl(NullContextData)

    @Composable
    actual fun emailAddress(): TextMatcher = emailAddress(NullContextData)

    @Composable
    actual fun phoneNumber(): TextMatcher = phoneNumber(NullContextData)
}