package sh.calvin.autolinktext

import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.util.Locale

actual fun getMatcherDefaults() = object : TextMatcherDefaultsInterface {
    @NotForAndroid
    override fun phoneNumber(contextData: ContextData): TextMatcher<Any?> {
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
}
