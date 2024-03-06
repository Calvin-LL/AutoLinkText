package sh.calvin.autolinktext

import androidx.compose.runtime.Composable
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.util.Locale

actual object TextMatcherDefaults {
    actual fun webUrl(contextData: ContextData): TextMatcher<Any?> {
        return TextMatcher.RegexMatcher(BackUpRegex.WebUrl, MatchFilterDefaults.WebUrls)
    }

    actual fun emailAddress(contextData: ContextData): TextMatcher<Any?> {
        return TextMatcher.RegexMatcher(BackUpRegex.Email)
    }

    actual fun phoneNumber(contextData: ContextData): TextMatcher<Any?> {
        return TextMatcher.FunctionMatcher { text ->
            val phoneUtil = PhoneNumberUtil.getInstance()
            val regionCode = Locale.getDefault().country
            val matches = phoneUtil.findNumbers(
                text,
                regionCode, PhoneNumberUtil.Leniency.POSSIBLE, Long.MAX_VALUE
            )

            matches.mapNotNull {
                val result = SimpleTextMatchResult(it.start(), it.end())
                if (MatchFilterDefaults.PhoneNumber(text, result)) result else null
            }
        }
    }

    @Composable
    actual fun webUrl() = webUrl(NullContextData)

    @Composable
    actual fun emailAddress() = emailAddress(NullContextData)

    @Composable
    actual fun phoneNumber() = phoneNumber(NullContextData)
}